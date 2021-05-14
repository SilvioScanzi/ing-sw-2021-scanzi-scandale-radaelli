package it.polimi.ingsw.observers;

import it.polimi.ingsw.commons.ActionToken;
import it.polimi.ingsw.commons.Resources;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;

public interface ModelObserver {
    void updateMarket(ResourceMarket m);
    void updateDCMarket(DevelopmentCardMarket DCM);
    void updateWR(Warehouse wr, String s);
    void updateLCHand(ArrayList<LeaderCard> LCHand, String s);
    void updateSB(Strongbox sb, String s);
    void updateFT(FaithTrack ft, String s);
    void updateSlots(DevelopmentCard DC, int slotIndex, String nickname);
    void updateHand(ArrayList<Resources> hand, String s);
    void updateLCPlayed(ArrayList<LeaderCard> lcp, String s);
    void updateVP(int victoryPoints,String s);
    void updateLorenzo(LorenzoTrack lorenzo);
    void updateActionToken(ActionToken AT);
    void updateActionDone(String s);
}
