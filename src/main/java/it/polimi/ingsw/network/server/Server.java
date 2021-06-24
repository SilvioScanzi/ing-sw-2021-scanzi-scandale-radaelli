package it.polimi.ingsw.network.server;

import it.polimi.ingsw.commons.Pair;
import it.polimi.ingsw.network.messages.ChoosePlayerNumberMessage;
import it.polimi.ingsw.network.messages.NicknameMessage;
import it.polimi.ingsw.network.messages.StandardMessages;
import it.polimi.ingsw.observers.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.stream.Collectors;

public class Server implements CH_ServerObserver, GameHandlerObserver {

    private GameHandler currentGameHandler;
    //ArrayList clientHandlers also used as Lock when accessing gameHandlerMap
    private final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    //Hashmap contains the nickname of the player as the key and a pair which defines his game and the current state (true if connected, false if disconnected)
    private final HashMap<String, Pair<GameHandler,Boolean>> gameHandlerMap = new HashMap<>();
    private boolean gameHandlerRequired = true;
    private final Object Lock = new Object();

    /**
     * Method used to run the server.
     * @param port is the port number on which to open the connections.
     */
    public void startServer(int port) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.println("[SERVER] Ready");

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("[SERVER] Connection Established");
                ClientHandler CH = new ClientHandler(socket);
                CH.addServerObserver(this);
                new Thread(CH).start();
            } catch(IOException e) {
                System.out.println("[SERVER] Fatal Error");
                break;
            }
        }
    }


    /**
     * Method used to update the state of clients in the server. When a client disconnects from the server,
     * it is checked if that client was currently in a game. If so, the data structure of the clients is updated.
     * Otherwise, it is checked if the client was currently choosing player number for a new game and, if so, the
     * next active client is notified. If none of these conditions are met, the client is just removed from the current
     * game or from the lobby.
     * @param obs is the client who has disconnected
     */
    @Override
    public void updateServerDisconnection(CHObservable obs){
        ClientHandler CH = (ClientHandler) obs;

        synchronized (clientHandlers) {
            if(CH.getState().equals(ClientHandler.ClientHandlerState.nickname)){
                System.out.println("[SERVER] Anonymous client has disconnected from the game");
                return;
            }
            //if disconnected player was choosing the number of players in the game
            else if(CH.getState().equals(ClientHandler.ClientHandlerState.playerNumber)){
                clientHandlers.remove(CH);
                if(clientHandlers.size()>0) startNewGameHandler(clientHandlers.get(0));
                else gameHandlerRequired = true;
            }
            //if disconnected player is in a game
            else if (!clientHandlers.remove(CH) && gameHandlerMap.containsKey(CH.getNickname())) {
                Pair<GameHandler, Boolean> P = gameHandlerMap.get(CH.getNickname());
                if (P.getKey().equals(currentGameHandler)) {
                    gameHandlerMap.remove(CH.getNickname());
                    currentGameHandler.removePlayer(CH);
                } else{
                    P.setValue(false);
                    gameHandlerMap.put(CH.getNickname(), P);
                }
            }
            else{
                System.out.println("\n\n[SERVER] FATAL ERROR on client\n\n");
            }
        }
        System.out.println("[SERVER] Client " + CH.getNickname() + " has disconnected from the game");
    }

    /**
     * Method used to notify the server that a new game has to be built with a specified number of players.
     * After building it, it is checked if there are connected players who can be inserted in the game.
     * @param obs is the client who has chosen the player number
     * @param message contains the number of players chosen
     */
    @Override
    public void updateServerPlayerNumber(CHObservable obs,ChoosePlayerNumberMessage message) {
        ClientHandler CH = (ClientHandler) obs;
        boolean started = false;
        int playerNumber = message.getN();
        System.out.println("[SERVER] A lobby for "+playerNumber+" players has been built");
        synchronized (Lock) {
            currentGameHandler = new GameHandler(playerNumber);
            currentGameHandler.addObserver(this);
            gameHandlerMap.put(CH.getNickname(), new Pair<>(currentGameHandler, true));
            currentGameHandler.addPlayer(CH);
            CH.sendStandardMessage(StandardMessages.gameIsStarting);
            if (currentGameHandler.getAddedPlayers() == playerNumber) {
                currentGameHandler.start();
                currentGameHandler = null;
                gameHandlerRequired = true;
                started = true;
            }
        }
        synchronized (clientHandlers) {
            clientHandlers.remove(CH);
            if (!started && clientHandlers.size() > 0) {
                synchronized (Lock) {
                    while (currentGameHandler.getAddedPlayers() < playerNumber && clientHandlers.size() > 0) {
                        currentGameHandler.addPlayer(clientHandlers.get(0));
                        clientHandlers.get(0).sendStandardMessage(StandardMessages.gameIsStarting);
                        gameHandlerMap.put(clientHandlers.get(0).getNickname(), new Pair<>(currentGameHandler, true));
                        clientHandlers.remove(0);
                    }
                    if (currentGameHandler.getAddedPlayers() == playerNumber) {
                        currentGameHandler.start();
                        currentGameHandler = null;
                        gameHandlerRequired = true;
                        if(clientHandlers.size()>0){
                            startNewGameHandler(clientHandlers.get(0));
                        }
                    }
                }
            }
        }
    }

    /**
     * Method used to notify the server that a new client is ready to play, with a specified nickname.
     * If there is already a game to be launched, the client is inserted, otherwise the server
     * sends a message to the client and waits for the client to choose the number of players for a new game.
     * @param obs is the new client
     * @param message contains the nickname of the client
     */
    @Override
    public void updateServerNickname(CHObservable obs,NicknameMessage message){
        ClientHandler CH = (ClientHandler) obs;
        ArrayList<String> currPlayers;
        GameHandler gameHandler;

        synchronized (Lock){
             gameHandler = currentGameHandler;
        }
        synchronized (clientHandlers) {
            currPlayers = new ArrayList<>(clientHandlers.stream().map(ClientHandler::getNickname).collect(Collectors.toList()));
            String S = message.getNickname();

            if ((gameHandlerMap.containsKey(S) && gameHandlerMap.get(S).getValue()) || currPlayers.contains(S)) {
                ((ClientHandler) obs).sendStandardMessage(StandardMessages.nicknameAlreadyInUse);
            } else if (gameHandlerMap.containsKey(S)) {
                CH.setNickname(S);
                CH.setState(ClientHandler.ClientHandlerState.reconnecting);
                CH.sendStandardMessage(StandardMessages.reconnection);
                System.out.println("[SERVER] Client " + S + " is trying to reconnect");
            } else {
                System.out.println("[SERVER] Client " + S + " is ready to play");
                CH.setState(ClientHandler.ClientHandlerState.lobbyNotReady);
                CH.setNickname(S);
                clientHandlers.add((ClientHandler) obs);
                if (gameHandlerRequired) {
                    startNewGameHandler(CH);
                }
                //if there is already a game which is ready
                else if(gameHandler != null && gameHandler.getAddedPlayers() < gameHandler.getPlayerNumber()){
                    currentGameHandler.addPlayer(CH);
                    gameHandlerMap.put(CH.getNickname(), new Pair<>(currentGameHandler, true));
                    clientHandlers.remove(CH);
                    CH.sendStandardMessage(StandardMessages.gameIsStarting);
                    if(gameHandler.getAddedPlayers()== gameHandler.getPlayerNumber()){
                        synchronized (Lock){
                            currentGameHandler.start();
                            currentGameHandler = null;
                            gameHandlerRequired = true;
                            if(clientHandlers.size()>0){
                                startNewGameHandler(clientHandlers.get(0));
                            }
                        }
                    }
                }
                else {
                    CH.sendStandardMessage(StandardMessages.gameNotCreated);
                    CH.setState(ClientHandler.ClientHandlerState.gameNotCreated);
                }
            }
        }
    }

    /**
     * Method used to notify the server that a game is done and has to be demolished.
     * @param obs is the finished game
     */
    @Override
    public void gameHandlerUpdate(GameHandlerObservable obs) {
        synchronized (clientHandlers){
            for(ClientHandler CH : ((GameHandler)obs).getClients()){
                synchronized (CH) {
                    CH.clearServer();
                }
            }
            for(String S : ((GameHandler)obs).getAllNicknames()){
                gameHandlerMap.remove(S);
            }
        }
        System.out.println("[SERVER] A game containing "+((GameHandler)obs).getPlayerNumber()+" players has been demolished");
    }

    /**
     * Method used to start a new game
     * @param CH is the client that has to choose the player number for that game
     */
    private void startNewGameHandler(ClientHandler CH) {
        synchronized (CH) {
            CH.setState(ClientHandler.ClientHandlerState.playerNumber);
            CH.sendStandardMessage(StandardMessages.choosePlayerNumber);
        }
        synchronized (Lock) {
            gameHandlerRequired = false;
        }
    }

    /**
     * Method used for the reconnection of a client. The data structure of the clients is updated.
     * @param obs is the client who is reconnecting
     */
    @Override
    public void updateServerReconnection(CHObservable obs) {
        ClientHandler CH = (ClientHandler) obs;
        String S = CH.getNickname();
        gameHandlerMap.get(S).setValue(true);
        gameHandlerMap.get(S).getKey().addPlayer(CH);
    }
}
