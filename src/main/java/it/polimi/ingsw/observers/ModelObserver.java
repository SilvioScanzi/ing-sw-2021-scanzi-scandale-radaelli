package it.polimi.ingsw.observers;

import it.polimi.ingsw.commons.ActionToken;
import it.polimi.ingsw.commons.Marbles;
import it.polimi.ingsw.commons.Resources;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.HashMap;

public interface ModelObserver {
    //void updateMarket(ResourceMarket m);
    void updateMarket(Marbles[][] grid, Marbles remainingMarble);
    void updateDCMarket(DevelopmentCardMarket DCM);
    void updateWR(Warehouse wr, String s);
    void updateLCHand(HashMap<Integer,LeaderCard> LCHand, String s);
    void updateSB(Strongbox sb, String s);
    void updateFT(FaithTrack ft, String s);
    void updateSlots(Slot[] slots, String nickname);
    void updateHand(ArrayList<Resources> hand, String s);
    void updateMarketHand(ArrayList<Resources> hand, String s, Marbles[][] grid, Marbles remainingMarble);
    void updateLCPlayed(HashMap<Integer,LeaderCard> lcp, String s);
    void updateLorenzo(LorenzoTrack lorenzo, boolean[] popeFavor);
    void updateActionToken(ActionToken AT);
    void updateActionDone(String s);
    void updateResourceBuyDone(String nickname);
}
