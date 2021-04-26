package it.polimi.ingsw;

import it.polimi.ingsw.network.client.NetworkHandler;
import it.polimi.ingsw.view.CLI.CLI;

public class ClientApp {
    public static void main(String args[]){
        NetworkHandler NH = new NetworkHandler();
        new Thread(NH).start();
    }
}
