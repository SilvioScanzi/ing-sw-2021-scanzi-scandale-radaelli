package it.polimi.ingsw;

import it.polimi.ingsw.network.Server;

public class ServerApp {
    public static void main(String args[]){
        Server server = new Server();
        server.startServer();
    }
}
