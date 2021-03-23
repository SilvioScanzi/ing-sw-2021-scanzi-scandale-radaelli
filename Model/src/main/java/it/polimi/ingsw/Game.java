package it.polimi.ingsw;
import java.util.*;

public class Game {
    public enum Marbles {Grey,Purple,Red,Blue,Yellow,White}
    public enum Resources {Coins,Stones,Servants,Shields,Nothing}
    public enum Colours {Purple,Yellow,Green,Blue}

    private int inkwell;
    private int nplayer;
    private Map<Integer,String> nickname;
    private ArrayList<Board> boards;
    private Market market;
    private boolean[] VaticanReport;

    public Game() {
    }

    public void setup(int n){

    }

    public boolean checkwin(){
        return false;
    }
}
