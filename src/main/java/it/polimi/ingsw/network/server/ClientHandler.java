package it.polimi.ingsw.network.server;

import it.polimi.ingsw.commons.ActionToken;
import it.polimi.ingsw.commons.Marbles;
import it.polimi.ingsw.commons.Resources;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.observers.CHObservable;
import it.polimi.ingsw.observers.ModelObserver;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ClientHandler extends CHObservable implements Runnable, ModelObserver {
    public enum ClientHandlerState{reconnecting, nickname, playerNumber, gameNotCreated, lobbyNotReady, wait, discardLeaderCard, finishingSetup, myTurn, notMyTurn, endGame, disconnected}
    private ClientHandlerState state = ClientHandlerState.nickname;
    private String nickname = null;
    private final Socket socket;
    private ObjectOutputStream socketOut;
    private ObjectInputStream socketIn;
    private Timer t;

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

    public synchronized void setState(ClientHandlerState state) {
        int delay;
        if (state.equals(ClientHandlerState.discardLeaderCard) || state.equals(ClientHandlerState.finishingSetup)) {
            delay = 120000;
        } else if(state.equals(ClientHandlerState.myTurn)) delay = 600000;
        else delay = 0;

        if(t != null){
            t.cancel();
            t.purge();
        }
        if(delay!=0) {
            t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    setState(ClientHandlerState.disconnected);
                    sendObject(new DisconnectedMessage(nickname));
                    notifyServerDisconnection();
                    notifyDisconnected();
                }
            }, delay);
        }
        this.state = state;
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
                if(state.equals(ClientHandlerState.playerNumber) || state.equals(ClientHandlerState.discardLeaderCard) ||
                        state.equals(ClientHandlerState.finishingSetup)){
                    if(t != null){
                        t.cancel();
                        t.purge();
                    }
                    t = new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            sendObject(new DisconnectedMessage(nickname));
                            notifyServerDisconnection();
                            if(state.equals(ClientHandlerState.discardLeaderCard) || state.equals(ClientHandlerState.finishingSetup)) {
                                notifyDisconnected();
                            }
                            else closeConnection();
                            state = ClientHandlerState.disconnected;
                        }
                    },120000);  //timer set to 2 minutes for setup stages of the game
                    message = socketIn.readObject();
                    t.cancel();
                    t.purge();
                    t = null;
                }
                else if(state.equals(ClientHandlerState.myTurn)){
                    if(t != null){
                        t.cancel();
                        t.purge();
                    }
                    t = new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            state = ClientHandlerState.disconnected;
                            sendObject(new DisconnectedMessage(nickname));
                            notifyServerDisconnection();
                            notifyDisconnected();
                        }
                    },600000);  //timer set to 10 minutes for every action in the game
                    message = socketIn.readObject();
                    t.cancel();
                    t.purge();
                    t = null;
                }
                else message = socketIn.readObject();

                if (!(message instanceof Message)) {
                    sendStandardMessage(StandardMessages.wrongObject);
                } else {
                    processMessage((Message) message);
                }
            } catch (IOException | ClassNotFoundException e) {
                synchronized (this) {
                    if(!state.equals(ClientHandlerState.disconnected)) {
                        notifyServerDisconnection();
                        notifyDisconnected();
                        state = ClientHandlerState.disconnected;
                    }
                }
                return;
            }
        }
    }

    public synchronized void processMessage(Message message) {
        switch (state) {
            case nickname -> {
                if (message instanceof NicknameMessage) {
                    notifyServerNickname((NicknameMessage) message);
                } else {
                    sendStandardMessage(StandardMessages.wrongObject);
                }
            }
            case playerNumber -> {
                if (!(message instanceof ChoosePlayerNumberMessage))
                    sendStandardMessage(StandardMessages.wrongObject);
                else {
                    if (((ChoosePlayerNumberMessage) message).getN() < 1 || ((ChoosePlayerNumberMessage) message).getN() > 4) {
                        sendStandardMessage(StandardMessages.wrongObject);
                        sendStandardMessage(StandardMessages.choosePlayerNumber);
                    } else {
                        state = ClientHandlerState.wait;
                        notifyServerPlayerNumber((ChoosePlayerNumberMessage) message);
                    }
                }
            }
            case gameNotCreated -> sendStandardMessage(StandardMessages.gameNotCreated);
            case lobbyNotReady -> sendStandardMessage(StandardMessages.lobbyNotReady);
            case wait -> sendStandardMessage(StandardMessages.wait);
            case discardLeaderCard -> {
                if (message instanceof DiscardLeaderCardSetupMessage) {
                    notifyLCDiscard((DiscardLeaderCardSetupMessage) message);
                } else sendStandardMessage(StandardMessages.wrongObject);
            }
            case finishingSetup -> {
                if (message instanceof FinishSetupMessage) {
                    notifyFinishSetup((FinishSetupMessage) message);
                } else sendStandardMessage(StandardMessages.wrongObject);
            }
            case myTurn -> {
                if (message instanceof BuyResourcesMessage) notifyBuyResources((BuyResourcesMessage) message);
                else if (message instanceof BuyDevelopmentCardMessage)
                    notifyBuyDevelopmentCard((BuyDevelopmentCardMessage) message);
                else if (message instanceof ProductionMessage) notifyProduction((ProductionMessage) message);
                else if (message instanceof MoveResourcesMessage) notifyMoveResources((MoveResourcesMessage) message);
                else if (message instanceof PlayLeaderCardMessage)
                    notifyPlayLeaderCard((PlayLeaderCardMessage) message);
                else if (message instanceof DiscardLeaderCardMessage)
                    notifyDiscardLeaderCard((DiscardLeaderCardMessage) message);
                else if (message instanceof TurnDoneMessage)
                    notifyTurnDone();
                else sendStandardMessage(StandardMessages.wrongObject);
            }
            case notMyTurn -> sendStandardMessage(StandardMessages.notYourTurn);
            case endGame -> sendStandardMessage(StandardMessages.endGame);
            case reconnecting -> {
                if(message instanceof ReconnectMessage && ((ReconnectMessage) message).getNickname().equals("Y")) notifyReconnection();
                else {
                    state = ClientHandlerState.nickname;
                    sendStandardMessage(StandardMessages.chooseNickName);
                }
            }
        }

    }

    public void printAlive(){
        System.out.println(!socket.isClosed());
    }

    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    public void sendStandardMessage(StandardMessages SM){
        try{
            socketOut.reset();
            socketOut.writeObject(SM);
        }catch(IOException e){e.printStackTrace();}
    }

    public void sendObject(Object o){
        try{
            socketOut.reset();
            socketOut.writeObject(o);
        }catch(IOException e){e.printStackTrace();}
    }

    @Override
    public void updateMarket(Marbles[][] grid, Marbles remainingMarble){
        sendObject(new ResourceMarketMessage(grid,remainingMarble));
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
    public void updateLCHand(HashMap<Integer,LeaderCard> LCHand, String s){
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
    public void updateLCPlayed(HashMap<Integer,LeaderCard> lcp, String s){
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

    @Override
    public void updateActionDone(String s) {
        if(s.equals(nickname)){
            sendStandardMessage(StandardMessages.actionDone);
        }
    }
}