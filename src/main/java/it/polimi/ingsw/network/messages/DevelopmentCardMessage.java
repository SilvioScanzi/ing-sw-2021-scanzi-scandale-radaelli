package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.Colours;

import java.io.Serializable;

public class DevelopmentCardMessage extends Message implements Serializable {
    private Colours c;
    private int victoryPoints;

    public DevelopmentCardMessage(Colours c, int victoryPoints) {
        this.c = c;
        this.victoryPoints = victoryPoints;
    }

    public Colours getC() {
        return c;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }
}
