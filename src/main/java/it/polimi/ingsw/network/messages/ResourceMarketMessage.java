package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.commons.Marbles;
import it.polimi.ingsw.model.ResourceMarket;

import java.io.Serializable;

public class ResourceMarketMessage extends Message implements Serializable {
    private final Marbles[][] grid;
    private final Marbles remainingMarble;

    public ResourceMarketMessage(Marbles[][] grid, Marbles remainingMarble) {
        this.grid = grid;
        this.remainingMarble = remainingMarble;
    }

    public ResourceMarketMessage(ResourceMarket resourceMarket){
        grid = resourceMarket.getGrid();
        remainingMarble = resourceMarket.getRemainingMarble();
    }

    public Marbles[][] getGrid() {
        return grid;
    }

    public Marbles getRemainingMarble() {
        return remainingMarble;
    }
}
