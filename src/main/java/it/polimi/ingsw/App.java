package it.polimi.ingsw;

import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.view.CLI.CLI;
import it.polimi.ingsw.view.GUI.GUI;

import java.util.Scanner;

public class App {
    public static void main(String[] args){

        if(args.length>0){
            if(args[0].equals("-server")){
                System.out.println("Select port");
                Scanner in = new Scanner(System.in);
                int port = -1;
                do{
                    try{
                        port = Integer.parseInt(in.nextLine());
                        if(port<1024 || port>65536){
                            System.out.println("Ports under 1024 are reserved - Ports above 65536 don't exist");
                        }
                    }catch (NumberFormatException e){
                        System.out.println("Insert a valid port number");
                    }
                }while(port<1024 || port>65536);
                System.out.println("Welcome to the server log");
                Server server = new Server();
                server.startServer(port);
            }
            else if(args[0].equals("-client")){
                if(args.length>1 && args[1].equals("-cli")) {
                    CLI cli;
                    if(args.length>2 && args[2].equals("-color")) cli = new CLI(true);
                    else cli = new CLI(false);
                    cli.start();
                }
                else if(args.length>1 && !args[1].equals("-gui")){
                    System.out.println("Devi scrivere -cli se vuoi utilizzare un'interfaccia a linea di comando");
                    System.out.println("Devi scrivere -gui se vuoi utilizzare un'interfaccia grafica");
                    System.out.println("Altrimenti, se non scrivi nulla, di default viene utilizzata una GUI");
                }
                else{
                    GUI gui = new GUI();
                    gui.begin();
                }
            }
            else{
                System.out.println("Per lanciare l'applicazione usa -server o -client");
                System.out.println("Altrimenti, se non scrivi nulla, di default verrà lanciato il client con una GUI");
            }
        }
        else{
            GUI gui = new GUI();
            gui.begin();
        }
    }
}
