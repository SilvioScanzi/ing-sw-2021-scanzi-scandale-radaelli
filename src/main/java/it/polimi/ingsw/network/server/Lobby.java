package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Pair;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.observers.CHObservable;
import it.polimi.ingsw.observers.CHObserver;

import java.util.ArrayList;
import java.util.HashMap;

public class Lobby implements Runnable, CHObserver {
    //TODO: Fatal error forces the lobby to destroy itself, server must be notified when lobby is done
    public enum LobbyState{start, discardedLCSetup, finishingSetup, play, end, fatalError};
    private LobbyState state = LobbyState.start;
    private final Game game;
    private boolean endGame = false;
    private final int playerNumber;
    private final HashMap<ClientHandler, Pair<String,Integer>> clientMap = new HashMap<>();
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    private final ArrayList<String> disconnectedPlayers = new ArrayList<>();
    private boolean lastActionMarket = false;
    private int playersDone = 0;
    private final Object Lock = new Object();

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

    public void removePlayer(ClientHandler CH){
        clients.remove(CH);
    }

    @Override
    public void run() {
        //attaching client handler observers
        for (ClientHandler CH : clients) {
            CH.addObserver(this);
        }

        //attaching model observers
        for (int i = 0; i < playerNumber; i++) {
            Board board = game.getBoard(i);
            for (ClientHandler CH : clients) {
                board.addObserver(CH);
            }
        }

        //beginning setup
        for(int i=0;i<playerNumber;i++){
            clientMap.put(clients.get(i),new Pair<>(clients.get(i).getNickname(),i));
        }
        ArrayList<String> playersName = new ArrayList<>();
        for(ClientHandler CH : clients){
            playersName.add(clientMap.get(CH).getKey());
        }
        game.setup(playersName);

        //sending market and leader cards drawn to choose the ones to keep
        for(ClientHandler CH : clients){
            CH.sendObject(new MarketMessage(game.getMarket()));
            CH.sendObject(new LeaderCardMessage(game.getBoard(CH.getNickname()).getLeadercards()));
        }

        while (state.equals(LobbyState.discardedLCSetup)){
            synchronized (Lock){
                if(playersDone == playerNumber) state = LobbyState.finishingSetup;
            }
        }

        if(state.equals(LobbyState.fatalError)){
            return;
        }
        playersDone = 0;

        if (playerNumber == 1) playingSolo();
        else playingMultiplayer();
    }

    private void playingSolo(){

    }

    public void playingMultiplayer(){
        int k=1;
        for(int i=(game.getInkwell()+1)%playerNumber;i!=game.getInkwell();i=(i+1)%playerNumber) {
            synchronized(clients.get(i)){
                clients.get(i).setState(ClientHandler.ClientHandlerState.finishingSetup);
            }
            if (k == 1 || k == 2) {
                clients.get(i).sendStandardMessage(StandardMessages.chooseOneResource);
            } else if (k == 3) {
                clients.get(i).sendStandardMessage(StandardMessages.chooseTwoResource);
            }
            k++;
        }

        while (state.equals(LobbyState.finishingSetup)){
            synchronized (Lock){
                if(playersDone == playerNumber - 1) state = LobbyState.finishingSetup;
            }
        }

        if(state.equals(LobbyState.fatalError)){
            return;
        }

        int i = game.getInkwell();
        boolean lastRound = false;
        while(!endGame || !lastRound){
            ClientHandler CH = clients.get(i);
            if (!disconnectedPlayers.contains(clientMap.get(CH).getKey())) {
                synchronized (CH) {
                    CH.setState(ClientHandler.ClientHandlerState.myTurn);
                }
                clients.get(i).sendStandardMessage(StandardMessages.yourTurn);
                ClientHandler.ClientHandlerState state = ClientHandler.ClientHandlerState.myTurn;
                while (!state.equals(ClientHandler.ClientHandlerState.disconnected) && !state.equals(ClientHandler.ClientHandlerState.notMyTurn)) {
                    synchronized (CH) {
                        state = CH.getState();
                    }
                }
                endGame = game.checkEndGame(i);
                if (endGame && i == game.getInkwell()) {
                    lastRound = true;
                }
            }
            i = (i+1)%playerNumber;
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
    public void update(CHObservable obs, Object obj) {
        if (obs instanceof ClientHandler) {
            ClientHandler client = (ClientHandler) obs;
            int playerNumber = clientMap.get(client).getValue();

            if (obj instanceof DisconnectedMessage) {
                for (ClientHandler CH : clients) {
                    synchronized (CH) {
                        if (CH.getState().equals(ClientHandler.ClientHandlerState.disconnected)) {
                            disconnectedPlayers.add(CH.getNickname());
                            clients.remove(CH);
                        }
                    }
                }

                for (ClientHandler CH : clients) {
                    CH.sendObject(new DisconnectedMessage(disconnectedPlayers));
                }

                synchronized (Lock){
                    if(!state.equals(LobbyState.play) && !state.equals(LobbyState.end)){
                        for(ClientHandler CH : clients){
                            synchronized (CH){
                                CH.sendStandardMessage(StandardMessages.endGame);
                                CH.closeConnection();
                            }
                        }
                    }
                    state = LobbyState.fatalError;
                }
            }

            //TODO: Exception Handling
            else if (obj instanceof SetupLCDiscardMessage) {
                try {
                    game.discardSelectedLC(playerNumber,((SetupLCDiscardMessage) obj).getDiscardedLC());
                    client.setState(ClientHandler.ClientHandlerState.wait);
                    synchronized (Lock) {
                        playersDone++;
                    }
                }catch(Exception e){
                    client.sendStandardMessage(StandardMessages.leaderCardOutOfBounds);
                }
                finally{
                    client.setMessageReady(false);
                }
            }
            else if (obj instanceof FinishSetupMessage){
                try{
                    game.finishingSetup(playerNumber, ((FinishSetupMessage) obj).getUserChoice());
                    client.setState(ClientHandler.ClientHandlerState.wait);
                    synchronized (Lock) {
                        playersDone++;
                    }
                }catch(Exception e){
                    client.sendStandardMessage(StandardMessages.wrongObject);
                }finally{
                    client.setMessageReady(false);
                }
            }
        }
    }

}
