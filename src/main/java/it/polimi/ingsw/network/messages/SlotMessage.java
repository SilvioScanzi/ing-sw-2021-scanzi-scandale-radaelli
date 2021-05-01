package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.Colours;
import it.polimi.ingsw.model.DevelopmentCard;

import java.io.Serializable;

public class SlotMessage extends Message implements Serializable {
    private int slotNumber;
    private Colours colour;
    private int victoryPoints;

    public SlotMessage(int slotNumber, DevelopmentCard DC) {
        this.slotNumber = slotNumber;
        colour = DC.getColour();
        victoryPoints = DC.getVictoryPoints();
    }
}
