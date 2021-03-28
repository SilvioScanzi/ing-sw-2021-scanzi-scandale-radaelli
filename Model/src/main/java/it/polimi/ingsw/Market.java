package it.polimi.ingsw;
import java.util.*;

public class Market {
    private final Game.Marbles[][] grid;
    private Game.Marbles remainingMarble;

    public Market(){
        ArrayList<Game.Marbles> tmp = new ArrayList<>();
        tmp.add(Game.Marbles.Red);
        tmp.add(Game.Marbles.Blue);
        tmp.add(Game.Marbles.Blue);
        tmp.add(Game.Marbles.Grey);
        tmp.add(Game.Marbles.Grey);
        tmp.add(Game.Marbles.White);
        tmp.add(Game.Marbles.White);
        tmp.add(Game.Marbles.White);
        tmp.add(Game.Marbles.White);
        tmp.add(Game.Marbles.Yellow);
        tmp.add(Game.Marbles.Yellow);
        tmp.add(Game.Marbles.Purple);
        tmp.add(Game.Marbles.Purple);

        grid = new Game.Marbles[3][4];
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

    public Game.Marbles[][] getGrid() {
        return grid;
    }

    public Game.Marbles getRemainingMarble() {
        return remainingMarble;
    }

    public void setRemainingMarble(Game.Marbles remainingMarble) {
        this.remainingMarble = remainingMarble;
    }

    public ArrayList<Game.Marbles> updateMarket(boolean row, int i) throws IllegalArgumentException{
        if(row){
            if (i>3 || i<1) throw new IllegalArgumentException("Row doesn't exist");
            ArrayList<Game.Marbles> tmp = new ArrayList<Game.Marbles>();
            for(int k=0;k<4;k++) tmp.add(grid[i-1][k]);
            update(row,i-1);
            return tmp;
        }
        else{
            if(i>4 || i<1) throw new IllegalArgumentException("Column doesn't exist");
            ArrayList<Game.Marbles> tmp = new ArrayList<Game.Marbles>();
            for(int k=0;k<3;k++) tmp.add(grid[k][i-1]);
            update(row,i-1);
            return tmp;
        }
    }

    private void update(boolean row, int i){
        if(row){
            Game.Marbles tmp = grid[i][0];
            for(int j=0;j<3;j++){
                grid[i][j]=grid[i][j+1];
            }
            grid[i][3]=remainingMarble;
            remainingMarble=tmp;
        }
        else {
            Game.Marbles tmp = grid[0][i];
            for(int j=0;j<2;j++){
                grid[j][i]=grid[j+1][i];
            }
            grid[2][i]=remainingMarble;
            remainingMarble=tmp;
        }
    }
}
