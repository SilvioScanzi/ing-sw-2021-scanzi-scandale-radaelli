package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.Marbles;
import it.polimi.ingsw.model.Market;

import java.io.Serializable;

public class MarketMessage extends Message implements Serializable {
    Marbles[][] grid;
    Marbles remainingMarble;

    public MarketMessage(Marbles[][] grid,Marbles RM) {
        this.grid = grid;
        remainingMarble = RM;
    }

    public MarketMessage(Market market){
        grid = market.getGrid();
        remainingMarble = market.getRemainingMarble();
    }

    public Marbles[][] getGrid() {
        return grid;
    }

    public Marbles getRemainingMarble() {
        return remainingMarble;
    }
}
