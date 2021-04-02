package it.polimi.ingsw.model;

import java.util.*;

public class Market {
    private final Marbles[][] grid;
    private Marbles remainingMarble;

    public Market(){
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

    public Marbles[][] getGrid() {
        return grid;
    }

    public Marbles getRemainingMarble() {
        return remainingMarble;
    }

    public void setRemainingMarble(Marbles remainingMarble) {
        this.remainingMarble = remainingMarble;
    }

    public ArrayList<Marbles> updateMarket(boolean row, int i) throws IllegalArgumentException{
        if(row){
            if (i>3 || i<1) throw new IllegalArgumentException("Row doesn't exist");
            ArrayList<Marbles> tmp = new ArrayList<Marbles>();
            for(int k=0;k<4;k++) tmp.add(grid[i-1][k]);
            update(row,i-1);
            return tmp;
        }
        else{
            if(i>4 || i<1) throw new IllegalArgumentException("Column doesn't exist");
            ArrayList<Marbles> tmp = new ArrayList<Marbles>();
            for(int k=0;k<3;k++) tmp.add(grid[k][i-1]);
            update(row,i-1);
            return tmp;
        }
    }

    private void update(boolean row, int i){
        if(row){
            Marbles tmp = grid[i][0];
            for(int j=0;j<3;j++){
                grid[i][j]=grid[i][j+1];
            }
            grid[i][3]=remainingMarble;
            remainingMarble=tmp;
        }
        else {
            Marbles tmp = grid[0][i];
            for(int j=0;j<2;j++){
                grid[j][i]=grid[j+1][i];
            }
            grid[2][i]=remainingMarble;
            remainingMarble=tmp;
        }
    }
}
