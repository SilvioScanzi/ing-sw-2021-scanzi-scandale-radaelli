package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.observers.CHObservable;
import it.polimi.ingsw.observers.ModelObservable;
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
    private boolean lastActionMarket = false;

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

    public boolean getLastActionMarket() {
        return lastActionMarket;
    }

    public void setLastActionMarket(boolean lastActionMarket) {
        this.lastActionMarket = lastActionMarket;
    }

    public String getNickname(){
        return nickname;
    }

    @Override
    public void run() {
        sendStandardMessage(StandardMessages.chooseNickName);
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
                synchronized (state) {
                    state = ClientHandlerState.disconnected;
                    notify(this);
                    try {
                        state.wait();
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
                return;
            }
        }
    }

    public void processMessage(Message message) {
        switch (state) {
            case nickname: {
                if (message instanceof NicknameMessage) {
                    notify(message);
                } else {
                    sendStandardMessage(StandardMessages.wrongObject);
                }
                break;
            }

            case playerNumber: {
                if (!(message instanceof ChoosePlayerNumberMessage))
                    sendStandardMessage(StandardMessages.wrongObject);
                else {
                    if (((ChoosePlayerNumberMessage) message).getN() < 1 || ((ChoosePlayerNumberMessage) message).getN() > 4) {
                        sendStandardMessage(StandardMessages.wrongObject);
                        sendStandardMessage(StandardMessages.choosePlayerNumber);
                    } else {
                        state = ClientHandlerState.lobbyNotReady;
                        notify(message);
                    }
                }
                break;
            }

            case lobbyNotReady: {
                sendStandardMessage(StandardMessages.lobbyNotReady);
                break;
            }

            case discardLeaderCard: {
                if (message instanceof SetupLCDiscardMessage) {
                    notify(message);
                } else sendStandardMessage(StandardMessages.wrongObject);
                break;
            }

            case finishingSetup: {
                if (message instanceof FinishSetupMessage) {
                    notify(message);
                } else sendStandardMessage(StandardMessages.wrongObject);
                break;
            }

            case myTurn: {
                if (!(message instanceof BuyResourcesMessage) && !(message instanceof BuyDevelopmentCardMessage) && !(message instanceof ProductionMessage)
                        && !(message instanceof MoveResourcesMessage) && !(message instanceof PlayLeaderCardMessage) && !(message instanceof DiscardLeaderCardMessage)) {
                    sendStandardMessage(StandardMessages.wrongObject);
                } else {
                    notify(message);
                }
                break;
            }

            case moveNeeded: {
                if (!(message instanceof MoveResourcesMessage)) {
                    sendStandardMessage(StandardMessages.moveActionNeeded);
                } else {
                    notify(message);
                }
                break;
            }

            case actionDone: {
                if (!(message instanceof MoveResourcesMessage) && !(message instanceof PlayLeaderCardMessage) && !(message instanceof DiscardLeaderCardMessage) &&
                        !(message instanceof TurnDoneMessage)) {
                    sendStandardMessage(StandardMessages.wrongObject);
                } else {
                    notify(message);
                }
                break;
            }

            case notMyTurn: {
                sendStandardMessage(StandardMessages.notYourTurn);
                break;
            }

            case wait: {
                sendStandardMessage(StandardMessages.waitALittleMore);
                break;
            }

            case endGame: {
                sendStandardMessage(StandardMessages.endGame);
                break;
            }
        }

    }

    //Not message error handled above
    public Message getMessage() {
        return (Message)message;
    }

    public void setNickname(String nickname){
        this.nickname = nickname;
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
    public void update(ModelObservable obs, Object obj){
        if(obs instanceof Board){

        }
        else if(obs instanceof DevelopmentCardMarket){
            sendObject(new DCMarketMessage((DevelopmentCardMarket)obj));
        }
        else if(obs instanceof ResourceMarket){
            sendObject(new MarketMessage(((ResourceMarket) obs).getGrid(),((ResourceMarket) obs).getRemainingMarble()));
        }
    }


    /*@Override
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
    }*/
}