package it.polimi.ingsw.observers;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;

public interface ModelObserver {
    void updateMarket(Market m);
    void updateDCMarket(DevelopmentCard DC);
    void updateWR(Warehouse wr);
    void updateSB(Strongbox sb);
    void updateFT(FaithTrack ft);
    void updateSlots(int number,DevelopmentCard DC);
    void updateHand(ArrayList<Resources> hand);
    void updateLCPlayed(LeaderCard lcp);
}
