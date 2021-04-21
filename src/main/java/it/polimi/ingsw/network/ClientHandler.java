package it.polimi.ingsw.network;

import it.polimi.ingsw.messages.*;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

//server side
public class ClientHandler implements Runnable{
    private Object message;
    private String nickname = null;
    private Socket socket;
    private ObjectOutputStream socketOut;
    private ObjectInputStream socketIn;
    private PrintWriter out;
    private Scanner in;
    private HashMap<ClientHandler,String> nameQueue;
    private boolean chosenNickName = false;
    private boolean myTurn = false;
    private boolean setPlayerNumber;
    private boolean messageReady = false;
    private int playerNumber;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try{
            socketOut = new ObjectOutputStream(socket.getOutputStream());
            socketIn = new ObjectInputStream(socket.getInputStream());
            socketOut.writeObject("Ciao");
        }
        catch(Exception e){e.printStackTrace();}
        out = new PrintWriter(socketOut);
        in = new Scanner(socketIn);
        myTurn = false;
    }

    public void setNameQueue(HashMap<ClientHandler, String> nameQueue) {
        this.nameQueue = nameQueue;
    }

    @Override
    public void run() {
        while(true){
            try {
                message = socketIn.readObject();
                if(!(message instanceof Message)){
                    sendStandardMessage(StandardMessages.wrongObject);
                }
                else{
                    processMessage((Message) message);
                }
            }catch(IOException | ClassNotFoundException e){
                sendStandardMessage(StandardMessages.wrongObject);
                e.printStackTrace();
            }
        }
    }

    public void processMessage(Message message){
        if(!setPlayerNumber){

        }
        else if(!chosenNickName){
            if(message instanceof NicknameMessage){
                String inputNickname = ((NicknameMessage) message).getNickname();
                synchronized (nameQueue){
                    if(!nameQueue.containsValue(inputNickname)){
                        nameQueue.put(this,inputNickname);
                        chosenNickName = true;
                        nickname = inputNickname;
                        nameQueue.notify();
                    }
                    else sendStandardMessage(StandardMessages.nicknameAlreadyInUse);
                }
            }
        }
        else if(myTurn) {

            messageReady = true;
            this.notifyAll();
            try{
                this.wait();
            }catch(Exception e){e.printStackTrace();}
            //lobby.updateBuyResources((BuyResourceMessage)message);
        }
    }

    public boolean getMessageReady() {
        return messageReady;
    }

    //Not message error handled above
    public Message getMessage() {
        return (Message)message;
    }

    public String getNickname() {
        return nickname;
    }

    public void setMessageReady(boolean check) {
        this.messageReady = check;
    }

    public void sendStandardMessage(StandardMessages SM){
        try{
            socketOut.writeObject(SM);
        }catch(IOException e){e.printStackTrace();}
    }


}