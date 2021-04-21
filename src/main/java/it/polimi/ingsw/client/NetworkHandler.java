package it.polimi.ingsw.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class NetworkHandler {
    Socket socket;
    private ObjectOutputStream socketOut;
    private ObjectInputStream socketIn;

    public NetworkHandler(){
        try{
            socket = new Socket("127.0.0.1",9090);
            socketOut = new ObjectOutputStream(socket.getOutputStream());
            socketIn = new ObjectInputStream(socket.getInputStream());
        }catch(Exception e){System.out.println("Connessione non disponibile"); e.printStackTrace();}
    }


}
