package it.polimi.ingsw.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private HashMap<Integer,Lobby> lobbyList;
    private int lobbyID = 0;

    public void startServer() {
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(3000);
        } catch (IOException e) {
            System.err.println(e.getMessage()); // porta non disponibile
            return;
        }
        System.out.println("Server ready");

        Lobby lobby;
        while (true) {
            try {
                lobby = lobbyList.get(lobbyID);
                Socket socket = serverSocket.accept();
                ClientHandler CH = new ClientHandler(socket);
                if(lobby==null || lobby.getAddedPlayers()==lobby.getPlayerNumber()){
                    int playernumber = CH.setPlayerNumber(false);
                    while(playernumber<1 || playernumber>4){
                        playernumber = CH.setPlayerNumber(true);
                    }
                    if(lobby.getAddedPlayers()==lobby.getPlayerNumber()) lobbyID++;
                    lobby = new Lobby(playernumber);
                    lobbyList.put(lobbyID,lobby);
                }
                lobby.addPlayer(CH);
                if(lobby.getAddedPlayers()==lobby.getPlayerNumber()) executor.submit(lobby);
                executor.submit(CH);
            } catch(IOException e) {
                break; // entrerei qui se serverSocket venisse chiuso
            }
        }
        executor.shutdown();
    }
}
