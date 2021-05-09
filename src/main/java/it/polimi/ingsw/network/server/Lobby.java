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

//ASK: Sincronizzazione, quando è necessaria? (se leggo...)

public class Lobby extends LobbyObservable implements Runnable, CHObserver {
    public enum LobbyState{start, discardedLCSetup, finishingSetup, play, fatalError}
    private int playersDone = 0;
    private LobbyState state = LobbyState.start;
    //Used to access Lobby State (synchronization)
    private final Object Lock = new Object();
    private final int playerNumber;
    private final Game game;
    //ArrayList of client handlers which are connected to this lobby
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    //Map: ClientHandler associated with his Name and his Position in the game
    private final HashMap<ClientHandler, Pair<String,Integer>> clientMap = new HashMap<>();
    //Map: Name of the disconnected players associated with his Position in the game
    private final HashMap<String, Integer> disconnectedPlayers = new HashMap<>();

    public Lobby(int playerNumber){
        this.playerNumber = playerNumber;
        game = new Game();
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public int getAddedPlayers(){
        synchronized (clients){
            return clients.size();
        }
    }

    public void addPlayer(ClientHandler CH) {
        if(state.equals(LobbyState.start)){
            //no need for synchronization because this first add is only done in one thread (lobbyManager)
            clients.add(CH);
        }
        else if(state.equals(LobbyState.play)) {
            String name = CH.getNickname();

            CH.setState(ClientHandler.ClientHandlerState.notMyTurn);


            for (ClientHandler clientHandler : clients) {
                if (!clientHandler.equals(CH) && !(disconnectedPlayers.containsKey(clientHandler.getNickname()))) clientHandler.sendObject(new ReconnectMessage(CH.getNickname()));
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
            }
        }
        else{
            CH.sendStandardMessage(StandardMessages.wrongObject);
            CH.closeConnection();
        }
    }

    public void removePlayer(ClientHandler CH){
        synchronized (clients) {
            clients.remove(CH);
        }
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
        ArrayList<String> playersName = new ArrayList<>();

        for (int i = 0; i < playerNumber; i++) {
            clientMap.put(clients.get(i), new Pair<>(clients.get(i).getNickname(), i));
        }

        for (ClientHandler CH : clients) {
            playersName.add(clientMap.get(CH).getKey());
        }

        game.setup(playersName);

        for(ClientHandler CH : clients) {
            CH.setState(ClientHandler.ClientHandlerState.discardLeaderCard);
            CH.sendStandardMessage(StandardMessages.chooseDiscardedLC);
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

    private void playingSolo() {

        synchronized (Lock) {
            if (!state.equals(LobbyState.fatalError)) state = LobbyState.play;
            else {
                lobbyNotify();
                return;
            }
        }

        boolean endGame = false;
        boolean lorenzoWin = false;
        while (!endGame && !lorenzoWin) {
            //player's turn
            ClientHandler CH = clients.get(0);


            CH.setState(ClientHandler.ClientHandlerState.myTurn);
            CH.sendStandardMessage(StandardMessages.yourTurn);


            while (!CH.getState().equals(ClientHandler.ClientHandlerState.disconnected) && !CH.getState().equals(ClientHandler.ClientHandlerState.notMyTurn)) {
            }
            /*synchronized (CH) {
                if(CH.getState().equals(ClientHandler.ClientHandlerState.disconnected)) {CH.notify();}
            }*/

            synchronized (Lock) {
                if (state.equals(LobbyState.fatalError)) {
                    lobbyNotify();
                    return;
                }
            }
            endGame = game.checkEndGame(0);

            //Lorenzo's turn
            if (!endGame) {
                game.activatedToken();
                lorenzoWin = game.checkLorenzoWin();
            }
        }

        game.countVictoryPoints();

        ClientHandler CH = clients.get(0);
        clients.get(0).sendStandardMessage(StandardMessages.endGame);
        if (endGame)
            CH.sendObject(new VictoryMessage(clients.get(0).getNickname(), game.getBoard(0).getVictoryPoints()));
        else CH.sendStandardMessage(StandardMessages.lorenzoWin);


        CH.setState(ClientHandler.ClientHandlerState.disconnected);
        CH.closeConnection();

        lobbyNotify();
    }

    public void playingMultiplayer(){
        int k=1;
        for(int i=(game.getInkwell()+1)%playerNumber; i!=game.getInkwell(); i=(i+1)%playerNumber) {

            clients.get(i).setState(ClientHandler.ClientHandlerState.finishingSetup);

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
        int turn;
        turn = game.getInkwell();

        while(!endGame || !lastRound){
            boolean isDisconnected = false;
            synchronized (clients){
                isDisconnected = disconnectedPlayers.containsValue(turn);
            }
            if (!isDisconnected) {
                ClientHandler CH;
                synchronized (clients) {
                    CH = clients.get(turn);
                }


                CH.setState(ClientHandler.ClientHandlerState.myTurn);
                CH.sendStandardMessage(StandardMessages.yourTurn);


                while (!CH.getState().equals(ClientHandler.ClientHandlerState.disconnected) && !CH.getState().equals(ClientHandler.ClientHandlerState.notMyTurn)) {
                }

                /*synchronized (CH) {
                    if(CH.getState().equals(ClientHandler.ClientHandlerState.disconnected)) {CH.notify();}
                }*/

                endGame = game.checkEndGame(turn);
                turn = (turn + 1) % playerNumber;

                if (endGame && turn == game.getInkwell()) {
                    lastRound = true;
                }
            }
            else {
                synchronized (Lock){
                    if(state.equals(Lobby.LobbyState.fatalError)) {
                        lobbyNotify();
                        return;
                    }
                }
                turn = (turn+1)%playerNumber;
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

        for(ClientHandler CH : clients) {
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
            synchronized (clients) {
                for (ClientHandler CH : clients) {
                    if (CH.getState().equals(ClientHandler.ClientHandlerState.disconnected)) {
                        disconnectedPlayers.put(CH.getNickname(), clientMap.get(CH).getValue());
                        clientMap.remove(CH);
                    }
                }

                ArrayList<String> DP = new ArrayList<>(disconnectedPlayers.keySet());
                for (ClientHandler CH : clients) {
                    if (!disconnectedPlayers.containsKey(CH.getNickname())) {
                        CH.sendObject(new DisconnectedMessage(DP));
                    }
                }
            }

            synchronized (Lock){
                if(disconnectedPlayers.size() == playerNumber){
                    state = Lobby.LobbyState.fatalError;
                }
            }
        }
    }

    @Override
    public void updateLCDiscard(CHObservable obs, SetupLCDiscardMessage message) {
        ClientHandler client = (ClientHandler) obs;
        synchronized (clients) {
            int player = clientMap.get(client).getValue();
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
        synchronized (clients) {
            int player = clientMap.get(client).getValue();
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
        synchronized (clients) {
            int player = clientMap.get(client).getValue();
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
    public void updateBuyDC(CHObservable obs, BuyDevelopmentCardMessage message) {
        ClientHandler client = (ClientHandler) obs;
        synchronized (clients) {
            int player = clientMap.get(client).getValue();
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
        synchronized (clients) {
            int player = clientMap.get(client).getValue();


            try {
                game.activateProductionAction(player, message.getUserChoice());
            } catch (Exception e) {
                client.sendStandardMessage(StandardMessages.wrongObject);
            }

            client.setState(ClientHandler.ClientHandlerState.actionDone);
        }
    }

    @Override
    public void updateMoveResources(CHObservable obs, MoveResourcesMessage message) {
        ClientHandler client = (ClientHandler) obs;
        synchronized (clients) {
            int player = clientMap.get(client).getValue();
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
                    game.discardRemainingResources(player);
                    client.setLastActionMarket(false);
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
        synchronized (clients) {
            int player = clientMap.get(client).getValue();
            try {
                game.playLeaderCard(player, message.getN());
            } catch (RequirementsNotMetException | IndexOutOfBoundsException e) {
                client.sendStandardMessage(StandardMessages.wrongObject);
            }
        }
    }

    @Override
    public void updateDiscardLeaderCard(CHObservable obs, DiscardLeaderCardMessage message) {
        ClientHandler client = (ClientHandler) obs;
        synchronized (clients) {
            int player = clientMap.get(client).getValue();
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
