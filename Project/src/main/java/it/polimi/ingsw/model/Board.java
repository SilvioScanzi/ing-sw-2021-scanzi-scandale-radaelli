package it.polimi.ingsw.model;

import java.util.*;

public class Board {
    private final FaithTrack faithtrack;
    private Warehouse warehouse;
    private Strongbox strongbox;
    private final Slot[] slots;
    private final ArrayList<LeaderCard> leadercardshand;
    private final ArrayList<LeaderCard> leadercardsplayed;
    private ArrayList<Resources> hand;
    private boolean flagIncompatibleResources;
    private boolean flagResourceError;

    public Board(String nickname, ArrayList<LeaderCard> leadercards) {
        faithtrack = new FaithTrack();
        warehouse = new Warehouse();
        strongbox = new Strongbox();
        slots = new Slot[3];
        for(int i=0;i<3;i++){
            slots[i] = new Slot();
        }
        leadercardshand = new ArrayList<LeaderCard> (leadercards);
        leadercardsplayed = new ArrayList<LeaderCard>();
        hand = new ArrayList<>();
        flagIncompatibleResources = false;
        flagResourceError = false;
    }

    public void setFlagIncompatibleResources(boolean flagIncompatibleResources) {
        this.flagIncompatibleResources = flagIncompatibleResources;
    }

    public void setFlagResourceError(boolean flagResourceError) {
        this.flagResourceError = flagResourceError;
    }

    public void clearWarehouse(){
        for(int i=1;i<4;i++) {
            hand.addAll(warehouse.clear(i));
        }
    }

    public ArrayList<Resources> getHand() {
        return hand;
    }

    public ArrayList<LeaderCard> getLeadercardsplayed() {
        return leadercardsplayed;
    }

    public void setHand(ArrayList<Resources> hand) {
        this.hand = hand;
    }

    public FaithTrack getFaithtrack() {
        return faithtrack;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public Strongbox getStrongbox() {
        return strongbox;
    }

    public Slot[] getSlots() {
        return slots;
    }

    public Slot getSlot(int index) throws IllegalArgumentException{
        if(index < 1 || index > 3) throw new IllegalArgumentException("Invalid slot: index must be between 1 and 3");
        return slots[index-1];
    }

    public ArrayList<LeaderCard> getLeadercards() {
        return leadercardshand;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public void setStrongbox(Strongbox strongbox) {
        this.strongbox = strongbox;
    }
}
