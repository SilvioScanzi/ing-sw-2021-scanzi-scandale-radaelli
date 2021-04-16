package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class UpdateMarketTest {
    private Market market;
    private Marbles[][] grid = new Marbles[3][4];

    @BeforeEach
    void setup(){
        market = new Market(0);
        grid = market.getGrid();
    }

    @Test
    @DisplayName("ensure the number of white marbles returned is correct")
    void testGetWM(){
        int WMNumber = 0;
        int getWMrow = 0;
        int getWMcol = 0;
        //get the number of white marbles
        for(int i=0;i<3;i++){
            for(int j=0;j<4;j++){
                if(grid[i][j].equals(Marbles.White)) WMNumber++;
            }
        }
        //test the method for rows and columns
        for(int i=0;i<3;i++){
            getWMrow += market.getWhiteMarbles(true,i+1);
        }
        for(int i=0;i<4;i++){
            getWMcol += market.getWhiteMarbles(false,i+1);
        }

        assert(WMNumber == getWMrow);
        assert(WMNumber == getWMcol);
    }

    @Test
    @DisplayName("ensure update methos works")
    void testUpdateMarket(){
        Marbles[][] previousGrid = new Marbles[3][4];
        for(int i=0;i<3;i++){
            for(int j=0;j<4;j++){
                previousGrid[i][j] = grid[i][j];
            }
        }

        for(int i=0;i<3;i++){
            ArrayList<Marbles> received = market.updateMarket(true,i+1);
            ArrayList<Marbles> gotMarbles = new ArrayList<>();
            Arrays.stream(previousGrid[i]).forEach(e -> gotMarbles.add(e));
            assert(received.equals(gotMarbles));
        }

        //restart from the initial grid situation
        for(int i=0;i<3;i++){
            for(int j=0;j<4;j++){
                grid[i][j] = previousGrid[i][j];
            }
        }

        for(int i=0;i<4;i++){
            ArrayList<Marbles> received = market.updateMarket(false,i+1);
            ArrayList<Marbles> gotMarbles = new ArrayList<>();
            for(int j=0;j<3;j++){
                gotMarbles.add(previousGrid[j][i]);
            }
            assert(received.equals(gotMarbles));
        }
    }
}
