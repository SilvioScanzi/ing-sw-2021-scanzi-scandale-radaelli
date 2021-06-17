package it.polimi.ingsw.observers;

import it.polimi.ingsw.commons.ActionToken;
import it.polimi.ingsw.commons.Marbles;
import it.polimi.ingsw.commons.Resources;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModelObservable {
    private final List<ModelObserver> observers = new ArrayList<>();

    public void addObserver(ModelObserver observer){
        synchronized (observers) {
            observers.add(observer);
        }
    }

    public void removeObserver(ModelObserver observer){
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    public void notifyResourceMarket(Marbles[][] grid, Marbles remainingMarble){
        for(ModelObserver obs : observers){
            obs.updateMarket(grid,remainingMarble);
        }
    }

    public void notifyMarketHand(ArrayList<Resources> hand, String nickname, Marbles[][] grid, Marbles remainingMarble){
        for(ModelObserver obs : observers){
            obs.updateMarketHand(hand, nickname, grid,remainingMarble);
        }
    }
    public void notifyDCMarket(DevelopmentCardMarket DCM){
        for(ModelObserver obs : observers){
            obs.updateDCMarket(DCM);
        }
    }

    public void notifyWR(Warehouse wr, String s){
        for(ModelObserver obs : observers){
            obs.updateWR(wr,s);
        }
    }

    public void notifySlot(Slot[] slots, String nickname){
        for(ModelObserver obs : observers){
            obs.updateSlots(slots, nickname);
        }
    }

    public void notifyLCHand(HashMap<Integer,LeaderCard> LCHand, String s){
        for(ModelObserver obs : observers){
            obs.updateLCHand(LCHand,s);
        }
    }

    public void notifySB(Strongbox sb, String s){
        for(ModelObserver obs : observers){
            obs.updateSB(sb,s);
        }
    }

    public void notifyFT(FaithTrack ft, String s){
        for(ModelObserver obs : observers){
            obs.updateFT(ft,s);
        }
    }

    public void notifyHand(ArrayList<Resources> hand, String s){
        for(ModelObserver obs : observers){
            obs.updateHand(hand,s);
        }
    }

    public void notifyLCPlayed(HashMap<Integer,LeaderCard> lcp, String s){
        for(ModelObserver obs : observers){
            obs.updateLCPlayed(lcp,s);
        }
    }

    public void notifyLorenzo(LorenzoTrack lorenzo, boolean[] popeFavor){
        for(ModelObserver obs : observers){
            obs.updateLorenzo(lorenzo,popeFavor);
        }
    }

    public void notifyActionToken(ActionToken AT){
        for(ModelObserver obs : observers){
            obs.updateActionToken(AT);
        }
    }

    public void notifyActionDone(String s){
        for(ModelObserver obs : observers){
            obs.updateActionDone(s);
        }
    }

    public void notifyResourceBuyDone(String nickname){
        for(ModelObserver obs : observers){
            obs.updateResourceBuyDone(nickname);
        }
    }
}
