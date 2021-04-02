package it.polimi.ingsw.model;

import java.util.*;

public class Board {
    private final String nickname;
    private final FaithTrack faithtrack;
    private final Warehouse warehouse;
    private final Strongbox strongbox;
    private final Slot[] slots;
    private final ArrayList<LeaderCard> leadercards;

    public Board(String nickname, ArrayList<LeaderCard> leadercards) {
        this.nickname = nickname;
        faithtrack = new FaithTrack();
        warehouse = new Warehouse();
        strongbox = new Strongbox();
        slots = new Slot[3];
        for(int i=0;i<3;i++){
            slots[i] = new Slot();
        }
        this.leadercards = new ArrayList<LeaderCard> (leadercards);
    }

    public String getNickname() {
        return nickname;
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

    public ArrayList<LeaderCard> getLeadercards() {
        return leadercards;
    }
}
