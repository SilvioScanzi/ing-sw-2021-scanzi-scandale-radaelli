package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.observers.CHObservable;
import it.polimi.ingsw.observers.CHObserver;

import java.util.ArrayList;
import java.util.HashMap;

public class Lobby implements Runnable, CHObserver {
    private final Game game;
    private boolean endGame = false;
    private final int playerNumber;
    private final HashMap<ClientHandler,String> clientNames = new HashMap<>();
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    private final ArrayList<String> playersName = new ArrayList<>();
    private ArrayList<String> disconnectedPlayers = new ArrayList<>();
    private boolean lastActionMarket = false;

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
        //attaching client handler observers
        for(ClientHandler CH : clients){
            CH.addObserver(this);
        }

        //attaching model observers
        for(int i=0;i<playerNumber;i++){
            Board board = game.getBoard(i);
            for(ClientHandler CH : clients){
                board.addObserver(CH);
            }
        }

        for(ClientHandler CH : clients){
            CH.setNameQueue(clientNames);
            CH.setLobbyReady(true);
        }

        for(ClientHandler CH : clients){
            CH.sendStandardMessage(StandardMessages.chooseNickName);
        }
        synchronized (clientNames){
            while(clientNames.size()<playerNumber){
                try{clientNames.wait();
                }catch(Exception e){e.printStackTrace();}
            }
            for(int i=0;i<playerNumber;i++){
                playersName.add(clientNames.get(clients.get(i)));
                clients.get(i).setMessageReady(false);
            }
        }

        //beginning setup
        game.setup(playersName);
        //sending market and leader cards drawn to choose the ones to keep
        for(int i=0; i<playerNumber; i++){
            clients.get(i).sendObject(new MarketMessage(game.getMarket()));
            clients.get(i).sendObject(new LeaderCardMessage(game.getBoard(clientNames.get(clients.get(i))).getLeadercards()));
            clients.get(i).sendStandardMessage(StandardMessages.chooseDiscardedLC);
            synchronized (clients.get(i)) {
                while (!clients.get(i).getMessageReady()) {
                    try {
                        clients.get(i).wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message message = clients.get(i).getMessage();
                if (message instanceof SetupLCDiscardMessage) {
                    game.discardSelectedLC(i, ((SetupLCDiscardMessage) message).getDiscardedLC());
                    clients.get(i).setDiscardLeaderCard(true);
                } else {
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
        for(int i=(game.getInkwell()+1)%playerNumber;i!=game.getInkwell();i=(i+1)%playerNumber) {
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
                    k++;
                } else {
                    clients.get(i).sendStandardMessage(StandardMessages.wrongObject);
                    i--;
                }
                clients.get(i).setMessageReady(false);
            }
        }

        clients.get(game.getInkwell()).setFinishingSetup(true);

        int i = game.getInkwell();
        boolean lastRound = false;
        while(!endGame || !lastRound){
            if(disconnectedPlayers.contains(playersName.get(i))){
                i=(i+1)%playerNumber;
            }
            else {
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
                if (game.checkEndGame(i)) {
                    endGame = true;
                }
                clients.get(i).setMyTurn(false);
                i = (i + 1) % playerNumber;
                if (endGame && i == game.getInkwell()) {
                    lastRound = true;
                }
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
        //Buy resources from market
        if(message instanceof BuyResourcesMessage) {
            try {
                game.BuyMarketResourcesAction(player, ((BuyResourcesMessage) message).getRow(), ((BuyResourcesMessage) message).getN(), ((BuyResourcesMessage) message).getRequestedWMConversion());
            } catch (ActionAlreadyDoneException | IndexOutOfBoundsException e) {
                handleExceptions(e,player);
            }
            catch(IllegalArgumentException e){
                clients.get(player).sendStandardMessage(StandardMessages.whiteMarbleNotCongruent);
            }
            finally {
                clients.get(player).setMessageReady(false);
            }

            //after getting the resources, the user needs to say where he wants to deposit them
            clients.get(player).sendStandardMessage(StandardMessages.moveActionNeeded);
            clients.get(player).setMoveNeeded(true);
            clients.get(player).setActionDone(true);
            lastActionMarket = true;
        }

        //buy development card
        else if(message instanceof BuyDevelopmentCardMessage) {
            try {
                game.BuyDevelopmentCardAction(((BuyDevelopmentCardMessage) message).getC(), ((BuyDevelopmentCardMessage) message).getLevel(), player, ((BuyDevelopmentCardMessage) message).getSlotNumber(), ((BuyDevelopmentCardMessage) message).getUserChoice());
            } catch (ActionAlreadyDoneException| RequirementsNotMetException | ResourceErrorException | LeaderCardNotCompatibleException | IndexOutOfBoundsException e) {
                handleExceptions(e, player);
            }catch (EmptyException e){
                clients.get(player).sendStandardMessage(StandardMessages.emptyDCStack);
            }
            catch(InvalidPlacementException e){
                clients.get(player).sendStandardMessage(StandardMessages.invalidSlot);
            }
            catch(IllegalArgumentException e){
                clients.get(player).sendStandardMessage(StandardMessages.invalidChoice);
            }
            finally {
                clients.get(player).setMessageReady(false);
            }
            clients.get(player).setActionDone(true);
        }

        //activate production (choice of which one is in the message)
        else if(message instanceof ProductionMessage){
            try {
                game.activateProductionAction(player,((ProductionMessage) message).getUserChoice());
            } catch (ActionAlreadyDoneException | LeaderCardNotCompatibleException | ResourceErrorException | RequirementsNotMetException e)  {
                handleExceptions(e, player);
                clients.get(player).setMessageReady(false);
            }
            catch (IllegalArgumentException e){
                clients.get(player).sendStandardMessage(StandardMessages.invalidChoice);
            }
            catch (BadRequestException e){
                clients.get(player).sendStandardMessage(StandardMessages.baseProductionError);
            }
            catch (EmptyException e){
                clients.get(player).sendStandardMessage(StandardMessages.emptySlot);
            }
            finally{
                clients.get(player).setMessageReady(false);
            }
            clients.get(player).setActionDone(true);
        }

        //move resources around
        else if(message instanceof MoveResourcesMessage){
            try{
                game.moveResources(player,((MoveResourcesMessage) message).getUserChoice());
                clients.get(player).setMoveNeeded(false);
                lastActionMarket = false;
            }catch(IllegalArgumentException e) {
                clients.get(player).sendStandardMessage(StandardMessages.invalidChoice);
            }
            catch(BadRequestException e) {
                clients.get(player).sendStandardMessage(StandardMessages.resourcesWrong);
            }
            catch(LeaderCardNotCompatibleException | IncompatibleResourceException | InvalidPlacementException | ResourceErrorException e) {
                handleExceptions(e, player);
            }
            catch(ResourcesLeftInHandException e){
                if(lastActionMarket){
                    game.discardRemainingResources(player);
                    clients.get(player).setMoveNeeded(false);
                    lastActionMarket = false;
                }
                else{
                    clients.get(player).sendStandardMessage(StandardMessages.resourcesLeftInHand);
                    clients.get(player).setMoveNeeded(true);
                }
            }
            finally{
                clients.get(player).setMessageReady(false);
            }
        }

        //play a leader card
        else if(message instanceof PlayLeaderCardMessage){
            try {
                game.playLeaderCard(player, ((PlayLeaderCardMessage) message).getN());
            } catch (RequirementsNotMetException | IndexOutOfBoundsException e) {
                handleExceptions(e,player);
            }
            finally{
                clients.get(player).setMessageReady(false);
            }
        }

        //discard a leader card
        else if(message instanceof DiscardLeaderCardMessage){
            try {
                game.discardLeaderCard(player, ((DiscardLeaderCardMessage) message).getN());
            } catch(IndexOutOfBoundsException e) {
                handleExceptions(e,player);
            }
            finally{
                clients.get(player).setMessageReady(false);
            }
        }

        //turn finished
        else if(message instanceof TurnDoneMessage){
            clients.get(player).setMessageReady(false);
            return true;
        }

        //message not recognized
        else {
            clients.get(player).sendStandardMessage(StandardMessages.wrongObject);
            clients.get(player).setMessageReady(false);
        }
        return false;
    }

    private void handleExceptions(Exception e, int player){
        if(e instanceof ActionAlreadyDoneException) clients.get(player).sendStandardMessage(StandardMessages.actionAlreadyDone);
        else if(e instanceof IndexOutOfBoundsException) clients.get(player).sendStandardMessage(StandardMessages.indexOutOfBound);
        else if(e instanceof IncompatibleResourceException) clients.get(player).sendStandardMessage(StandardMessages.incompatibleResources);
        else if(e instanceof InvalidPlacementException) clients.get(player).sendStandardMessage(StandardMessages.invalidChoice);
        else if(e instanceof LeaderCardNotCompatibleException) clients.get(player).sendStandardMessage(StandardMessages.leaderCardWrongAbility);
        else if(e instanceof RequirementsNotMetException) clients.get(player).sendStandardMessage(StandardMessages.requirementsNotMet);
        else if(e instanceof ResourceErrorException) clients.get(player).sendStandardMessage(StandardMessages.notEnoughResources);
        else clients.get(player).sendStandardMessage(StandardMessages.wrongObject);
    }

    @Override
    public void update(CHObservable obs, Object obj){
        if(!(obs instanceof ClientHandler) || !(obj instanceof StandardMessages)){
            System.out.println("Errore nei messaggi o oggetti passati.");
        }
        else if(obj.equals(StandardMessages.disconnectedMessage)){
            ArrayList<String> disconnectedNames = new ArrayList<>();
            for(ClientHandler CH : clients){
                if(CH.getState().equals(ClientHandler.STATE.disconnected)){
                    disconnectedNames.add(CH.getNickname());
                    disconnectedPlayers.add(CH.getNickname());
                    clients.remove(CH);
                }
            }
            for(ClientHandler CH : clients){
                CH.sendObject(new DisconnectedMessage(disconnectedNames));
                CH.closeConnection();
            }
        }
    }
}
