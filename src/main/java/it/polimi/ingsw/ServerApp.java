package it.polimi.ingsw;

import it.polimi.ingsw.server.Server;

public class ServerApp {
    public static void main(String args[]){
        Server server = new Server();
        server.startServer();
    }
}
