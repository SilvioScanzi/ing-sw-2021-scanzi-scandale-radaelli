package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.ChoosePlayerNumberMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class NetworkHandler {
    Socket socket;
    private ObjectOutputStream socketOut;
    private ObjectInputStream socketIn;
    Scanner in = new Scanner(System.in);

    public NetworkHandler(){
        try{
            socket = new Socket("127.0.0.1",9090);
            socketOut = new ObjectOutputStream(socket.getOutputStream());
            socketIn = new ObjectInputStream(socket.getInputStream());
        }catch(Exception e){System.out.println("Connessione non disponibile"); e.printStackTrace();}
    }

    public void run() {
        while(true){
            try {
                System.out.println(socketIn.readObject().toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            String s = in.nextLine();
            ChoosePlayerNumberMessage CPNM = new ChoosePlayerNumberMessage(Integer.parseInt(s));
            try {
                socketOut.writeObject(CPNM);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
