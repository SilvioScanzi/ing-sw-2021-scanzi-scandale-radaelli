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
            else if (!clientHandlers.remove(CH)) {
                Pair<GameHandler, Boolean> P = gameHandlerMap.get(CH.getNickname());
                if (P.getKey().equals(currentGameHandler)) {
                    gameHandlerMap.remove(CH.getNickname());
                    currentGameHandler.removePlayer(CH);
                } else {
                    P.setValue(false);
                    gameHandlerMap.put(CH.getNickname(), P);
                }
            }
        }
        System.out.println("[SERVER] Client " + CH.getNickname() + " has disconnected from the game");
    }

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

    @Override
    public void gameHandlerUpdate(GameHandlerObservable obs) {
        synchronized (clientHandlers){
            for(String S : ((GameHandler)obs).getAllNicknames()){
                gameHandlerMap.remove(S);
            }
        }
        System.out.println("[SERVER] A game containing "+((GameHandler)obs).getPlayerNumber()+" players has been demolished");
    }

    private void startNewGameHandler(ClientHandler CH) {
        synchronized (CH) {
            CH.setState(ClientHandler.ClientHandlerState.playerNumber);
            CH.sendStandardMessage(StandardMessages.choosePlayerNumber);
        }
        synchronized (Lock) {
            gameHandlerRequired = false;
        }
    }

    @Override
    public void updateServerReconnection(CHObservable obs) {
        ClientHandler CH = (ClientHandler) obs;
        String S = CH.getNickname();
        gameHandlerMap.get(S).setValue(true);
        gameHandlerMap.get(S).getKey().addPlayer(CH);
    }
}
