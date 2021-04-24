package it.polimi.ingsw.server;

import it.polimi.ingsw.messages.ChoosePlayerNumberMessage;
import it.polimi.ingsw.messages.StandardMessages;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private Lobby currentLobby;
    private ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private boolean first = true;

    public void startServer() {
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(9090);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.println("Server ready");

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("Connessione accettata");
                ClientHandler CH = new ClientHandler(socket);
                executor.submit(CH);
                synchronized (clientHandlers) {
                    clientHandlers.add(CH);
                    clientHandlers.notifyAll();
                }
                if(first) {
                    new Thread (() ->lobbyManager()).start();
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
        synchronized (clientHandlers.get(0)) {
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
        }

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
        new Thread(() -> currentLobby.run()).start();

        //others waiting so a new lobby gets initialized...
        if (clientHandlers.size() > 0) new Thread(()->lobbyManager()).start();
        else first = true;  //...otherwise waits for others to connect
    }
}
