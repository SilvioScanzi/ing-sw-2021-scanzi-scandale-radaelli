package it.polimi.ingsw;
import it.polimi.ingsw.model.*;

import java.util.*;

public class Game extends Observable implements Observer{
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
    private leaderCardDeck leadercarddeck;

    private LorenzoTrack Lorenzo;
    private ActionStack actionStack;

    public Game() {
        inkwell = 0;
        nplayer = 0;
        nickname = new HashMap<>();
        boards = new ArrayList<>();
        market = new Market();
        developmentcardmarket = new DevelopmentCardMarket();
        vaticanReport = new boolean[] {false,false,false};
        leadercarddeck = new leaderCardDeck();
        Lorenzo = new LorenzoTrack();
        actionStack = new ActionStack();
    }

    public void setup(int n,String[] names){
        //distribuzione randomica
    }

    public void update(Observable a, Object b){

    }

    public boolean checkwin(){
        return false;
    }
}
