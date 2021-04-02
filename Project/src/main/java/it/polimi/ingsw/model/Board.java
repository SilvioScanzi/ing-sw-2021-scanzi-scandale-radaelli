package it.polimi.ingsw.model;

import java.util.*;

public class Board {
    private final FaithTrack faithtrack;
    private Warehouse warehouse;
    private Strongbox strongbox;
    private final Slot[] slots;
    private final ArrayList<LeaderCard> leadercardshand;
    private final ArrayList<LeaderCard> leadercardsplayed;

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
