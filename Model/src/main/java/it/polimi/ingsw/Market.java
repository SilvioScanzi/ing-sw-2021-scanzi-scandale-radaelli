package it.polimi.ingsw;
import java.util.*;

public class Market {
    private Game.Marbles[][] grid;
    private Game.Marbles remainingMarble;
    private Map<Game.Marbles,Integer> MarbleCount;

    public Market(){

    }

    public Game.Marbles[][] getGrid() {
        return grid;
    }

    public Game.Marbles getRemainingMarble() {
        return remainingMarble;
    }

    public void setGrid(Game.Marbles[][] grid) {
        this.grid = grid;
    }

    public void setRemainingMarble(Game.Marbles remainingMarble) {
        this.remainingMarble = remainingMarble;
    }
}
