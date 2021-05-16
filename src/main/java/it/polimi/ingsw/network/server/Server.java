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
    private int numberChosen = 0;

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

    public void lobbyManager() {
        int players;
        ClientHandler CH = clientHandlers.get(0);
        currentLobby = null;
        numberChosen = 0;

        //first player to connect gets to choose how many are the players
        synchronized (CH) {
            CH.sendStandardMessage(StandardMessages.choosePlayerNumber);
            if(!CH.getState().equals(ClientHandler.ClientHandlerState.disconnected)) CH.setState(ClientHandler.ClientHandlerState.playerNumber);
        }

        while(!(CH.getState().equals(ClientHandler.ClientHandlerState.disconnected) || numberChosen!=0)){}

        if(numberChosen!=0 && !CH.getState().equals(ClientHandler.ClientHandlerState.disconnected)){
            players = numberChosen;
            System.out.println("[SERVER] Player number chosen");

            //starting the lobby with the requested number of players
            synchronized (clientHandlers) {
                currentLobby = new Lobby(players);
                currentLobby.addObserver(this);
                lobbyMap.put(clientHandlers.get(0).getNickname(), new Pair<>(currentLobby, true));
                currentLobby.addPlayer(clientHandlers.remove(0));
                while (currentLobby.getAddedPlayers() < currentLobby.getPlayerNumber()) {
                    while (clientHandlers.size() == 0) {
                        try {
                            clientHandlers.wait();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    lobbyMap.put(clientHandlers.get(0).getNickname(), new Pair<>(currentLobby, true));
                    currentLobby.addPlayer(clientHandlers.remove(0));
                }
            }
            new Thread(currentLobby).start();
            System.out.println("[SERVER] Lobby started");
        }
        else{
            synchronized (clientHandlers){
                clientHandlers.remove(CH);
            }
        }

        //others waiting so a new lobby gets initialized...
        synchronized (clientHandlers) {
            if (clientHandlers.size() > 0) new Thread(this::lobbyManager).start();
            else lobbyRequired = true;  //...otherwise waits for others to connect
        }
    }

    @Override
    public void updateServerDisconnection(CHObservable obs){
        ClientHandler CH = (ClientHandler) obs;

        synchronized (clientHandlers) {
            if (!clientHandlers.remove(CH)) {
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
        System.out.println("[SERVER] " + ((CH.getNickname()==null)? "Anonymous client":"Client " +
                CH.getNickname()) + " has disconnected from the game");
    }

    @Override
    public void updateServerPlayerNumber(CHObservable obs,ChoosePlayerNumberMessage message){
        numberChosen = message.getN();
    }

    @Override
    public void updateServerNickname(CHObservable obs,NicknameMessage message){
        ClientHandler CH = (ClientHandler) obs;
        ArrayList<String> currPlayers;

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
                    CH.setState(ClientHandler.ClientHandlerState.playerNumber);
                    new Thread(this::lobbyManager).start();
                }
                lobbyRequired = false;
                clientHandlers.notifyAll();
            }
        }
    }

    @Override
    public void lobbyUpdate(LobbyObservable obs) {
        synchronized (clientHandlers){
            lobbyMap.remove(obs);
        }
    }
}
