package it.polimi.ingsw.observers;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;

public class ModelObservable {
    private final List<ModelObserver> observers = new ArrayList<>();

    public void addObserver(ModelObserver observer){
        synchronized (observers) {
            observers.add(observer);
        }
    }

    public void notifyMarket(Market m){
        for(ModelObserver obs : observers){
            obs.updateMarket(m);
        }
    }

    public void notifyDCMarket(DevelopmentCard DC){
        for(ModelObserver obs : observers){
            obs.updateDCMarket(DC);
        }
    }

    public void notifyWR(Warehouse wr){
        for(ModelObserver obs : observers){
            obs.updateWR(wr);
        }
    }

    public void notifySB(Strongbox sb){
        for(ModelObserver obs : observers){
            obs.updateSB(sb);
        }
    }

    public void notifyFT(FaithTrack ft){
        for(ModelObserver obs : observers){
            obs.updateFT(ft);
        }
    }

    public void notifyHand(ArrayList<Resources> hand){
        for(ModelObserver obs : observers){
            obs.updateHand(hand);
        }
    }

    public void notifyLCPlayed(LeaderCard lcp){
        for(ModelObserver obs : observers){
            obs.updateLCPlayed(lcp);
        }
    }
}
