package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.Resources;

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

        //TODO: discard leader card and request for extra resources (finishingSetup)
        game.setup(playersName);
        for(int i=0;i<playerNumber;i++){
            //richiedi le leader card che vuole scartare
            //writeObject con le carte pescate; readObject con quelle scelte
            int[] LCDiscardChoice = new int[2];
            game.discardSelectedLC(i,LCDiscardChoice);
        }

        if (playerNumber == 1) playingSolo();
        else playingMultiplayer();
    }

    private void playingSolo(){

    }

    public void playingMultiplayer(){
        for(int i=0;i<playerNumber;i++){
            if(i%game.getInkwell() > 0){    //TODO: dal secondo giocatore in poi
                //richiedi le risorse extra che vuole ricevere
                ArrayList<Resources> userChoice = new ArrayList<>();
                game.finishingSetup(i,userChoice);
            }
        }

        int i = game.getInkwell();
        boolean lastRound = false;
        while(!endGame || !lastRound){
            clients.get(i).setMyTurn(true);
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
        if(message instanceof SetupLCDiscardMessage){
            game.discardSelectedLC(player, ((SetupLCDiscardMessage) message).getDiscardedLC());
            clients.get(player).setDiscardLeaderCard(true);
        }
        else if(message instanceof FinishSetupMessage){
            game.finishingSetup(player, ((FinishSetupMessage) message).getUserChoice());
            clients.get(player).setFinishingSetup(true);
        }
        else if(message instanceof BuyResourcesMessage) {
            game.getMarketResources(player, ((BuyResourcesMessage) message).getRow(),
                    ((BuyResourcesMessage) message).getN(), ((BuyResourcesMessage) message).getRequestedWMConversion());

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
                    game.moveAction(player, ((MoveActionMessage) userChoice).getUserChoice());
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
                game.moveAction(player,((MoveActionMessage) message).getUserChoice());
            }catch(IllegalArgumentException e){e.printStackTrace();}
        }

        //buy development card
        else if(message instanceof BuyDevelopmentCardMessage){
            try {
                game.getDevelopmentCard(((BuyDevelopmentCardMessage) message).getC(),((BuyDevelopmentCardMessage) message).getLevel(),
                player,((BuyDevelopmentCardMessage) message).getSlotNumber(),((BuyDevelopmentCardMessage) message).getUserChoice());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //activate production (choice of which one is in the message)
        else if(message instanceof ProductionMessage){
            game.activateProduction(player,((ProductionMessage) message).getUserChoice());
        }

        //play a leader card
        else if(message instanceof PlayLeaderCardMessage){
            game.playLeaderCard(player, ((PlayLeaderCardMessage) message).getN());
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
