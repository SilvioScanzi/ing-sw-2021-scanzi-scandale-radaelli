package it.polimi.ingsw;
import java.util.*;

public class Game {
    public enum Marbles {Grey,Purple,Red,Blue,Yellow,White}
    public enum Resources {Coins,Stones,Servants,Shields}
    public enum Colours {Purple,Yellow,Green,Blue}
    public enum ActionToken {Advance2,dupAdvance2,AdvanceAndRefresh,DeleteGreen,DeleteYellow,DeletePurple,DeleteBlue}

    private int inkwell;
    private int nplayer;
    private Map<Integer,String> nickname;
    private ArrayList<Board> boards;
    private Market market;
    private DevelopmentCardMarket developmentcardmarket;
    private boolean[] VaticanReport;
    private LorenzoTrack Lorenzo;


    public Game() {
    }

    public void setup(int n){

    }

    public boolean checkwin(){
        return false;
    }
}
