package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.observers.CHObservable;
import it.polimi.ingsw.observers.ModelObservable;
import it.polimi.ingsw.observers.ModelObserver;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

//server side
public class ClientHandler extends CHObservable implements Runnable, ModelObserver {
    public enum STATE{start, nickname, playerNumber, lobbyNotReady, discardLeaderCard, finishingSetup, myTurn, moveNeeded, actionDone, wait, endGame, disconnected}
    private STATE state = STATE.start;
    private Object message;
    private String nickname = null;
    private final Socket socket;
    private ObjectOutputStream socketOut;
    private ObjectInputStream socketIn;
    private HashMap<ClientHandler,String> nameQueue;
    private boolean messageReady = false;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try{
            socketOut = new ObjectOutputStream(socket.getOutputStream());
            socketIn = new ObjectInputStream(socket.getInputStream());
            socketOut.writeObject(StandardMessages.connectionEstablished);
        }
        catch(Exception e){e.printStackTrace();}
    }

    public void closeConnection(){
        try {
            socketOut.close();
            socketIn.close();
            socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public STATE getState(){
        return state;
    }

    public void setState(STATE state) { this.state = state; }

    public String getNickname(){
        return nickname;
    }

    public void setNameQueue(HashMap<ClientHandler, String> nameQueue) {
        this.nameQueue = nameQueue;
    }

    @Override
    public void run() {
        while(true) {
            try {
                socket.setSoTimeout(60000);
                message = socketIn.readObject();
                if (!(message instanceof Message)) {
                    sendStandardMessage(StandardMessages.wrongObject);
                } else {
                    processMessage((Message) message);
                }
            } catch (IOException | ClassNotFoundException e) {
                    state = STATE.disconnected;
                    notify(this);
            }
        }
    }

    public void

    public void processMessage(Message message){
        switch(state) {
            case playerNumber: {
                if(!(message instanceof ChoosePlayerNumberMessage)) sendStandardMessage(StandardMessages.wrongObject);
                else{
                    if(((ChoosePlayerNumberMessage) message).getN()<1 || ((ChoosePlayerNumberMessage) message).getN()>4) {
                        sendStandardMessage(StandardMessages.wrongObject);
                        sendStandardMessage(StandardMessages.choosePlayerNumber);
                    }
                    else {
                        state = STATE.lobbyNotReady;
                        notify(message);
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
                synchronized (this) {
                    messageReady = true;
                    this.notifyAll();
                }
            } else sendStandardMessage(StandardMessages.wrongObject);
        }
        else if(!finishingSetup){
            if(message instanceof FinishSetupMessage){
                synchronized (this) {
                    messageReady = true;
                    this.notifyAll();
                }
            } else sendStandardMessage(StandardMessages.wrongObject);
        }
        else if(myTurn) {
            if(messageReady) sendStandardMessage(StandardMessages.waitALittleMore);
            else if(moveNeeded && !(message instanceof MoveResourcesMessage)) sendStandardMessage(StandardMessages.wrongObject);
            else if(!actionDone && message instanceof TurnDoneMessage) sendStandardMessage(StandardMessages.actionNotDone);
            else {
                synchronized (this) {
                    messageReady = true;
                    this.notifyAll();
                }
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

    public void setActionDone(boolean actionDone) {
        this.actionDone = actionDone;
    }

    @Override
    public void updateMarket(Market m){
        sendObject(new MarketMessage(m.getGrid(),m.getRemainingMarble()));
    }

    @Override
    public void updateDCMarket(DevelopmentCard DC){
        sendObject(new DCMarketMessage(DC.getColour(),DC.getVictoryPoints()));
    }

    @Override
    public void updateWR(Warehouse wr){
        sendObject(new WarehouseMessage(wr));
    }

    @Override
    public void updateSB(Strongbox sb){
        sendObject(new StrongboxMessage(sb));
    }

    @Override
    public void updateFT(FaithTrack ft){
        sendObject(new FaithTrackMessage(ft));
    }

    @Override
    public void updateSlots(int slotNumber,DevelopmentCard DC){
        sendObject(new SlotMessage(slotNumber,DC));
    }

    @Override
    public void updateHand(ArrayList<Resources> hand){
        sendObject(new HandMessage(hand));
    }

    @Override
    public void updateLCPlayed(LeaderCard lcp){
        sendObject(new LeaderCardPlayedMessage(lcp));
    }
}