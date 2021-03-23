package it.polimi.ingsw;
import java.util.*;

public class Board {
    private String nickname;
    private FaithTrack faithtrack;
    private Warehouse warehouse;
    private Strongbox strongbox;
    private Slot[] slots;
    private LeaderCard[] leadercards;

    public Board(String nickname) {
        this.nickname = nickname;
        //da aggiungere l'inizializzazione della board
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

    public LeaderCard[] getLeadercards() {
        return leadercards;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setFaithtrack(FaithTrack faithtrack) {
        this.faithtrack = faithtrack;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public void setStrongbox(Strongbox strongbox) {
        this.strongbox = strongbox;
    }

    public void setSlots(Slot[] slots) {
        this.slots = slots;
    }

    public void setLeadercards(LeaderCard[] leadercards) {
        this.leadercards = leadercards;
    }
}
