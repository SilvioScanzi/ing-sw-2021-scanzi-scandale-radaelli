package it.polimi.ingsw.network;

import it.polimi.ingsw.messages.StandardMessages;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

//server side
public class ClientHandler implements Runnable{

    private String nickname = null;
    private Socket socket;
    private boolean myTurn;
    private ObjectOutputStream socketOut;
    private ObjectInputStream socketIn;
    private PrintWriter out;
    private Scanner in;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try{
            socketOut = new ObjectOutputStream(socket.getOutputStream());
            socketIn = new ObjectInputStream(socket.getInputStream());
        }
        catch(Exception e){e.printStackTrace();}
        out = new PrintWriter(socketOut);
        in = new Scanner(socketIn);
        myTurn = false;
    }

    @Override
    public void run() {

    }

    public void setNickname(boolean already){
        if(already){
            out.println("Il nickname " + nickname + " è già in uso, inserisci un nuovo nickname");
        }
        else{
            out.println("Inserisci il nickname");
            out.println();
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

    public void sendStandardMessage(StandardMessages SM){
        try{
            socketOut.writeObject(SM);
        }catch(IOException e){e.printStackTrace();}
    }
}