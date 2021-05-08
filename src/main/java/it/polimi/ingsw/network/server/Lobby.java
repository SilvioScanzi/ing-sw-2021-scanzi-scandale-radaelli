package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Pair;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.observers.CHObservable;
import it.polimi.ingsw.observers.CHObserver;
import it.polimi.ingsw.observers.LobbyObservable;

import java.util.ArrayList;
import java.util.HashMap;

public class Lobby extends LobbyObservable implements Runnable, CHObserver {
    public enum LobbyState{start, discardedLCSetup, finishingSetup, play, end, fatalError}
    private final Object Lock = new Object();
    private LobbyState state = LobbyState.start;
    private final Game game;
    private final int playerNumber;
    private final HashMap<ClientHandler, Pair<String,Integer>> clientMap = new HashMap<>();
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    private final HashMap<String, Integer> disconnectedPlayers = new HashMap<>();
    private int playersDone = 0;
    private int turn;

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
        if(state.equals(LobbyState.start)){
            clients.add(CH);
        }
        else if(state.equals(LobbyState.play)) {
            String name = CH.getNickname();
            synchronized (clients) {
                CH.setState(ClientHandler.ClientHandlerState.notMyTurn);
                //TODO: manda messaggi per interfaccia
                int n = disconnectedPlayers.get(name);
                clients.set(n,CH);
                clientMap.put(CH, new Pair<>(name,disconnectedPlayers.get(name)));
                disconnectedPlayers.remove(name);
                if (n < turn) turn = (turn + 1) % playerNumber;
            }
        }
        else{
            CH.sendStandardMessage(StandardMessages.wrongObject);
            CH.closeConnection();
        }
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

        //beginning setup
        for(int i=0;i<playerNumber;i++){
            clientMap.put(clients.get(i),new Pair<>(clients.get(i).getNickname(),i));
        }
        ArrayList<String> playersName = new ArrayList<>();
        for(ClientHandler CH : clients){
            playersName.add(clientMap.get(CH).getKey());
        }
        game.setup(playersName);

        //attaching model observers
        for (int i = 0; i < playerNumber; i++) {
            Board board = game.getBoard(i);
            for (ClientHandler CH : clients) {
                board.addObserver(CH);
            }
        }
        for(ClientHandler CH : clients){
            game.getMarket().addObserver(CH);
            game.getDevelopmentCardMarket().addObserver(CH);
        }

        //sending market and leader cards drawn to choose the ones to keep
        for(ClientHandler CH : clients){
            CH.sendObject(new MarketMessage(game.getMarket()));
            CH.sendObject(new LeaderCardMessage(game.getBoard(CH.getNickname()).getLeadercards()));
        }

        for(ClientHandler CH : clients){
            synchronized (CH){
                CH.setState(ClientHandler.ClientHandlerState.discardLeaderCard);
                CH.sendStandardMessage(StandardMessages.chooseDiscardedLC);
            }
        }

        synchronized(Lock){
            if(!state.equals(LobbyState.fatalError)) state = LobbyState.discardedLCSetup;
        }

        while (state.equals(LobbyState.discardedLCSetup)){
            synchronized (Lock){
                if(playersDone == playerNumber) state = LobbyState.finishingSetup;
            }
        }

        if(state.equals(LobbyState.fatalError)){
            lobbyNotify();
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
                if(playersDone == playerNumber - 1) state = LobbyState.play;
            }
        }

        if(state.equals(LobbyState.fatalError)){
            lobbyNotify();
            return;
        }

        boolean lastRound = false;
        boolean endGame = false;
        synchronized (clients) {
            turn = game.getInkwell();
        }
        while(!endGame || !lastRound){
            if (!disconnectedPlayers.containsValue(turn)) {
                ClientHandler CH;
                synchronized(clients) {
                    CH = clients.get(turn);
                }
                ClientHandler.ClientHandlerState state;
                synchronized (CH) {
                    CH.setState(ClientHandler.ClientHandlerState.myTurn);
                    CH.sendStandardMessage(StandardMessages.yourTurn);
                    state = ClientHandler.ClientHandlerState.myTurn;
                }

                while (!state.equals(ClientHandler.ClientHandlerState.disconnected) && !state.equals(ClientHandler.ClientHandlerState.notMyTurn)) {
                    synchronized (CH) {
                        state = CH.getState();
                        if(state.equals(ClientHandler.ClientHandlerState.disconnected)) {CH.notify();}
                    }
                }
                endGame = game.checkEndGame(turn);
                synchronized (clients) {
                    turn = (turn + 1) % playerNumber;
                }
                if (endGame && turn == game.getInkwell()) {
                    lastRound = true;
                }
            }
            else{ synchronized (clients) {turn = (turn+1)%playerNumber;}}
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

        //TODO: Sistemare la notify
        lobbyNotify();
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
            int player = clientMap.get(client).getValue();

            if (obj instanceof ClientHandler) {
                LobbyState LS;
                synchronized (Lock) {
                    LS = state;
                }
                if (LS.equals(LobbyState.discardedLCSetup) || LS.equals(LobbyState.finishingSetup)) {
                    for (ClientHandler CH : clients) {
                        synchronized (CH) {
                            CH.sendStandardMessage(StandardMessages.endGame);
                            CH.closeConnection();
                        }
                    }
                    synchronized (Lock) {
                        playersDone = playerNumber;
                        state = LobbyState.fatalError;
                    }
                } else {
                    for (ClientHandler CH : clients) {
                        synchronized (CH) {
                            if (CH.getState().equals(ClientHandler.ClientHandlerState.disconnected)) {
                                disconnectedPlayers.put(CH.getNickname(), clientMap.get(CH).getValue());
                                clientMap.remove(CH);
                            }
                        }
                    }

                    ArrayList<String> DP = new ArrayList<>(disconnectedPlayers.keySet());
                    for (ClientHandler CH : clients) {
                        if(!disconnectedPlayers.containsKey(CH.getNickname())) {
                            CH.sendObject(new DisconnectedMessage(DP));
                        }
                    }
                }
            }

            //TODO: Exception Handling
            synchronized (client) {
                if (obj instanceof SetupLCDiscardMessage) {
                    try {
                        game.discardSelectedLC(player, ((SetupLCDiscardMessage) obj).getDiscardedLC());
                        client.setState(ClientHandler.ClientHandlerState.wait);
                        synchronized (Lock) {
                            playersDone++;
                        }
                    } catch (Exception e) {
                        client.sendStandardMessage(StandardMessages.leaderCardOutOfBounds);
                    }
                } else if (obj instanceof FinishSetupMessage) {
                    try {
                        game.finishingSetup(player, ((FinishSetupMessage) obj).getUserChoice());
                        client.setState(ClientHandler.ClientHandlerState.wait);
                        synchronized (Lock) {
                            playersDone++;
                        }
                    } catch (Exception e) {
                        client.sendStandardMessage(StandardMessages.wrongObject);
                    }
                } else if (obj instanceof BuyResourcesMessage) {
                    try {
                        game.BuyMarketResourcesAction(player, ((BuyResourcesMessage) obj).getRow(), ((BuyResourcesMessage) obj).getN(), ((BuyResourcesMessage) obj).getRequestedWMConversion());
                    } catch (Exception e) {
                        client.sendStandardMessage(StandardMessages.wrongObject);
                    }
                    //after getting the resources, the user needs to say where he wants to deposit them
                    client.setLastActionMarket(true);
                    client.setState(ClientHandler.ClientHandlerState.moveNeeded);
                } else if (obj instanceof BuyDevelopmentCardMessage) {
                    try {
                        game.BuyDevelopmentCardAction(((BuyDevelopmentCardMessage) obj).getC(), ((BuyDevelopmentCardMessage) obj).getLevel(), player, ((BuyDevelopmentCardMessage) obj).getSlotNumber(), ((BuyDevelopmentCardMessage) obj).getUserChoice());
                    } catch (Exception e) {
                        client.sendStandardMessage(StandardMessages.wrongObject);
                    }
                    client.setState(ClientHandler.ClientHandlerState.actionDone);
                } else if (obj instanceof ProductionMessage) {
                    try {
                        game.activateProductionAction(player, ((ProductionMessage) obj).getUserChoice());
                    } catch (Exception e) {
                        client.sendStandardMessage(StandardMessages.wrongObject);
                    }
                    client.setState(ClientHandler.ClientHandlerState.actionDone);
                } else if (obj instanceof MoveResourcesMessage) {
                    try {
                        game.moveResources(player, ((MoveResourcesMessage) obj).getUserChoice());
                        if (client.getLastActionMarket()) {
                            client.setLastActionMarket(false);
                            client.setState(ClientHandler.ClientHandlerState.actionDone);
                        } else {
                            client.setState(ClientHandler.ClientHandlerState.myTurn);
                        }
                    } catch (IllegalArgumentException | BadRequestException | LeaderCardNotCompatibleException | IncompatibleResourceException | InvalidPlacementException | ResourceErrorException e) {
                        client.sendStandardMessage(StandardMessages.wrongObject);
                    } catch (ResourcesLeftInHandException e) {
                        if (client.getLastActionMarket()) {
                            client.setLastActionMarket(false);
                            game.discardRemainingResources(player);
                            client.setState(ClientHandler.ClientHandlerState.actionDone);
                        } else {
                            client.sendStandardMessage(StandardMessages.wrongObject);
                        }
                    }
                } else if (obj instanceof PlayLeaderCardMessage) {
                    try {
                        game.playLeaderCard(player, ((PlayLeaderCardMessage) obj).getN());
                    } catch (RequirementsNotMetException | IndexOutOfBoundsException e) {
                        client.sendStandardMessage(StandardMessages.wrongObject);
                    }
                } else if (obj instanceof DiscardLeaderCardMessage) {
                    try {
                        game.discardLeaderCard(player, ((DiscardLeaderCardMessage) obj).getN());
                    } catch (IndexOutOfBoundsException e) {
                        client.sendStandardMessage(StandardMessages.wrongObject);
                    }
                } else if (obj instanceof TurnDoneMessage) {
                    client.setState(ClientHandler.ClientHandlerState.notMyTurn);
                }
            }
        }
    }
}
