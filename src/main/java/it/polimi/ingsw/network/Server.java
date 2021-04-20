package it.polimi.ingsw.network;

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
            serverSocket = new ServerSocket(6969);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.println("Server ready");

        Lobby lobby;
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                ClientHandler CH = new ClientHandler(socket);
                executor.submit(CH);
                synchronized (clientHandlers) {
                    clientHandlers.add(CH);
                    clientHandlers.notifyAll();
                }
                if(first) {
                    new Thread (() ->lobbyManager());
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
        do {
            clientHandlers.get(0).sendStandardMessage(StandardMessages.choosePlayerNumber);
            //TODO: ClientRead
            //players = Integer.parseInt(clientHandlers.get(0).read());
        } while (players < 1 || players > 4);
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
                    currentLobby.addPlayer(clientHandlers.remove(0));
                }
            }
        }
        new Thread(() -> currentLobby.run());
        currentLobby = null;
        if (clientHandlers.size() > 0) new Thread(()->lobbyManager());
        else first = true;
    }
}
