package it.polimi.ingsw.network;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable{

    private String nickname = null;
    private Socket socket;
    private OutputStream socketOut;
    private InputStream socketIn;
    private PrintWriter out;
    private Scanner in;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try{
            socketOut = socket.getOutputStream();
            socketIn = socket.getInputStream();
        }
        catch(Exception e){e.printStackTrace();}
        out = new PrintWriter(socketOut);
        in = new Scanner(socketIn);
    }

    //Idea brutta: Synchronized su lobby passandogliela come parametro (wait fino a che inizia la partita)
    @Override
    public void run() {

    }

    public void setNickname(boolean already){
        if(already){
            out.println("Il nickname " + nickname+" è già in uso, inserisci un nuovo nickname");
        }
        else{
            out.println("Inserisci il nickname");
        }
        out.flush();
        nickname = in.nextLine();
    }

    public int setPlayerNumber(boolean already){
        if(already){
            out.println("Il numero deve essere tra 1 e 4. Reinserisci il numero");
        }
        else{
            out.println("Inserisci il numero di giocatori della partita");
        }
        out.flush();
        return Integer.parseInt(in.nextLine());
    }

    public String getNickname() {
        return nickname;
    }
}