package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.messages.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Lobby implements Runnable {
    private final Game game;
    private boolean endGame = false;
    private final int playerNumber;
    private final HashMap<ClientHandler,String> clientNames = new HashMap<>();
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    private final ArrayList<String> playersName = new ArrayList<>();

    public Lobby(int playerNumber){
        this.playerNumber = playerNumber;
        game = new Game();
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public int getAddedPlayers(){
        return clients.size();
    }

    public void addPlayer(ClientHandler CH) {
        clients.add(CH);
    }

    @Override
    public void run() {
        for(ClientHandler CH : clients){
            CH.setNameQueue(clientNames);
            CH.setLobbyReady(true);
        }

        for(ClientHandler CH : clients){
            CH.sendStandardMessage(StandardMessages.chooseNickName);
        }
        for(int i=0;i<playerNumber;i++){
            synchronized (clientNames) {
                if(clientNames.get(clients.get(i))==null) {try{clientNames.wait();}catch(Exception e){e.printStackTrace();}}
                playersName.add(clientNames.get(clients.get(i)));
            }
        }

        game.setup(playersName);
        for(int i=0;i<playerNumber;i++){
            synchronized (clients.get(i)) {
                while(!clients.get(i).getMessageReady()){
                    try {
                        clients.get(i).wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message message = clients.get(i).getMessage();
                if(message instanceof SetupLCDiscardMessage){
                    try{
                        game.discardSelectedLC(i, ((SetupLCDiscardMessage) message).getDiscardedLC());
                        clients.get(i).setDiscardLeaderCard(true);
                    }
                    catch(IndexOutOfBoundsException e) {
                        e.printStackTrace();
                        clients.get(i).sendStandardMessage(StandardMessages.leaderCardOutOfBounds);
                        i--;
                    }
                    catch(IllegalArgumentException e) {
                        e.printStackTrace();
                        clients.get(i).sendStandardMessage(StandardMessages.leaderCardWrongFormat);
                        i--;
                    }
                } else{
                    clients.get(i).sendStandardMessage(StandardMessages.wrongObject);
                    i--;
                }
                clients.get(i).setMessageReady(false);
            }
        }

        if (playerNumber == 1) playingSolo();
        else playingMultiplayer();
    }

    private void playingSolo(){

    }

    public void playingMultiplayer(){
        int k=1;
        for(int i=game.getInkwell()+1;i!=game.getInkwell();i=(i+1)%playerNumber) {
            if (k == 1 || k == 2) {
                clients.get(i).sendStandardMessage(StandardMessages.chooseOneResource);
            } else if (k == 3) {
                clients.get(i).sendStandardMessage(StandardMessages.chooseTwoResource);
            }
            synchronized (clients.get(i)) {
                while (!clients.get(i).getMessageReady()) {
                    try {
                        clients.get(i).wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message message = clients.get(i).getMessage();
                if (message instanceof FinishSetupMessage) {
                    game.finishingSetup(i, ((FinishSetupMessage) message).getUserChoice());
                    clients.get(i).setFinishingSetup(true);
                } else {
                    clients.get(i).sendStandardMessage(StandardMessages.wrongObject);
                    k--;
                    i--;
                }
            }
            k++;
        }

        int i = game.getInkwell();
        boolean lastRound = false;
        while(!endGame || !lastRound){
            clients.get(i).setMyTurn(true);
            clients.get(i).sendStandardMessage(StandardMessages.yourTurn);
            boolean turnDone = false;
            while (!turnDone) {
                synchronized (clients.get(i)) {
                    while (!clients.get(i).getMessageReady()) {
                        try {
                            clients.get(i).wait();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Message message = clients.get(i).getMessage();
                    turnDone = handleMessage(message, i);
                }
            }
            if(game.checkEndGame(i)){
                endGame = true;
            }
            clients.get(i).setMyTurn(false);
            i=(i+1)%playerNumber;
            if(endGame && i==game.getInkwell()){
                lastRound = true;
            }
        }

        //checking who won
        game.countVictoryPoints();
        int victoryIndex = 0;
        int max = -1;
        for(int j=0;j<playerNumber;j++){
            if(max < game.getBoard(j).getVictoryPoints()){
                max = game.getBoard(j).getVictoryPoints();
                victoryIndex = j;
            }
        }
        for(ClientHandler CH : clients){
            CH.sendObject(new VictoryMessage(game.getPlayers(victoryIndex),max));
        }

    }

    private boolean handleMessage(Message message,int player){
        //TODO: try catch and send/handle errors
        //Buy resources from market
        if(message instanceof BuyResourcesMessage) {
            try {
                game.BuyMarketResourcesAction(player, ((BuyResourcesMessage) message).getRow(),
                        ((BuyResourcesMessage) message).getN(), ((BuyResourcesMessage) message).getRequestedWMConversion());
            } catch (ActionAlreadyDoneException e) {
                e.printStackTrace();
            }

            clients.get(player).setMessageReady(false);

            //after getting the resources, the user needs to say where he wants to deposit them
            boolean moveActionCorrect = false;
            do {
                while (!clients.get(player).getMessageReady()) {
                    try {
                        clients.get(player).wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Message userChoice = clients.get(player).getMessage();
                clients.get(player).setMessageReady(false);

                if(userChoice instanceof MoveActionMessage) {
                    try {
                        game.moveResources(player, ((MoveActionMessage) userChoice).getUserChoice());
                    } catch (BadRequestException | LeaderCardNotCompatibleException | ResourceErrorException | InvalidPlacementException | IncompatibleResourceException | ResourcesLeftInHandException e) {
                        e.printStackTrace();
                    }
                    game.discardRemainingResources(player); //can't hold resources after getting them from the market
                    moveActionCorrect = true;
                } else {
                    clients.get(player).sendStandardMessage(StandardMessages.wrongObject);
                }
            }while(!moveActionCorrect);
        }

        //move resources around
        else if(message instanceof MoveActionMessage){
            try{
                game.moveResources(player,((MoveActionMessage) message).getUserChoice());
            }catch(IllegalArgumentException | BadRequestException | LeaderCardNotCompatibleException | IncompatibleResourceException | ResourcesLeftInHandException | InvalidPlacementException | ResourceErrorException e){e.printStackTrace();}
        }

        //buy development card
        else if(message instanceof BuyDevelopmentCardMessage){
            try {
                game.BuyDevelopmentCardAction(((BuyDevelopmentCardMessage) message).getC(),((BuyDevelopmentCardMessage) message).getLevel(),
                player,((BuyDevelopmentCardMessage) message).getSlotNumber(),((BuyDevelopmentCardMessage) message).getUserChoice());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //activate production (choice of which one is in the message)
        else if(message instanceof ProductionMessage){
            try {
                game.activateProductionAction(player,((ProductionMessage) message).getUserChoice());
            } catch (ActionAlreadyDoneException | LeaderCardNotCompatibleException | EmptyException | ResourceErrorException | RequirementsNotMetException e) {
                e.printStackTrace();
            }
        }

        //play a leader card
        else if(message instanceof PlayLeaderCardMessage){
            try {
                game.playLeaderCard(player, ((PlayLeaderCardMessage) message).getN());
            } catch (RequirementsNotMetException e) {
                e.printStackTrace();
            }
        }

        //discard a leader card
        else if(message instanceof DiscardLeaderCardMessage){
            game.discardLeaderCard(player,((DiscardLeaderCardMessage) message).getN());
        }

        //turn finished
        else if(message instanceof TurnDoneMessage){
            return true;
        }

        //message not recognized
        else clients.get(player).sendStandardMessage(StandardMessages.wrongObject);

        clients.get(player).setMessageReady(false);
        return false;
    }
}
