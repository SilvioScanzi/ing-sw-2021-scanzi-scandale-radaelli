package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.DevelopmentCardMarket;
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

            CH.setState(ClientHandler.ClientHandlerState.notMyTurn);
            synchronized (clients) {
                for (ClientHandler clientHandler : clients) {
                    if (!clientHandler.equals(CH)) clientHandler.sendObject(new ReconnectMessage(CH.getNickname()));
                }
            }
            //attaching observers
            CH.addObserver(this);
            game.addObserver(CH);

            //sending game status to the reconnected player
            synchronized (game){
                CH.sendObject(new MarketMessage(game.getMarket()));
                CH.sendObject(new DCMarketMessage(game.getDevelopmentCardMarket()));
                for(int i=0; i<playerNumber; i++){
                    Board playerBoard = game.getBoard(i);
                    CH.sendObject(new FaithTrackMessage(playerBoard.getFaithtrack(),playerBoard.getNickname()));
                    CH.sendObject(new WarehouseMessage(playerBoard.getWarehouse(),playerBoard.getNickname()));
                    CH.sendObject(new StrongboxMessage(playerBoard.getStrongbox(),playerBoard.getNickname()));
                    CH.sendObject(new SlotMessage(playerBoard.getSlot(1),playerBoard.getNickname()));
                    CH.sendObject(new SlotMessage(playerBoard.getSlot(2),playerBoard.getNickname()));
                    CH.sendObject(new SlotMessage(playerBoard.getSlot(3),playerBoard.getNickname()));
                    CH.sendObject(new LeaderCardPlayedMessage(playerBoard.getLeaderCardsPlayed(), playerBoard.getNickname()));
                }
            }

            synchronized (clients) {
                int n = disconnectedPlayers.get(name);
                clients.set(n, CH);
                clientMap.put(CH, new Pair<>(name, disconnectedPlayers.get(name)));
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

        //attaching model observers
        for(ClientHandler CH : clients){
            game.addObserver(CH);
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
        boolean endGame = false;
        boolean lorenzoWin = false;
        while(!endGame && !lorenzoWin){
            //player's turn     //TODO: implement lobby death if disconnected
            ClientHandler CH = clients.get(0);

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
            endGame = game.checkEndGame(0);

            //Lorenzo's turn
            if(!endGame){
                game.activatedToken();
                lorenzoWin = game.checkLorenzoWin();
            }
        }

        game.countVictoryPoints();
        clients.get(0).sendStandardMessage(StandardMessages.endGame);
        if(endGame) clients.get(0).sendObject(new VictoryMessage(clients.get(0).getNickname(),game.getBoard(0).getVictoryPoints()));
        else clients.get(0).sendStandardMessage(StandardMessages.lorenzoWin);

        clients.get(0).setState(ClientHandler.ClientHandlerState.disconnected);
        clients.get(0).closeConnection();

        lobbyNotify();
    }

    public void playingMultiplayer(){
        int k=1;
        for(int i=(game.getInkwell()+1)%playerNumber; i!=game.getInkwell(); i=(i+1)%playerNumber) {
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

        for(ClientHandler CH : clients){
            CH.setState(ClientHandler.ClientHandlerState.disconnected);
            CH.closeConnection();
        }
        lobbyNotify();
    }

    @Override
    public void updateDisconnected(CHObservable obs){
        LobbyState LS;
        synchronized (Lock) {
            LS = state;
        }
        if (LS.equals(LobbyState.discardedLCSetup) || LS.equals(LobbyState.finishingSetup)) {
            for (ClientHandler CH : clients) {
                synchronized (clients) {
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
                synchronized (clients) {
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

    @Override
    public void updateLCDiscard(CHObservable obs, SetupLCDiscardMessage message){
        ClientHandler client = (ClientHandler) obs;
        int player = clientMap.get(client).getValue();

        synchronized (clients) {
            try {
                game.discardSelectedLC(player, message.getDiscardedLC());
                client.setState(ClientHandler.ClientHandlerState.wait);
                synchronized (Lock) {
                    playersDone++;
                }
            } catch (Exception e) {
                client.sendStandardMessage(StandardMessages.leaderCardOutOfBounds);
            }
        }
    }

    @Override
    public void updateFinishSetup(CHObservable obs, FinishSetupMessage message){
        ClientHandler client = (ClientHandler) obs;
        int player = clientMap.get(client).getValue();

        synchronized (clients) {
            try {
                game.finishingSetup(player, message.getUserChoice());
                client.setState(ClientHandler.ClientHandlerState.wait);
                synchronized (Lock) {
                    playersDone++;
                }
            } catch (Exception e) {
                client.sendStandardMessage(StandardMessages.wrongObject);
            }
        }
    }

    @Override
    public void updateBuyResources(CHObservable obs, BuyResourcesMessage message){
        ClientHandler client = (ClientHandler) obs;
        int player = clientMap.get(client).getValue();

        synchronized (clients) {
            try {
                game.BuyMarketResourcesAction(player, message.getRow(), message.getN(), message.getRequestedWMConversion());
            } catch (Exception e) {
                client.sendStandardMessage(StandardMessages.wrongObject);
            }
            //after getting the resources, the user needs to say where he wants to deposit them
            client.setLastActionMarket(true);
            client.setState(ClientHandler.ClientHandlerState.moveNeeded);
        }
    }

    @Override
    public void updateBuyDC(CHObservable obs, BuyDevelopmentCardMessage message){
        ClientHandler client = (ClientHandler) obs;
        int player = clientMap.get(client).getValue();

        synchronized (clients) {
            try {
                game.BuyDevelopmentCardAction(message.getC(), message.getLevel(), player, message.getSlotNumber(), message.getUserChoice());
            } catch (Exception e) {
                client.sendStandardMessage(StandardMessages.wrongObject);
            }
            client.setState(ClientHandler.ClientHandlerState.actionDone);
        }
    }

    @Override
    public void updateProduction(CHObservable obs, ProductionMessage message){
        ClientHandler client = (ClientHandler) obs;
        int player = clientMap.get(client).getValue();

        synchronized (clients) {
            try {
                game.activateProductionAction(player, message.getUserChoice());
            } catch (Exception e) {
                client.sendStandardMessage(StandardMessages.wrongObject);
            }
            client.setState(ClientHandler.ClientHandlerState.actionDone);
        }
    }

    @Override
    public void updateMoveResources(CHObservable obs, MoveResourcesMessage message){
        ClientHandler client = (ClientHandler) obs;
        int player = clientMap.get(client).getValue();

        synchronized (clients) {
            try {
                game.moveResources(player, message.getUserChoice());
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
        }
    }

    @Override
    public void updatePlayLeaderCard(CHObservable obs, PlayLeaderCardMessage message){
        ClientHandler client = (ClientHandler) obs;
        int player = clientMap.get(client).getValue();

        synchronized (clients) {
            try {
                game.playLeaderCard(player, message.getN());
            } catch (RequirementsNotMetException | IndexOutOfBoundsException e) {
                client.sendStandardMessage(StandardMessages.wrongObject);
            }
        }
    }

    @Override
    public void updateDiscardLeaderCard(CHObservable obs, DiscardLeaderCardMessage message){
        ClientHandler client = (ClientHandler) obs;
        int player = clientMap.get(client).getValue();

        synchronized (clients) {
            try {
                game.discardLeaderCard(player, message.getN());
            } catch (IndexOutOfBoundsException e) {
                client.sendStandardMessage(StandardMessages.wrongObject);
            }
        }
    }

    @Override
    public void updateTurnDone(CHObservable obs, TurnDoneMessage message){
        ClientHandler client = (ClientHandler) obs;

        client.setState(ClientHandler.ClientHandlerState.notMyTurn);
    }
}
