package it.polimi.ingsw;

public class LorenzoTrack {
    private int blackCross;

    public LorenzoTrack(){
        blackCross=0;
    }

    public int getBlackCross() {
        return blackCross;
    }

    public void advanceBlackCross(){
        blackCross = blackCross+1;
    }
}