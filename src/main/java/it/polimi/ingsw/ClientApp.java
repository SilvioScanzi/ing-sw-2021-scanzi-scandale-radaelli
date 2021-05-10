package it.polimi.ingsw;

import it.polimi.ingsw.network.client.NetworkHandler;

public class ClientApp {
    public static void main(String[] args){
        NetworkHandler NH = new NetworkHandler(false);
        new Thread(NH).start();
    }
}
