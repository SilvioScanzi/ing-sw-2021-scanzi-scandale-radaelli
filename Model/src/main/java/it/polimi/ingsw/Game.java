package it.polimi.ingsw;
import java.util.*;

public class Game {
    public enum Marbles {Grey,Purple,Red,Blue,Yellow,White}
    public enum Resources {Coins,Stones,Servants,Shields}
    public enum Colours {Purple,Yellow,Green,Blue}
    public enum ActionToken {Advance2,AdvanceAndRefresh,DeleteGreen,DeleteYellow,DeletePurple,DeleteBlue}

    private int inkwell;
    private int nplayer;
    private Map<Integer,String> nickname;
    private ArrayList<Board> boards;
    private Market market;
    private DevelopmentCardMarket developmentcardmarket;
    private boolean[] vaticanReport;
    private LorenzoTrack Lorenzo;

    private ArrayList<LeaderCard> leaderCards;

    public Game() {
        inkwell = 0;
        nplayer = 0;
        nickname = new HashMap<>();
        boards = new ArrayList<>();
        market = new Market();
        developmentcardmarket = new DevelopmentCardMarket();
        vaticanReport = new boolean[] {false,false,false};
        Lorenzo = new LorenzoTrack();
        leaderCards = new ArrayList<>(); //da inizializzare
    }

    public void setup(int n,String[] names){
        //distribuzione randomica
    }

    public boolean checkwin(){
        return false;
    }
}
