package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.Pair;
import it.polimi.ingsw.network.messages.ChoosePlayerNumberMessage;
import it.polimi.ingsw.network.messages.NicknameMessage;
import it.polimi.ingsw.network.messages.StandardMessages;
import it.polimi.ingsw.observers.CHObservable;
import it.polimi.ingsw.observers.CHObserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server implements CHObserver {

    private Lobby currentLobby;
    private final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    //Hashmap contains the nickname of the player as the key and a pair which defines his lobby and the current state (true if connected, false if disconnected)
    private final HashMap<String, Pair<Lobby,Boolean>> lobbyMap = new HashMap<>();
    private boolean lobbyRequired = true;
    private boolean numberChosen = false;

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
                CH.addObserver(this);
                CH.sendStandardMessage(StandardMessages.chooseNickName);
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

        //first player to connect gets to choose how many are the players
        synchronized (CH) {
            CH.sendStandardMessage(StandardMessages.choosePlayerNumber);
            if(!CH.getState().equals(ClientHandler.ClientHandlerState.disconnected)) CH.setState(ClientHandler.ClientHandlerState.playerNumber);
        }
        ClientHandler.ClientHandlerState state;

        do{
            synchronized (CH){
                 state = CH.getState();
            }
        }while(!(state.equals(ClientHandler.ClientHandlerState.disconnected) || numberChosen));

        if(numberChosen && !state.equals(ClientHandler.ClientHandlerState.disconnected)){
            synchronized (CH){
                players = ((ChoosePlayerNumberMessage) CH.getMessage()).getN();
                CH.setState(ClientHandler.ClientHandlerState.lobbyNotReady);
                CH.setMessageReady(false);
            }
            System.out.println("[SERVER] Player number chosen");

            //starting the lobby with the requested number of players
            synchronized (clientHandlers) {
                currentLobby = new Lobby(players);
                lobbyMap.put(clientHandlers.get(0).getNickname(),new Pair<>(currentLobby,true));
                currentLobby.addPlayer(clientHandlers.remove(0));
                while (currentLobby.getAddedPlayers() < currentLobby.getPlayerNumber()) {
                    while (clientHandlers.size() == 0) {
                        try {
                            clientHandlers.wait();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    lobbyMap.put(clientHandlers.get(0).getNickname(),new Pair<>(currentLobby,true));
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

    public void update(CHObservable obs, Object obj) {

        if (obs instanceof ClientHandler) {

            //Disconnection
            ClientHandler CH = (ClientHandler) obs;
            if (obj instanceof ClientHandler) {
                synchronized (clientHandlers) {
                    if (!clientHandlers.remove(obj)) {
                        Pair<Lobby, Boolean> P = lobbyMap.get(CH.getNickname());
                        if (P.getKey().equals(currentLobby)) {
                            lobbyMap.remove(CH.getNickname());
                        } else {
                            P.setValue(false);
                            lobbyMap.put(CH.getNickname(), P);
                        }
                        currentLobby.removePlayer(CH);
                    }
                }
                System.out.println("[SERVER] " + ((CH.getNickname()!=null)? "Anonymous client":"Client "+CH.getNickname()) + " has disconnected from the game");
            }

            //Player Number is chosen for the current lobby
            else if (obj instanceof ChoosePlayerNumberMessage) {
                numberChosen = true;
            }

            //Player has chosen a nickname
            else if (obj instanceof NicknameMessage) {
                String S = ((NicknameMessage) obj).getNickname();
                if (lobbyMap.containsKey(S) && lobbyMap.get(S).getValue()) {
                    ((ClientHandler) obs).sendStandardMessage(StandardMessages.nicknameAlreadyInUse);
                } else if (lobbyMap.containsKey(S)) {
                    //TODO:Riconnesione
                    System.out.println("[SERVER] Client " + S + " is trying to reconnect");
                } else {
                    synchronized (clientHandlers) {
                        System.out.println("[SERVER] Client " + S + " is ready to play");
                        CH.setState(ClientHandler.ClientHandlerState.lobbyNotReady);
                        CH.setNickname(S);
                        clientHandlers.add((ClientHandler) obs);
                        if (lobbyRequired) {
                            new Thread(this::lobbyManager).start();
                        }
                        lobbyRequired = false;
                        clientHandlers.notifyAll();
                    }
                }
            }
        }
    }

}
