package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Marbles;

import java.util.*;

public class ResourceMarket {
    private final Marbles[][] grid;
    private Marbles remainingMarble;

    public ResourceMarket(){
        ArrayList<Marbles> tmp = new ArrayList<>();
        tmp.add(Marbles.Red);
        tmp.add(Marbles.Blue);
        tmp.add(Marbles.Blue);
        tmp.add(Marbles.Grey);
        tmp.add(Marbles.Grey);
        tmp.add(Marbles.White);
        tmp.add(Marbles.White);
        tmp.add(Marbles.White);
        tmp.add(Marbles.White);
        tmp.add(Marbles.Yellow);
        tmp.add(Marbles.Yellow);
        tmp.add(Marbles.Purple);
        tmp.add(Marbles.Purple);

        grid = new Marbles[3][4];
        int k=0;
        for(int i=0;i<3;i++){
            for(int j=0;j<4;j++){
                int index;
                index = (int) (Math.random() * (13-k));
                grid[i][j] = tmp.remove(index);
                k++;
            }
        }
        remainingMarble = tmp.remove(0);
    }

    /**
     * Method used for testing, it initialize a resource market with a standard configuration
     * @param Arandom is just used to overload the method
     */
    public ResourceMarket(int Arandom){
        ArrayList<Marbles> tmp = new ArrayList<>();
        tmp.add(Marbles.Red);
        tmp.add(Marbles.Blue);
        tmp.add(Marbles.Blue);
        tmp.add(Marbles.Grey);
        tmp.add(Marbles.Grey);
        tmp.add(Marbles.Yellow);
        tmp.add(Marbles.Yellow);
        tmp.add(Marbles.Purple);
        tmp.add(Marbles.White);
        tmp.add(Marbles.White);
        tmp.add(Marbles.White);
        tmp.add(Marbles.White);
        tmp.add(Marbles.Purple);


        grid = new Marbles[3][4];
        int k=0;
        for(int i=0;i<3;i++){
            for(int j=0;j<4;j++){
                grid[i][j] = tmp.remove(0);
                k++;
            }
        }
        remainingMarble = tmp.remove(0);
    }

    public Marbles[][] getGrid() {
        return grid;
    }

    public Marbles getRemainingMarble() {
        return remainingMarble;
    }

    /**
     * Method used to count the number of white marbles in the chosen row or column
     * @param row boolean which tells if the player chose a row (true) or a column (false)
     * @param i index of the row or column selected
     * @return number of white marbles in the chosen row or column
     */
    public int getWhiteMarbles(boolean row, int i){
        int n=0;
        if(row){
            for(int k=0;k<4;k++) n = n + (((grid[i-1][k]).equals(Marbles.White))?1:0);
        }
        else{
            for(int k=0;k<3;k++) n = n + (((grid[k][i-1]).equals(Marbles.White))?1:0);
        }
        return n;
    }

    /**
     * Method used to update the market after a player buys the resources from a chosen row or column
     * @param row boolean which tells if the player chose a row (true) or a column (false)
     * @param i index of the row or column selected
     * @return the marbles contained in the row or column chosen by the player
     */
    public ArrayList<Marbles> updateMarket(boolean row, int i){
        ArrayList<Marbles> tmp = new ArrayList<>();
        i = i-1;
        if(row){
            for(int k=0;k<4;k++) tmp.add(grid[i][k]);
            Marbles mb = grid[i][0];
            for(int j=0;j<3;j++){
                grid[i][j] = grid[i][j+1];
            }
            grid[i][3] = remainingMarble;
            remainingMarble = mb;
        }
        else{
            for(int k=0;k<3;k++) tmp.add(grid[k][i]);
            Marbles mb = grid[0][i];
            for(int j=0;j<2;j++){
                grid[j][i] = grid[j+1][i];
            }
            grid[2][i] = remainingMarble;
            remainingMarble = mb;
        }
        return tmp;
    }
}
