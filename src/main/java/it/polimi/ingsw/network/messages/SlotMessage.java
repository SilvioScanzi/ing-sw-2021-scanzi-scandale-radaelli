package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.Slot;

import java.io.Serializable;

public class SlotMessage extends Message implements Serializable {
    private Slot slot;
    private String nickname;

    public SlotMessage(Slot slot, String s) {
        this.slot = slot;
        nickname = s;
    }

    public Slot getSlot() {
        return slot;
    }

    public String getNickname() {
        return nickname;
    }
}
