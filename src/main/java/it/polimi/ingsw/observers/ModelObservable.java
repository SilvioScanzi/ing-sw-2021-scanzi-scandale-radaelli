package it.polimi.ingsw.observers;

import it.polimi.ingsw.commons.ActionToken;
import it.polimi.ingsw.commons.Marbles;
import it.polimi.ingsw.commons.Resources;
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

    /*public void notifyResourceMarket(ResourceMarket m){
        for(ModelObserver obs : observers){
            obs.updateMarket(m);
        }
    }*/

    public void notifyResourceMarket(Marbles[][] grid, Marbles remainingMarble){
        for(ModelObserver obs : observers){
            obs.updateMarket(grid,remainingMarble);
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

    public void notifySlot(DevelopmentCard DC, int slotIndex, String nickname){
        for(ModelObserver obs : observers){
            obs.updateSlots(DC, slotIndex, nickname);
        }
    }

    public void notifyLCHand(ArrayList<LeaderCard> LCHand, String s){
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

    public void notifyLCPlayed(ArrayList<LeaderCard> lcp, String s){
        for(ModelObserver obs : observers){
            obs.updateLCPlayed(lcp,s);
        }
    }

    public void notifyLorenzo(LorenzoTrack lorenzo){
        for(ModelObserver obs : observers){
            obs.updateLorenzo(lorenzo);
        }
    }

    public void notifyActionToken(ActionToken AT){
        for(ModelObserver obs : observers){
            obs.updateActionToken(AT);
        }
    }

    public void notifyVictoryPoints(int victoryPoints, String s){
        for(ModelObserver obs : observers){
            obs.updateVP(victoryPoints,s);
        }
    }

    public void notifyActionDone(String s){
        for(ModelObserver obs : observers){
            obs.updateActionDone(s);
        }
    }
}
