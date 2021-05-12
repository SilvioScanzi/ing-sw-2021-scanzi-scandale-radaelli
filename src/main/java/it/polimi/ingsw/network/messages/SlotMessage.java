package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.commons.Colours;
import it.polimi.ingsw.model.DevelopmentCard;

import java.io.Serializable;

public class SlotMessage extends Message implements Serializable {
    private final int slotIndex;
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
    }
}
