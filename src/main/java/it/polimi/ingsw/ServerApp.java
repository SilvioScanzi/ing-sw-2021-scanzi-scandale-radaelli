package it.polimi.ingsw;

import it.polimi.ingsw.network.server.Server;

import java.util.Scanner;

public class ServerApp {
    public static void main(String[] args){
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
        System.out.println("\u001B[31mWelcome to the server log");
        Server server = new Server();
        server.startServer(port);
    }
}
