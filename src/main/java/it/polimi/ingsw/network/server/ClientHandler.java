package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.observers.CHObservable;
import it.polimi.ingsw.observers.ModelObserver;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


//server side
public class ClientHandler extends CHObservable implements Runnable, ModelObserver {
    public enum ClientHandlerState{nickname, playerNumber, lobbyNotReady, discardLeaderCard, finishingSetup, wait, myTurn, moveNeeded, actionDone, notMyTurn, endGame, disconnected}
    private ClientHandlerState state = ClientHandlerState.nickname;
    private Object message;
    private String nickname = null;
    private final Socket socket;
    private ObjectOutputStream socketOut;
    private ObjectInputStream socketIn;
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

    public ClientHandlerState getState(){
        return state;
    }

    public void setState(ClientHandlerState state) { this.state = state; }

    public String getNickname(){
        return nickname;
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
                    state = ClientHandlerState.disconnected;
                    notify(this);
            }
        }
    }

    public void processMessage(Message message){
        if(messageReady){
            sendStandardMessage(StandardMessages.waitALittleMore);
        }
        else {
            switch (state) {

                case nickname: {
                    if(message instanceof NicknameMessage){
                        messageReady = true;
                        notify(message);
                    }
                    else{
                        sendStandardMessage(StandardMessages.wrongObject);
                    }
                }

                case playerNumber: {
                    if (!(message instanceof ChoosePlayerNumberMessage))
                        sendStandardMessage(StandardMessages.wrongObject);
                    else {
                        if (((ChoosePlayerNumberMessage) message).getN() < 1 || ((ChoosePlayerNumberMessage) message).getN() > 4) {
                            sendStandardMessage(StandardMessages.wrongObject);
                            sendStandardMessage(StandardMessages.choosePlayerNumber);
                        } else {
                            messageReady = true;
                            state = ClientHandlerState.lobbyNotReady;
                            notify(message);
                        }
                    }
                }

                case lobbyNotReady:{
                    sendStandardMessage(StandardMessages.lobbyNotReady);
                }

                case discardLeaderCard:{
                    if(message instanceof SetupLCDiscardMessage){
                        messageReady = true;
                        notify(message);
                    }
                    else sendStandardMessage(StandardMessages.wrongObject);
                }

                case finishingSetup: {
                    if(message instanceof FinishSetupMessage) {
                        messageReady = true;
                        notify(message);
                    } else sendStandardMessage(StandardMessages.wrongObject);
                }

                case myTurn:{
                    if(!(message instanceof BuyResourcesMessage) && !(message instanceof BuyDevelopmentCardMessage) && !(message instanceof ProductionMessage)
                            && !(message instanceof MoveResourcesMessage) && !(message instanceof PlayLeaderCardMessage) && !(message instanceof DiscardLeaderCardMessage)){
                        sendStandardMessage(StandardMessages.wrongObject);
                    }
                    else{
                        messageReady = true;
                        notify(message);
                    }
                }

                case moveNeeded:{
                    if(!(message instanceof MoveResourcesMessage)){
                        sendStandardMessage(StandardMessages.moveActionNeeded);
                    }
                    else{
                        messageReady = true;
                        notify(message);
                    }
                }

                case actionDone:{
                    if(!(message instanceof MoveResourcesMessage) && !(message instanceof PlayLeaderCardMessage) && !(message instanceof DiscardLeaderCardMessage) &&
                        !(message instanceof TurnDoneMessage)){
                        sendStandardMessage(StandardMessages.wrongObject);
                    }
                    else{
                        messageReady = true;
                        notify(message);
                    }
                }

                case notMyTurn: sendStandardMessage(StandardMessages.notYourTurn);

                case wait: sendStandardMessage(StandardMessages.waitALittleMore);

                case endGame: sendStandardMessage(StandardMessages.endGame);
            }
        }
    }

    public boolean getMessageReady() {
        return messageReady;
    }

    //Not message error handled above
    public Message getMessage() {
        return (Message)message;
    }

    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    public void setMessageReady(boolean messageReady) {
        this.messageReady = messageReady;
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