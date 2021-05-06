package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.ChoosePlayerNumberMessage;
import it.polimi.ingsw.network.messages.StandardMessages;
import it.polimi.ingsw.observers.CHObservable;
import it.polimi.ingsw.observers.CHObserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.ArrayList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.lang.Object.*;

public class Server implements CHObserver {

    private Lobby currentLobby;
    private ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private boolean first = true;
    private boolean CHDisconnected = false;
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
                executor.submit(CH);
                CH.addObserver(this);
                synchronized (clientHandlers) {
                    clientHandlers.add(CH);
                    clientHandlers.notifyAll();
                }
                if(first) {
                    new Thread (this::lobbyManager).start();
                }
                first = false;
            } catch(IOException e) {
                break;
            }
        }
        executor.shutdown();
    }

    public void lobbyManager() {
        int players = 0;

        //first player to connect gets to choose how many are the players
        clientHandlers.get(0).sendStandardMessage(StandardMessages.choosePlayerNumber);
        while(!(CHDisconnected || numberChosen)){

        }
        if(numberChosen && !CHDisconnected){
            //TODO:seleziona il numero
            System.out.println("[SERVER] Player number chosen");

            //starting the lobby with the requested number of players
            synchronized (clientHandlers) {
                currentLobby = new Lobby(players);
                currentLobby.addPlayer(clientHandlers.remove(0));
                while (currentLobby.getAddedPlayers() < currentLobby.getPlayerNumber()) {
                    while (clientHandlers.size() == 0) {
                        try {
                            clientHandlers.wait();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    currentLobby.addPlayer(clientHandlers.remove(0));
                }
            }
            new Thread(currentLobby).start();

            System.out.println("[SERVER] Lobby started");
        }
        /*synchronized (clientHandlers.get(0)) {
            clientHandlers.get(0).setSetPlayerNumber(true);
            while (!clientHandlers.get(0).getMessageReady()) {
                try {
                    clientHandlers.get(0).wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ChoosePlayerNumberMessage message = (ChoosePlayerNumberMessage) clientHandlers.get(0).getMessage();
            clientHandlers.get(0).setMessageReady(false);
            players = message.getN();
        }*/

        //others waiting so a new lobby gets initialized...
        if (clientHandlers.size() > 0) new Thread(this::lobbyManager).start();
        else first = true;  //...otherwise waits for others to connect
    }

    public void update(CHObservable obs, Object obj){
        if(obs instanceof ClientHandler && obj instanceof ClientHandler){
            clientHandlers.remove(obj);
            CHDisconnected = true;
        }
    }
}
