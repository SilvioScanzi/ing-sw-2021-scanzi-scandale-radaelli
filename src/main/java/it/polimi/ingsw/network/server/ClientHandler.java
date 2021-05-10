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

    public synchronized ClientHandlerState getState(){
        return state;
    }

    public synchronized void setState(ClientHandlerState state) { this.state = state; }

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
        Object message;
        sendStandardMessage(StandardMessages.chooseNickName);
        while(!state.equals(ClientHandlerState.disconnected)) {
            try {
                socket.setSoTimeout(60000);
                message = socketIn.readObject();
                if (!(message instanceof Message)) {
                    sendStandardMessage(StandardMessages.wrongObject);
                } else {
                    processMessage((Message) message);
                }
            } catch (IOException | ClassNotFoundException e) {
                synchronized (this) {
                    state = ClientHandlerState.disconnected;
                    notifyServerDisconnection();
                    notifyDisconnected();
                    /*try {
                        state.wait();
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }*/
                }
                return;
            }
        }

    }

    public synchronized void processMessage(Message message) {
        switch (state) {
            case nickname: {
                if (message instanceof NicknameMessage) {
                    notifyServerNickname((NicknameMessage) message);
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
                        notifyServerPlayerNumber((ChoosePlayerNumberMessage) message);
                    }
                }
                break;
            }

            case lobbyNotReady: {
                sendStandardMessage(StandardMessages.lobbyNotReady);
                break;
            }

            case discardLeaderCard: {
                if (message instanceof DiscardLeaderCardSetupMessage) {
                    notifyLCDiscard((DiscardLeaderCardSetupMessage) message);
                } else sendStandardMessage(StandardMessages.wrongObject);
                break;
            }

            case finishingSetup: {
                if (message instanceof FinishSetupMessage) {
                    notifyFinishSetup((FinishSetupMessage) message);
                } else sendStandardMessage(StandardMessages.wrongObject);
                break;
            }

            case myTurn: {
                if(message instanceof BuyResourcesMessage) notifyBuyResources((BuyResourcesMessage) message);
                else if(message instanceof BuyDevelopmentCardMessage) notifyBuyDevelopmentCard((BuyDevelopmentCardMessage) message);
                else if(message instanceof ProductionMessage) notifyProduction((ProductionMessage) message);
                else if(message instanceof MoveResourcesMessage) notifyMoveResources((MoveResourcesMessage) message);
                else if(message instanceof PlayLeaderCardMessage) notifyPlayLeaderCard((PlayLeaderCardMessage) message);
                else if(message instanceof DiscardLeaderCardMessage) notifyDiscardLeaderCard((DiscardLeaderCardMessage) message);
                else sendStandardMessage(StandardMessages.wrongObject);
                break;
            }

            case moveNeeded: {
                if (!(message instanceof MoveResourcesMessage)) {
                    sendStandardMessage(StandardMessages.moveActionNeeded);
                } else {
                    notifyMoveResources((MoveResourcesMessage) message);
                }
                break;
            }

            case actionDone: {
                if(message instanceof MoveResourcesMessage) notifyMoveResources((MoveResourcesMessage) message);
                else if(message instanceof PlayLeaderCardMessage) notifyPlayLeaderCard((PlayLeaderCardMessage) message);
                else if(message instanceof DiscardLeaderCardMessage) notifyDiscardLeaderCard((DiscardLeaderCardMessage) message);
                else sendStandardMessage(StandardMessages.wrongObject);
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
    public void updateMarket(ResourceMarket m){
        sendObject(new ResourceMarketMessage(m.getGrid(),m.getRemainingMarble()));
    }

    @Override
    public void updateDCMarket(DevelopmentCardMarket DCM){
        sendObject(new DCMarketMessage(DCM));
    }

    @Override
    public void updateWR(Warehouse wr, String s){
        sendObject(new WarehouseMessage(wr,s));
    }

    @Override
    public void updateSB(Strongbox sb, String s){
        sendObject(new StrongboxMessage(sb,s));
    }

    //if you are playing, the cards are showed to you, otherwise it's just to show something to other players
    @Override
    public void updateLCHand(ArrayList<LeaderCard> LCHand, String s){
        if(nickname.equals(s)) sendObject(new LeaderCardHandMessage(LCHand));
        else sendObject(new LeaderCardHandUpdateMessage(s));
    }

    @Override
    public void updateFT(FaithTrack ft,String s){
        sendObject(new FaithTrackMessage(ft,s));
    }

    @Override
    public void updateSlots(DevelopmentCard DC, int slotIndex, String nickname){
        sendObject(new SlotMessage(DC, slotIndex, nickname));
    }

    @Override
    public void updateHand(ArrayList<Resources> hand, String s){
        sendObject(new ResourceHandMessage(hand,s));
    }

    @Override
    public void updateLCPlayed(ArrayList<LeaderCard> lcp, String s){
        sendObject(new LeaderCardPlayedMessage(lcp,s));
    }

    @Override
    public void updateVP(int vp, String s){
        sendObject(new VictoryPointsMessage(vp,s));
    }

    @Override
    public void updateLorenzo(LorenzoTrack lorenzo){
        sendObject(new LorenzoTrackMessage(lorenzo));
    }

    @Override
    public void updateActionToken(ActionToken AT){
        sendObject(new ActionTokenMessage(AT));
    }
}