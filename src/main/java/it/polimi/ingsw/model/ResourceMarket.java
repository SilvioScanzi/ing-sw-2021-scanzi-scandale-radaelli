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

    //Only used for testing
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

    @Override
    public String toString(){
        String tmp = "Mercato delle risorse:\n";
        for(int i=0;i<3;i++){
            for(int j=0;j<4;j++){
                tmp=tmp.concat(grid[i][j].abbreviation() + " ");
            }
            tmp=tmp.concat("\n");
        }
        tmp=tmp.concat("La biglia rimanente è: "+remainingMarble.toString());
        return tmp;
    }

    public Marbles[][] getGrid() {
        return grid;
    }

    public Marbles getRemainingMarble() {
        return remainingMarble;
    }

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
