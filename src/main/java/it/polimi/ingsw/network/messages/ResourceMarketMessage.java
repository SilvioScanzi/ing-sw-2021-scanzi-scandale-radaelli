package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.commons.Marbles;

import java.io.Serializable;

public class ResourceMarketMessage extends Message implements Serializable {
    private final Marbles[][] grid;
    private final Marbles remainingMarble;
    private final String[][] gridS;
    private final String remainingMarbleS;

    /*public ResourceMarketMessage(ResourceMarket resourceMarket){
        grid = resourceMarket.getGrid();
        remainingMarble = resourceMarket.getRemainingMarble();
    }*/

    //TODO:ASKCUGOLA
    public ResourceMarketMessage(Marbles[][] grid, Marbles remainingMarble){
        this.grid = grid;
        gridS = new String[3][4];
        for(int i=0;i<3;i++){
            for(int j=0;j<4;j++){
                gridS[i][j] = grid[i][j].abbreviation();
            }
        }
        remainingMarbleS = remainingMarble.abbreviation();
        this.remainingMarble = remainingMarble;
    }

    public String[][] getGridS() {
        return gridS;
    }

    public String getRemainingMarbleS() {
        return remainingMarbleS;
    }

    public Marbles[][] getGrid() {
        return grid;
    }

    public Marbles getRemainingMarble() {
        return remainingMarble;
    }
}
