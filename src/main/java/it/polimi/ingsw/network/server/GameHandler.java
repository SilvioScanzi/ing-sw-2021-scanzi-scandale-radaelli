package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.commons.Pair;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.observers.CHObservable;
import it.polimi.ingsw.observers.CHObserver;
import it.polimi.ingsw.observers.GameHandlerObservable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class GameHandler extends GameHandlerObservable implements CHObserver {
    private int playersDone = 0;
    //Used to access playersDone and other variables (synchronization)
    private final Object Lock = new Object();
    private final int playerNumber;
    private final Game game;
    //ArrayList of client handlers which are connected to this lobby
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    //Map: ClientHandler associated with his Name and his Position in the game
    private final HashMap<ClientHandler, Pair<String,Integer>> clientMap = new HashMap<>();
    //Map: Name of the disconnected players associated with his Position in the game
    private final HashMap<String, Integer> disconnectedPlayers = new HashMap<>();
    private final HashMap<String,Integer> nameMap = new HashMap<>();

    private Timer t;

    private int turn;
    private boolean endGame = false;
    private boolean started = false;
    private boolean demolished = false;

    public GameHandler(int playerNumber){
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
        if(!started){
            //no need for synchronization because this first add is only done in one thread (lobbyManager)
            clients.add(CH);
        }
        else {
            if(disconnectedPlayers.size() == playerNumber) {
                t.cancel();
            }

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
    }

    public void removePlayer(ClientHandler CH){
        synchronized (clients) {
            clients.remove(CH);
        }
    }

    public void start(){
        game.setInkwell((int)(Math.random() * playerNumber));
        System.out.println("[SERVER] A game containing "+playerNumber+" players is starting");
        for(ClientHandler CH : clients){
            CH.sendStandardMessage(StandardMessages.welcomeMessage);
        }

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

        for (ClientHandler C : clientMap.keySet()) {
            nameMap.put(clientMap.get(C).getKey(),clientMap.get(C).getValue());
        }

        for (ClientHandler CH : clients) {
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
    }

    @Override
    public void updateDisconnected(CHObservable obs){
        ClientHandler client = (ClientHandler) obs;
        if(demolished) return;
        if (!started) {
            synchronized (clients) {
            for (ClientHandler CH : clients) {
                    if(!CH.equals(client)) {
                        CH.sendStandardMessage(StandardMessages.fatalError);
                        CH.closeConnection();
                    }
                }
            }
            demolished = true;
            gameHandlerNotify();
        } else {
            synchronized (clients) {
                synchronized (Lock) {
                    disconnectedPlayers.put(client.getNickname(), clientMap.get(client).getValue());
                    if(turn == clientMap.get(client).getValue() && disconnectedPlayers.size() != playerNumber){
                        turn = (turn + 1)%playerNumber;
                        getNextActivePlayer();
                    }
                    clientMap.remove(client);
                }
                for (ClientHandler CH : clients) {
                    if (!disconnectedPlayers.containsKey(CH.getNickname())) {
                        CH.sendObject(new DisconnectedMessage(client.getNickname()));
                    }
                }
            }

            synchronized (clients){
                if(disconnectedPlayers.size() == playerNumber){
                    t = new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            demolished = true;
                            gameHandlerNotify();
                            t.cancel();
                        }
                    },600000);  //timer set to 10 minutes for reconnection
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
                if(playerNumber>1) {
                    boolean flag = false;
                    synchronized (Lock) {
                        playersDone++;
                        if (playersDone == playerNumber) {
                            flag = true;
                            playersDone = 0;
                        }
                    }
                    if (flag) {
                        int k = 1;
                        for (int i = (game.getInkwell() + 1) % playerNumber; i != game.getInkwell(); i = (i + 1) % playerNumber) {
                            synchronized (clients.get(i)) {
                                clients.get(i).setState(ClientHandler.ClientHandlerState.finishingSetup);
                            }
                            if (k == 1 || k == 2) {
                                clients.get(i).sendStandardMessage(StandardMessages.chooseOneResource);
                            } else if (k == 3) {
                                clients.get(i).sendStandardMessage(StandardMessages.chooseTwoResource);
                            }
                            k++;
                        }
                        clients.get(game.getInkwell()).sendStandardMessage(StandardMessages.wait);
                        clients.get(game.getInkwell()).setState(ClientHandler.ClientHandlerState.wait);
                    }
                    else{
                        client.sendStandardMessage(StandardMessages.wait);
                        client.setState(ClientHandler.ClientHandlerState.wait);
                    }
                }
                else{
                    started = true;
                    turn = 0;
                    client.setState(ClientHandler.ClientHandlerState.myTurn);
                    client.sendStandardMessage(StandardMessages.yourTurn);
                }
            }catch (Exception e) {
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

                boolean flag = false;
                synchronized (Lock) {
                    playersDone++;
                    if(playersDone == playerNumber - 1){
                        flag = true;
                        playersDone = 0;
                    }
                }
                if(flag){
                    turn = game.getInkwell();
                    synchronized (clients.get(turn)) {
                        clients.get(turn).setState(ClientHandler.ClientHandlerState.myTurn);
                        clients.get(turn).sendStandardMessage(StandardMessages.yourTurn);
                    }
                    started = true;
                    for(ClientHandler CH : clients){
                        if(!CH.equals(clients.get(turn))){
                            synchronized (CH){
                                CH.sendStandardMessage(StandardMessages.notYourTurn);
                                CH.setState(ClientHandler.ClientHandlerState.notMyTurn);
                            }
                        }
                    }
                }
                else{
                    client.sendStandardMessage(StandardMessages.wait);
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
        synchronized (client) {
            client.sendStandardMessage(StandardMessages.notYourTurn);
            client.setState(ClientHandler.ClientHandlerState.notMyTurn);
        }

        if(!endGame){
            endGame = game.checkEndGame(clientMap.get(client).getValue());
        }

        if(endGame && playerNumber==1){
            game.countVictoryPoints();
            client.sendObject(new VictoryPointsMessage(game.getBoard(0).getVictoryPoints(),game.getBoard(0).getNickname()));
            client.closeConnection();
            demolished = true;
            gameHandlerNotify();
        }

        else if(endGame && playerNumber>1){
            turn = (turn + 1) % playerNumber;
            if (turn == game.getInkwell()) {
                game.countVictoryPoints();
                synchronized (clients) {
                    for (ClientHandler CH : clients) {
                        CH.sendObject(new VictoryPointsMessage(game.getBoard(0).getVictoryPoints(), game.getBoard(0).getNickname()));
                        CH.closeConnection();
                    }
                    demolished = true;
                    gameHandlerNotify();
                }
            }else{
                getNextActivePlayer();
            }
        }
        else if(playerNumber == 1){
            game.activatedToken();
            if(game.checkLorenzoWin()){
                client.sendStandardMessage(StandardMessages.lorenzoWin);
                client.closeConnection();
                demolished = true;
                gameHandlerNotify();
            }
        }
        else  {
            turn = (turn + 1) % playerNumber;
            getNextActivePlayer();
        }
    }

    private void getNextActivePlayer() {
        int i = turn - 1;
        if(i<0){i = playerNumber-1;}
        boolean found = false;
        synchronized (clients) {
            do {
                if (!disconnectedPlayers.containsKey(clients.get(turn).getNickname())) found = true;
                else{ turn = (turn + 1) % playerNumber; }
            } while (turn != i && !found);
        }
        synchronized(clients.get(turn)){
            clients.get(turn).setState(ClientHandler.ClientHandlerState.myTurn);
            clients.get(turn).sendStandardMessage(StandardMessages.yourTurn);
        }
    }

    public ArrayList<String> getAllNicknames(){
        ArrayList<String> tmp = new ArrayList<>();
        synchronized (Lock){
            for(ClientHandler CH : clientMap.keySet()){
                tmp.add(clientMap.get(CH).getKey());
            }
            tmp.addAll(disconnectedPlayers.keySet());
        }
        return tmp;
    }
}
