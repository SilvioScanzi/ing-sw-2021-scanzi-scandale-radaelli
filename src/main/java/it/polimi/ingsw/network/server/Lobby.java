package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.commons.Pair;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.observers.CHObservable;
import it.polimi.ingsw.observers.CHObserver;
import it.polimi.ingsw.observers.LobbyObservable;

import java.util.ArrayList;
import java.util.HashMap;

public class Lobby extends LobbyObservable implements Runnable, CHObserver {
    public enum LobbyState{setup, discardedLCSetup, finishingSetup, playMultiplayer, playSolo, lorenzoTurn, demolished}
    private int playersDone = 0;
    private LobbyState state = LobbyState.setup;
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
        if(state.equals(LobbyState.setup)){
            //no need for synchronization because this first add is only done in one thread (lobbyManager)
            clients.add(CH);
        }
        else if(state.equals(LobbyState.playSolo) || state.equals(LobbyState.playMultiplayer)) {
            String name = CH.getNickname();

            CH.setState(ClientHandler.ClientHandlerState.notMyTurn);


            for (ClientHandler clientHandler : clients) {
                if (!clientHandler.equals(CH) && !(disconnectedPlayers.containsKey(clientHandler.getNickname())))
                    clientHandler.sendObject(new ReconnectMessage(CH.getNickname()));
            }

            //attaching observers
            CH.addObserver(this);
            game.addObserver(CH);

            //sending game status to the reconnected player
            synchronized (game){
                HashMap<Integer,String> nameMap = new HashMap<>();
                synchronized (clients) {
                    for (ClientHandler C : clientMap.keySet()) {
                        nameMap.put(clientMap.get(C).getValue(),clientMap.get(C).getKey());
                    }
                    for (String S : disconnectedPlayers.keySet()){
                        nameMap.put(disconnectedPlayers.get(S),S);
                    }
                }
                CH.sendObject(new NicknameMapMessage(CH.getNickname(),nameMap, game.getInkwell()));
                CH.sendObject(new ResourceMarketMessage(game.getMarket()));
                CH.sendObject(new DCMarketMessage(game.getDevelopmentCardMarket()));
                for(int i=0; i<playerNumber; i++){
                    Board playerBoard = game.getBoard(i);
                    CH.sendObject(new FaithTrackMessage(playerBoard.getFaithTrack(),playerBoard.getNickname()));
                    CH.sendObject(new WarehouseMessage(playerBoard.getWarehouse(),playerBoard.getNickname()));
                    CH.sendObject(new StrongboxMessage(playerBoard.getStrongbox(),playerBoard.getNickname()));
                    try {
                        CH.sendObject(new SlotMessage(playerBoard.getSlot(1).getFirstCard(), 1,playerBoard.getNickname()));
                    }catch(EmptyException ignored){}
                    try {
                        CH.sendObject(new SlotMessage(playerBoard.getSlot(2).getFirstCard(), 2,playerBoard.getNickname()));
                    }catch(EmptyException ignored){}
                    try {
                        CH.sendObject(new SlotMessage(playerBoard.getSlot(3).getFirstCard(), 3,playerBoard.getNickname()));
                    }catch(EmptyException ignored){}
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
        boolean lastRound = false;
        boolean endGame = false;
        boolean lorenzoWin = false;
        int turn = 0;
        LobbyState LS;
        do{
            synchronized (Lock){
                LS = state;
            }
            switch (LS) {
                case setup: {
                    //attaching client handler observers
                    for (ClientHandler CH : clients) {
                        CH.addObserver(this);
                    }

                    //attaching model observers
                    for (ClientHandler CH : clients) {
                        game.addObserver(CH);
                    }

                    //beginning setup
                    ArrayList<String> playersName = new ArrayList<>();

                    for (int i = 0; i < playerNumber; i++) {
                        clientMap.put(clients.get(i), new Pair<>(clients.get(i).getNickname(), i));
                    }

                    for (ClientHandler CH : clients) {
                        HashMap<Integer, String> nameMap = new HashMap<>();
                        for (ClientHandler C : clientMap.keySet()) {
                            nameMap.put(clientMap.get(C).getValue(), clientMap.get(C).getKey());
                        }
                        CH.sendObject(new NicknameMapMessage(CH.getNickname(), nameMap, game.getInkwell()));
                    }

                    for (ClientHandler CH : clients) {
                        playersName.add(clientMap.get(CH).getKey());
                    }

                    game.setup(playersName);

                    for (ClientHandler CH : clients) {
                        CH.setState(ClientHandler.ClientHandlerState.discardLeaderCard);
                        CH.sendStandardMessage(StandardMessages.chooseDiscardedLC);
                    }

                    synchronized (Lock) {
                        if (!state.equals(LobbyState.demolished)) state = LobbyState.discardedLCSetup;
                    }
                    break;
                }

                case discardedLCSetup: {
                    synchronized (Lock) {
                        if (playersDone == playerNumber && playerNumber == 1 && state != LobbyState.demolished) state = LobbyState.playSolo;
                        else if (playersDone == playerNumber && state != LobbyState.demolished) {
                            state = LobbyState.finishingSetup;
                            playersDone = 0;

                            int k = 1;
                            for (int i = (game.getInkwell() + 1) % playerNumber; i != game.getInkwell(); i = (i + 1) % playerNumber) {

                                clients.get(i).setState(ClientHandler.ClientHandlerState.finishingSetup);

                                if (k == 1 || k == 2) {
                                    clients.get(i).sendStandardMessage(StandardMessages.chooseOneResource);
                                } else if (k == 3) {
                                    clients.get(i).sendStandardMessage(StandardMessages.chooseTwoResource);
                                }
                                k++;
                            }
                        }
                    }
                    break;
                }

                case finishingSetup: {
                    synchronized (Lock){
                        if(playersDone == playerNumber - 1 && state != LobbyState.demolished){
                            state = LobbyState.playMultiplayer;
                        }
                    }
                    turn = game.getInkwell();
                    break;
                }

                case playMultiplayer: {
                    if(!(endGame && lastRound)){
                        boolean isDisconnected;
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

                            //TODO: Observer che dice in che stato si è NOTIFICA
                            while (!CH.getState().equals(ClientHandler.ClientHandlerState.disconnected) && !CH.getState().equals(ClientHandler.ClientHandlerState.notMyTurn)) {}

                            endGame = game.checkEndGame(turn);
                            turn = (turn + 1) % playerNumber;

                            if (endGame && turn == game.getInkwell()) {
                                lastRound = true;
                            }
                        }
                        else {
                            turn = (turn+1)%playerNumber;
                        }
                    }
                    else {
                        //checking who won
                        game.countVictoryPoints();
                        synchronized (clients){
                            for(ClientHandler CH : clients){
                                for(ClientHandler CH1 : clients) {
                                    CH.sendObject(new VictoryPointsMessage(game.getBoard(CH1.getNickname()).getVictoryPoints(), CH1.getNickname()));
                                }
                            }
                        }
                        synchronized (Lock){
                            state = LobbyState.demolished;
                        }
                    }
                    break;
                }

                case playSolo: {
                    if(!(endGame  || lorenzoWin)){
                        ClientHandler CH;
                        synchronized (clients) {
                            CH = clients.get(0);
                        }

                        synchronized (CH) {
                            CH.setState(ClientHandler.ClientHandlerState.myTurn);
                            CH.sendStandardMessage(StandardMessages.yourTurn);
                        }

                        while (!CH.getState().equals(ClientHandler.ClientHandlerState.disconnected) && !CH.getState().equals(ClientHandler.ClientHandlerState.notMyTurn)) { }

                        endGame = game.checkEndGame(0);
                        if(!endGame){
                            synchronized (Lock){
                                state = LobbyState.lorenzoTurn;
                            }
                        }
                    }
                    else{
                        game.countVictoryPoints();
                        synchronized (clients) {
                            ClientHandler CH = clients.get(0);
                            clients.get(0).sendStandardMessage(StandardMessages.endGame);
                            if (endGame)
                                CH.sendObject(new VictoryPointsMessage(game.getBoard(0).getVictoryPoints(), clients.get(0).getNickname()));
                            else CH.sendStandardMessage(StandardMessages.lorenzoWin);
                        }
                        synchronized (Lock){
                            state = LobbyState.demolished;
                        }
                    }
                    break;
                }

                case lorenzoTurn: {
                    game.activatedToken();
                    lorenzoWin = game.checkLorenzoWin();
                    synchronized (Lock){
                        state = LobbyState.playSolo;
                    }
                    break;
                }

                case demolished:{
                    for (ClientHandler CH : clients) {
                        CH.setState(ClientHandler.ClientHandlerState.disconnected);
                        CH.closeConnection();
                    }
                    lobbyNotify();
                    synchronized (Lock){
                        state = LobbyState.demolished;
                    }
                    break;
                }
            }
        }while(LS!=LobbyState.demolished);
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
                state = LobbyState.demolished;
            }
        } else {
            synchronized (clients) {
                ClientHandler client = (ClientHandler) obs;
                disconnectedPlayers.put(client.getNickname(), clientMap.get(client).getValue());
                clientMap.remove(client);
                for (ClientHandler CH : clients) {
                    if (!disconnectedPlayers.containsKey(CH.getNickname())) {
                        CH.sendObject(new DisconnectedMessage(client.getNickname()));
                    }
                }
            }

            synchronized (Lock){
                if(disconnectedPlayers.size() == playerNumber){
                    state = LobbyState.demolished;
                }
            }
        }
    }

    @Override
    public void updateLCDiscard(CHObservable obs, DiscardLeaderCardSetupMessage message) {
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

                //after getting the resources, the user needs to say where he wants to deposit them
                client.setLastActionMarket(true);
                client.setState(ClientHandler.ClientHandlerState.moveNeeded);
            } catch (Exception e) {
                client.sendStandardMessage(StandardMessages.wrongObject);
            }
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
                game.playLeaderCard(player, message.getIndex());
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
                game.discardLeaderCard(player, message.getIndex());
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
