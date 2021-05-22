package it.polimi.ingsw;

import it.polimi.ingsw.view.CLI.CLI;
import it.polimi.ingsw.view.GUI.GUI;
import javafx.application.Application;

public class ClientApp {
    public static void main(String[] args){
        if(args.length>0){
            if(args[0].equals("cli")) {
                CLI cli = new CLI();
                cli.start();
            }
            else if(!args[0].equals("gui")){
                System.out.println("Devi scrivere cli se vuoi utilizzare un'interfaccia a linea di comando");
                System.out.println("Devi scrivere gui se vuoi utilizzare un'interfaccia grafica");
                System.out.println("Altrimenti, se non scrivi nulla, di default viene utilizzata una GUI");
                return;
            }
            else{
                GUI.main(args);
            }
        }
        else{
            //CLI cli = new CLI();
            //cli.start();
            GUI.main(args);
        }
    }
}
