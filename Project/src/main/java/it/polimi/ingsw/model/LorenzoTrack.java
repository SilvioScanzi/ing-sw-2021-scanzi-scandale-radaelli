package it.polimi.ingsw.model;

public class LorenzoTrack {
    private int blackCross;

    public LorenzoTrack(){
        blackCross=0;
    }

    @Override
    public String toString(){
        return "Lorenzo è nella posizione: "+blackCross;
    }

    public int getBlackCross() {
        return blackCross;
    }

    public void advanceBlackCross(){
        blackCross = blackCross+1;
    }
}
