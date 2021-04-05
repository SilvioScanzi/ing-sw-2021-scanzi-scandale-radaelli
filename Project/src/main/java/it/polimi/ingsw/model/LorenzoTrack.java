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
        if(blackCross<24) blackCross = blackCross+1;
    }

    public int checkPopeFavor(){
        if(blackCross == 8 || blackCross == 16 || blackCross == 24){
            return blackCross/8;
        }
        else return -1;
    }
}
