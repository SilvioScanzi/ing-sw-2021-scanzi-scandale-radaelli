package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.commons.Colours;
import it.polimi.ingsw.commons.Pair;
import it.polimi.ingsw.model.Slot;

import java.io.Serializable;
import java.util.ArrayList;

public class SlotMessage extends Message implements Serializable {
    private final ArrayList<Pair<Colours,Integer>> Slot_1;
    private final ArrayList<Pair<Colours,Integer>> Slot_2;
    private final ArrayList<Pair<Colours,Integer>> Slot_3;
    private String nickname;

    public SlotMessage(Slot[] slots, String nickname){
        this.nickname = nickname;
        Slot_1 = new ArrayList<>(slots[0].getSlotListVP());
        Slot_2 = new ArrayList<>(slots[1].getSlotListVP());
        Slot_3 = new ArrayList<>(slots[2].getSlotListVP());
    }

    public String getNickname() {
        return nickname;
    }

    public ArrayList<ArrayList<Pair<Colours, Integer>>> getSlots(){
        ArrayList<ArrayList<Pair<Colours, Integer>>> tmp = new ArrayList<>();
        tmp.add(Slot_1);
        tmp.add(Slot_2);
        tmp.add(Slot_3);
        return tmp;
    }

    /*private final int slotIndex;
    private final int victoryPoints;
    private final Colours colour;
    private final String nickname;

    public SlotMessage(DevelopmentCard DC, int slotIndex, String nickname) {
        this.nickname = nickname;
        this.slotIndex = slotIndex;
        victoryPoints = DC.getVictoryPoints();
        colour = DC.getColour();
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public Colours getColour() {
        return colour;
    }

    public String getNickname() {
        return nickname;
    }*/
}
