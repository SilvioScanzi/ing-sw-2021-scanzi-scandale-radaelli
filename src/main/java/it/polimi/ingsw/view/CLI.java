package it.polimi.ingsw.view;
import it.polimi.ingsw.network.client.NetworkHandler;

import java.util.Scanner;
/*TODO: network handler sempre in ascolto, CLI scrive usando il metodo sendObject, usiamo una sorta di buffer per stampare a schermo
*  quando finisce l'azione (se non sono errori). IDEA MACCHINA A STATI*/
public class CLI {
    private NetworkHandler networkHandler;
    private Scanner scanner;

    public CLI() {
        networkHandler = new NetworkHandler();
        scanner = new Scanner(System.in);
    }

    public void startCLI(){
        new Thread(networkHandler).start();
    }
}
