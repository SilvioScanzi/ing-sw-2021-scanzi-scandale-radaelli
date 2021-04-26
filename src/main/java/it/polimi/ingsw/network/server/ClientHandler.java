package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.*;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

//server side
public class ClientHandler implements Runnable{
    private Object message;
    private String nickname = null;
    private final Socket socket;
    private ObjectOutputStream socketOut;
    private ObjectInputStream socketIn;
    private HashMap<ClientHandler,String> nameQueue;
    private boolean chosenNickName = false;
    private boolean myTurn = false;
    private boolean setPlayerNumber = false;
    private boolean messageReady = false;
    private boolean lobbyReady = false;
    private boolean discardLeaderCard = false;
    private boolean finishingSetup = false;
    private boolean moveNeeded = false;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try{
            socketOut = new ObjectOutputStream(socket.getOutputStream());
            socketIn = new ObjectInputStream(socket.getInputStream());
            socketOut.writeObject(StandardMessages.connectionEstablished);
        }
        catch(Exception e){e.printStackTrace();}
    }

    public void setMoveNeeded(boolean moveNeeded) {
        this.moveNeeded = moveNeeded;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    public void setNameQueue(HashMap<ClientHandler, String> nameQueue) {
        this.nameQueue = nameQueue;
    }

    public void setLobbyReady(boolean lobbyReady) {
        this.lobbyReady = lobbyReady;
    }

    public void setSetPlayerNumber(boolean setPlayerNumber) {
        this.setPlayerNumber = setPlayerNumber;
    }

    public void setDiscardLeaderCard(boolean discardLeaderCard) {
        this.discardLeaderCard = discardLeaderCard;
    }

    public void setFinishingSetup(boolean finishingSetup) {
        this.finishingSetup = finishingSetup;
    }

    @Override
    public void run() {
        while(true) {
                try {
                    message = socketIn.readObject();
                    if (!(message instanceof Message)) {
                        sendStandardMessage(StandardMessages.wrongObject);
                    } else {
                        processMessage((Message) message);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    sendStandardMessage(StandardMessages.wrongObject);
                    e.printStackTrace();
                }
            }
    }

    public void processMessage(Message message){
        if(setPlayerNumber){
            if(!(message instanceof ChoosePlayerNumberMessage)) sendStandardMessage(StandardMessages.wrongObject);
            else{
                if(((ChoosePlayerNumberMessage) message).getN()<1 || ((ChoosePlayerNumberMessage) message).getN()>4) {
                    sendStandardMessage(StandardMessages.wrongObject);
                    sendStandardMessage(StandardMessages.choosePlayerNumber);

                }
                else {
                    synchronized (this) {
                        messageReady = true;
                        setPlayerNumber = false;
                        this.notifyAll();
                    }
                }
            }
        }
        else if(!lobbyReady){
          sendStandardMessage(StandardMessages.lobbyNotReady);
        }
        else if(!chosenNickName){
            if(message instanceof NicknameMessage){
                String inputNickname = ((NicknameMessage) message).getNickname();
                synchronized (nameQueue){
                    if(!nameQueue.containsValue(inputNickname)){
                        nameQueue.put(this,inputNickname);
                        chosenNickName = true;
                        nickname = inputNickname;
                        nameQueue.notifyAll();
                    }
                    else sendStandardMessage(StandardMessages.nicknameAlreadyInUse);
                }
            }
        }
        else if(!discardLeaderCard){
            if(message instanceof SetupLCDiscardMessage){
                messageReady = true;
                this.notifyAll();
            } else sendStandardMessage(StandardMessages.wrongObject);
        }
        else if(!finishingSetup){
            if(message instanceof FinishSetupMessage){
                messageReady = true;
                this.notifyAll();
            } else sendStandardMessage(StandardMessages.wrongObject);
        }
        else if(myTurn) {
            if(messageReady) sendStandardMessage(StandardMessages.waitALittleMore);
            else if(moveNeeded && !(message instanceof MoveResourcesMessage)) sendStandardMessage(StandardMessages.wrongObject);
            else {
                messageReady = true;
                this.notifyAll();
            }
        }
        else sendStandardMessage(StandardMessages.notYourTurn);
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

    public void sendObject(Object o){
        try{
            socketOut.writeObject(o);
        }catch(IOException e){e.printStackTrace();}
    }


}