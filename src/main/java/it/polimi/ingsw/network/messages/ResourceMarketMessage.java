package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.commons.Marbles;

import java.io.Serializable;

public class ResourceMarketMessage extends Message implements Serializable {
    private final Marbles[][] grid;
    private final Marbles remainingMarble;

    public ResourceMarketMessage(Marbles[][] grid, Marbles remainingMarble){
        this.grid = grid;
        this.remainingMarble = remainingMarble;
    }

    public Marbles[][] getGrid() {
        return grid;
    }

    public Marbles getRemainingMarble() {
        return remainingMarble;
    }
}
