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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class Server implements CH_ServerObserver, LobbyObserver {

    private Lobby currentLobby;
    //ArrayList clientHandlers also used as Lock when accessing lobbyMap
    private final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    //Hashmap contains the nickname of the player as the key and a pair which defines his lobby and the current state (true if connected, false if disconnected)
    private final HashMap<String, Pair<Lobby,Boolean>> lobbyMap = new HashMap<>();
    private boolean lobbyRequired = true;
    private final Object Lock = new Object();


    public void startServer() {
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(9090);
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
                executor.submit(CH);
            } catch(IOException e) {
                System.out.println("[SERVER] Fatal Error");
                break;
            }
        }
        executor.shutdown();
    }

    @Override
    public void updateServerDisconnection(CHObservable obs){
        ClientHandler CH = (ClientHandler) obs;

        synchronized (clientHandlers) {
            if(CH.getState().equals(ClientHandler.ClientHandlerState.nickname)){
                System.out.println("[SERVER] Anonymous client has disconnected from the game");
                return;
            }
            //if disconnected player was choosing the number of players in the lobby
            else if(CH.getState().equals(ClientHandler.ClientHandlerState.playerNumber)){
                clientHandlers.remove(CH);
                if(clientHandlers.size()>0) startNewLobby(clientHandlers.get(0));
                else lobbyRequired = true;
            }
            //if disconnected players is in a lobby
            else if (!clientHandlers.remove(CH)) {
                Pair<Lobby, Boolean> P = lobbyMap.get(CH.getNickname());
                if (P.getKey().equals(currentLobby)) {
                    lobbyMap.remove(CH.getNickname());
                    currentLobby.removePlayer(CH);
                } else {
                    P.setValue(false);
                    lobbyMap.put(CH.getNickname(), P);
                }
            }
        }
        System.out.println("[SERVER] Client " + CH.getNickname() + " has disconnected from the game");
    }

    @Override
    public void updateServerPlayerNumber(CHObservable obs,ChoosePlayerNumberMessage message) {
        System.out.println("[SERVER] Player number chosen");
        //timer.cancel();
        boolean started = false;
        int playerNumber = message.getN();
        synchronized (Lock) {
            currentLobby = new Lobby(playerNumber);
            currentLobby.addObserver(this);
            lobbyMap.put(clientHandlers.get(0).getNickname(), new Pair<>(currentLobby, true));
            currentLobby.addPlayer((ClientHandler) obs);
            ((ClientHandler) obs).sendStandardMessage(StandardMessages.gameIsStarting);
            if (currentLobby.getAddedPlayers() == playerNumber) {
                currentLobby.start();
                started = true;
            }
        }
        synchronized (clientHandlers) {
            clientHandlers.remove(0);
            if (!started && clientHandlers.size() > 0) {
                synchronized (Lock) {
                    while (currentLobby.getAddedPlayers() < playerNumber && clientHandlers.size() > 0) {
                        currentLobby.addPlayer(clientHandlers.get(0));
                        clientHandlers.get(0).sendStandardMessage(StandardMessages.gameIsStarting);
                        lobbyMap.put(clientHandlers.get(0).getNickname(), new Pair<>(currentLobby, true));
                        clientHandlers.remove(0);
                    }
                    if (currentLobby.getAddedPlayers() == playerNumber) {
                        currentLobby.start();
                        currentLobby = null;
                        lobbyRequired = true;
                        if(clientHandlers.size()>0){
                            startNewLobby(clientHandlers.get(0));
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
        Lobby lobby;

        synchronized (Lock){
             lobby = currentLobby;
        }

        synchronized (clientHandlers) {
            currPlayers = new ArrayList<>(clientHandlers.stream().map(ClientHandler::getNickname).collect(Collectors.toList()));
            String S = message.getNickname();

            if ((lobbyMap.containsKey(S) && lobbyMap.get(S).getValue()) || currPlayers.contains(S)) {
                ((ClientHandler) obs).sendStandardMessage(StandardMessages.nicknameAlreadyInUse);
            } else if (lobbyMap.containsKey(S)) {
                lobbyMap.get(S).setValue(true);
                CH.setNickname(S);
                lobbyMap.get(S).getKey().addPlayer(CH);
                System.out.println("[SERVER] Client " + S + " is trying to reconnect");
            } else {
                System.out.println("[SERVER] Client " + S + " is ready to play");
                CH.setState(ClientHandler.ClientHandlerState.lobbyNotReady);
                CH.setNickname(S);
                clientHandlers.add((ClientHandler) obs);
                if (lobbyRequired) {
                    startNewLobby(CH);
                }
                //if there is already a lobby which is ready
                else if(lobby != null){
                    lobbyMap.put(CH.getNickname(), new Pair<>(currentLobby, true));
                    currentLobby.addPlayer(CH);
                    lobbyMap.put(clientHandlers.get(0).getNickname(), new Pair<>(currentLobby, true));
                    clientHandlers.remove(0);
                    CH.sendStandardMessage(StandardMessages.gameIsStarting);
                    if(lobby.getAddedPlayers()==lobby.getPlayerNumber()){
                        synchronized (Lock){
                            currentLobby.start();
                            currentLobby = null;
                            lobbyRequired = true;
                            if(clientHandlers.size()>0){
                                startNewLobby(clientHandlers.get(0));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void lobbyUpdate(LobbyObservable obs) {
        synchronized (clientHandlers){
            for(String S : ((Lobby)obs).getAllNicknames()){
                lobbyMap.remove(S);
            }
        }
        System.out.println("[SERVER] A lobby containing "+((Lobby)obs).getPlayerNumber()+" players has been demolished");
    }

    private void startNewLobby(ClientHandler CH) {
        CH.sendStandardMessage(StandardMessages.choosePlayerNumber);
        CH.setState(ClientHandler.ClientHandlerState.playerNumber);
        lobbyRequired = false;
        /*timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (clientHandlers) {
                    clientHandlers.get(0).closeConnection();
                    clientHandlers.remove(0);
                    if (clientHandlers.size() > 0) {
                        clientHandlers.get(0).setState(ClientHandler.ClientHandlerState.playerNumber);
                        clientHandlers.get(0).sendStandardMessage(StandardMessages.choosePlayerNumber);
                    } else lobbyRequired = true;
                }
            }
        }, 10000);*/
    }
}
