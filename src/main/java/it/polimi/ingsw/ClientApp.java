package it.polimi.ingsw;

import it.polimi.ingsw.network.client.NetworkHandler;

public class ClientApp {
    public static void main(String[] args){
        boolean UI = true;
        if(args.length>0){
            if(args[0].equals("cli"))
                UI = false;
            else if(!args[0].equals("gui")){
                System.out.println("Devi scrivere cli se vuoi utilizzare un'interfaccia a linea di comando");
                System.out.println("Devi scrivere gui se vuoi utilizzare un'interfaccia grafica");
                System.out.println("Altrimenti, se non scrivi nulla, di default viene utilizzata una GUI");
                return;
            }
        }
        NetworkHandler NH = new NetworkHandler(true);
    }
}
