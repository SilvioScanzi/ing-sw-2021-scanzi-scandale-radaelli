package it.polimi.ingsw.observers;

import it.polimi.ingsw.network.messages.*;

import java.util.ArrayList;
import java.util.List;

public class CHObservable {
    private final List<CHObserver> observers = new ArrayList<>();
    private final List<CH_ServerObserver> serverObservers = new ArrayList<>();

    public void addObserver(CHObserver observer){
        synchronized (observers) {
            observers.add(observer);
        }
    }

    public void addServerObserver(CH_ServerObserver observer){
        synchronized (serverObservers) {
            serverObservers.add(observer);
        }
    }

    public void clearServer(){
        synchronized (serverObservers) {
            serverObservers.clear();
        }
    }

    public void notifyServerDisconnection(){
        for(CH_ServerObserver obs : serverObservers){
            obs.updateServerDisconnection(this);
        }
    }

    public void notifyServerPlayerNumber(ChoosePlayerNumberMessage message){
        for(CH_ServerObserver obs : serverObservers){
            obs.updateServerPlayerNumber(this,message);
        }
    }

    public void notifyServerNickname(NicknameMessage message){
        for(CH_ServerObserver obs : serverObservers){
            obs.updateServerNickname(this,message);
        }
    }

    public void notifyReconnection(){
        for(CH_ServerObserver obs : serverObservers){
            obs.updateServerReconnection(this);
        }
    }

    public void notifyDisconnected(){
        for(CHObserver obs : observers){
            obs.updateDisconnected(this);
        }
    }

    public void notifyLCDiscard(DiscardLeaderCardSetupMessage message){
        for(CHObserver obs : observers){
            obs.updateLCDiscard(this,message);
        }
    }

    public void notifyFinishSetup(FinishSetupMessage message){
        for(CHObserver obs : observers){
            obs.updateFinishSetup(this,message);
        }
    }

    public void notifyBuyResources(BuyResourcesMessage message){
        for(CHObserver obs : observers){
            obs.updateBuyResources(this,message);
        }
    }

    public void notifyBuyDevelopmentCard(BuyDevelopmentCardMessage message){
        for(CHObserver obs : observers){
            obs.updateBuyDC(this,message);
        }
    }

    public void notifyProduction(ProductionMessage message){
        for(CHObserver obs : observers){
            obs.updateProduction(this,message);
        }
    }

    public void notifyMoveResources(MoveResourcesMessage message){
        for(CHObserver obs : observers){
            obs.updateMoveResources(this,message);
        }
    }

    public void notifyPlayLeaderCard(PlayLeaderCardMessage message){
        for(CHObserver obs : observers){
            obs.updatePlayLeaderCard(this,message);
        }
    }

    public void notifyDiscardLeaderCard(DiscardLeaderCardMessage message){
        for(CHObserver obs : observers){
            obs.updateDiscardLeaderCard(this,message);
        }
    }

    public void notifyTurnDone() {
        for (CHObserver obs : observers) {
            obs.updateTurnDone(this);
        }
    }
}
