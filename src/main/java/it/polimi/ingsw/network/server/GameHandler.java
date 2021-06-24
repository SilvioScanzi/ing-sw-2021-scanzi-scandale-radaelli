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
    private boolean reconnect = false;

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
        if (!started) {
            //no need for synchronization because this first add is only done in one thread (lobbyManager)
            clients.add(CH);
        } else if (reconnect && disconnectedPlayers.size() == 1) {
            synchronized (clients) {
                int n = disconnectedPlayers.get(CH.getNickname());
                clients.set(n, CH);
                clientMap.put(CH, new Pair<>(CH.getNickname(), disconnectedPlayers.get(CH.getNickname())));
                disconnectedPlayers.remove(CH.getNickname());
                t.cancel();
                t.purge();

                for (ClientHandler c : clients) {
                    reconnectCH(c);
                }
                reconnect = false;
            }
            getNextActivePlayer(false);
        } else if (reconnect) {
            synchronized (clients) {
                int n = disconnectedPlayers.get(CH.getNickname());
                clients.set(n, CH);
                clientMap.put(CH, new Pair<>(CH.getNickname(), disconnectedPlayers.get(CH.getNickname())));
                disconnectedPlayers.remove(CH.getNickname());
                CH.sendStandardMessage(StandardMessages.waitForReconnection);
            }
        } else {
            synchronized (clients) {
                for (ClientHandler clientHandler : clients) {
                    if (!clientHandler.equals(CH) && !(disconnectedPlayers.containsKey(clientHandler.getNickname())))
                        clientHandler.sendObject(new ReconnectMessage(CH.getNickname()));
                }
                int n = disconnectedPlayers.get(CH.getNickname());
                clients.set(n, CH);
                clientMap.put(CH, new Pair<>(CH.getNickname(), disconnectedPlayers.get(CH.getNickname())));
                disconnectedPlayers.remove(CH.getNickname());
                reconnectCH(CH);
            }

        }
    }

    public void reconnectCH(ClientHandler CH){
        CH.setState(ClientHandler.ClientHandlerState.notMyTurn);

        //attaching observers
        CH.addObserver(this);
        game.addObserver(CH);

        //sending game status to the reconnected player
        synchronized (game){
            CH.sendObject(new NicknameMapMessage(CH.getNickname(),nameMap,game.getInkwell()));
            CH.sendObject(new LCMapMessage(game.getLCMap(CH.getNickname())));
            CH.sendObject(new ResourceMarketMessage(game.getMarket().getGrid(),game.getMarket().getRemainingMarble()));
            CH.sendObject(new DCMarketMessage(game.getDevelopmentCardMarket()));
            CH.sendObject(new LeaderCardHandMessage(game.getBoard(CH.getNickname()).getLeaderCardsHand()));
            for(int i=0; i<playerNumber; i++) {
                Board playerBoard = game.getBoard(i);
                CH.sendObject(new FaithTrackMessage(playerBoard.getFaithTrack(), playerBoard.getNickname()));
                CH.sendObject(new WarehouseMessage(playerBoard.getWarehouse(), playerBoard.getNickname()));
                CH.sendObject(new StrongboxMessage(playerBoard.getStrongbox(), playerBoard.getNickname()));
                CH.sendObject(new SlotMessage(playerBoard.getSlots(), playerBoard.getNickname()));
                if(!playerBoard.equals(game.getBoard(CH.getNickname()))) {
                    for (int j = playerBoard.getLeaderCardsHand().size(); j < 4; j++) {
                        CH.sendObject(new LeaderCardHandUpdateMessage(playerBoard.getNickname()));
                    }
                }
                CH.sendObject(new LeaderCardPlayedMessage(playerBoard.getLeaderCardsPlayed(), playerBoard.getNickname()));
            }
        }

        CH.sendStandardMessage(StandardMessages.notYourTurn);
    }

    public void removePlayer(ClientHandler CH){
        synchronized (clients) {
            clients.remove(CH);
        }
    }

    public void start(){
        game.setInkwell((int)(Math.random() * (playerNumber)));
        System.out.println("[SERVER] A game containing "+playerNumber+" players is starting");

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
        game.removeObserver(client);
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
                game.getBoard(client.getNickname()).setMoveNeeded(false);
                game.getBoard(client.getNickname()).setActionDone(false);
                game.getBoard(client.getNickname()).getHand().clear();
                client.closeConnection();
                synchronized (Lock) {
                    disconnectedPlayers.put(client.getNickname(), clientMap.get(client).getValue());
                    if(turn == clientMap.get(client).getValue() && disconnectedPlayers.size() != playerNumber){
                        turn = (turn + 1)%playerNumber;
                        getNextActivePlayer(false);
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
                    reconnect = true;
                    t = new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            demolished = true;
                            gameHandlerNotify();
                            t.cancel();
                            t.purge();
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
                client.sendObject(new LCMapMessage(game.getLCMap(client.getNickname())));
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
            }catch (IndexOutOfBoundsException e) {
                client.sendStandardMessage(StandardMessages.leaderCardOutOfBounds);
            }catch(IllegalArgumentException e){
                client.sendStandardMessage(StandardMessages.leaderCardWrongFormat);
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
            } catch (IllegalArgumentException e) {
                client.sendStandardMessage(StandardMessages.resourceParseError);
            }
        }
    }

    @Override
    public void updateBuyResources(CHObservable obs, BuyResourcesMessage message) {
        ClientHandler client = (ClientHandler) obs;
        synchronized (clients) {
            int player = clientMap.get(client).getValue();
            synchronized (client) {
                try {
                    game.BuyMarketResourcesAction(player, message.getRow(), message.getN(), message.getRequestedWMConversion());
                } catch (ActionAlreadyDoneException e) {
                    client.sendStandardMessage(StandardMessages.actionAlreadyDone);
                } catch (IndexOutOfBoundsException e) {
                    client.sendStandardMessage(StandardMessages.indexOutOfBound);
                } catch (IllegalArgumentException e) {
                    client.sendStandardMessage(StandardMessages.whiteMarbleNotCongruent);
                }
            }
        }
    }

    @Override
    public void updateBuyDC(CHObservable obs, BuyDevelopmentCardMessage message) {
        ClientHandler client = (ClientHandler) obs;
        synchronized (clients) {
            int player = clientMap.get(client).getValue();
            synchronized (client) {
                try {
                    game.BuyDevelopmentCardAction(message.getC(), message.getLevel(), player, message.getSlotNumber(), message.getUserChoice());
                } catch (Exception e){
                    client.sendStandardMessage(StandardMessages.buyDevelopmentWrong);
                }
            }
        }
    }

    @Override
    public void updateProduction(CHObservable obs, ProductionMessage message) {
        ClientHandler client = (ClientHandler) obs;
        synchronized (clients) {
            int player = clientMap.get(client).getValue();
            synchronized (client) {
                try {
                    if(message.getUserChoice().size()==0){
                        client.sendStandardMessage(StandardMessages.activateProductionWrong);
                    }
                    else {
                        game.activateProductionAction(player, message.getUserChoice());
                    }
                } catch (Exception e) {
                    client.sendStandardMessage(StandardMessages.activateProductionWrong);
                }
            }
        }
    }

    @Override
    public void updateMoveResources(CHObservable obs, MoveResourcesMessage message) {
        ClientHandler client = (ClientHandler) obs;
        synchronized (clients) {
            int player = clientMap.get(client).getValue();
            synchronized (client) {
                try {
                    game.moveResources(player, message.getUserChoice());
                } catch (Exception e) {
                    client.sendStandardMessage(StandardMessages.moveActionWrong);
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
            } catch (RequirementsNotMetException e){
                client.sendStandardMessage(StandardMessages.requirementsNotMet);
            }catch(IndexOutOfBoundsException e) {
                client.sendStandardMessage(StandardMessages.leaderCardOutOfBounds);
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
                client.sendStandardMessage(StandardMessages.leaderCardOutOfBounds);
            }
        }
    }

    @Override
    public void updateTurnDone(CHObservable obs){
        ClientHandler client = (ClientHandler) obs;
        if(game.getBoard(client.getNickname()).getActionDone() && !game.getBoard(client.getNickname()).getMoveNeeded()) {
            game.getBoard(client.getNickname()).setActionDone(false);
            synchronized (client) {
                client.sendStandardMessage(StandardMessages.notYourTurn);
                client.setState(ClientHandler.ClientHandlerState.notMyTurn);
            }

            if (!endGame) {
                endGame = game.checkEndGame();
            }

            if (endGame && playerNumber == 1) {
                if(game.checkLorenzoWin()) client.sendStandardMessage(StandardMessages.lorenzoWin);
                game.countVictoryPoints();
                HashMap<String,Integer> vp = new HashMap<>();
                vp.put(game.getBoard(0).getNickname(),game.getBoard(0).getVictoryPoints());
                client.sendObject(new VictoryPointsMessage(vp));
                demolished = true;
                gameHandlerNotify();
                client.closeConnection();
            } else if (endGame && playerNumber > 1) {
                turn = (turn + 1) % playerNumber;
                if (turn == game.getInkwell()) {
                    game.countVictoryPoints();
                    synchronized (clients) {
                        gameHandlerNotify();
                        for (ClientHandler CH : clients) {
                            HashMap<String,Integer> vp = new HashMap<>();
                            for(int i=0;i<playerNumber;i++){
                                vp.put(game.getBoard(i).getNickname(),game.getBoard(i).getVictoryPoints());
                            }
                            CH.sendObject(new VictoryPointsMessage(vp));
                            CH.closeConnection();
                        }
                        demolished = true;
                    }
                } else {
                    getNextActivePlayer(true);
                    if (turn == game.getInkwell()) {
                        game.countVictoryPoints();
                        synchronized (clients) {
                            gameHandlerNotify();
                            for (ClientHandler CH : clients) {
                                HashMap<String, Integer> vp = new HashMap<>();
                                for (int i = 0; i < playerNumber; i++) {
                                    vp.put(game.getBoard(i).getNickname(), game.getBoard(i).getVictoryPoints());
                                }
                                CH.sendObject(new VictoryPointsMessage(vp));
                                CH.closeConnection();
                            }
                            demolished = true;
                        }
                    }
                }
            } else if (playerNumber == 1) {
                game.activatedToken();
                if (game.checkLorenzoWin()) {
                    client.sendStandardMessage(StandardMessages.lorenzoWin);
                    game.countVictoryPoints();
                    HashMap<String,Integer> vp = new HashMap<>();
                    vp.put(game.getBoard(0).getNickname(),game.getBoard(0).getVictoryPoints());
                    client.sendObject(new VictoryPointsMessage(vp));
                    demolished = true;
                    gameHandlerNotify();
                    client.closeConnection();
                    return;
                }
                synchronized (client) {
                    client.setState(ClientHandler.ClientHandlerState.myTurn);
                }
                client.sendStandardMessage(StandardMessages.yourTurn);

            } else {
                turn = (turn + 1) % playerNumber;
                getNextActivePlayer(false);
            }
        }
        else if(game.getBoard(client.getNickname()).getMoveNeeded()){
            client.sendStandardMessage(StandardMessages.moveActionNeeded);
        }
        else{
            client.sendStandardMessage(StandardMessages.actionNeeded);
        }
    }

    private void getNextActivePlayer(boolean endGame) {
        int i = turn - 1;
        if(i<0){i = playerNumber-1;}
        boolean found = false;
        synchronized (clients) {
            do {
                if (!disconnectedPlayers.containsKey(clients.get(turn).getNickname())) found = true;
                else{ turn = (turn + 1) % playerNumber; }
            } while (turn != i && !found);
        }
        if(!endGame || turn != game.getInkwell()) {
            synchronized (clients.get(turn)) {
                clients.get(turn).setState(ClientHandler.ClientHandlerState.myTurn);
                clients.get(turn).sendStandardMessage(StandardMessages.yourTurn);
            }
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

    public ArrayList<ClientHandler> getClients(){
        return clients;
    }
}
