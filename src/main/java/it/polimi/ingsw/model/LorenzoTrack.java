package it.polimi.ingsw.model;

public class LorenzoTrack {
    private int blackCross;

    public LorenzoTrack(){
        blackCross=0;
    }

    public int getBlackCross() {
        return blackCross;
    }

    public void advanceBlackCross(){
        if(blackCross<24) blackCross = blackCross+1;
    }

    /**
     * Method used to check if a pope event has to be activated.
     * @return an integer: -1 if nothing was activated, 1,2,3 if lorenzo is on a pope favor tile.
     *  The integer is the progressive number of pope event to activate (1 for tile 8, 2 for tile 16, 3 for tile 24)
     */
    public int checkPopeFavor(){
        if(blackCross == 8 || blackCross == 16 || blackCross == 24){
            return blackCross/8;
        }
        else return -1;
    }
}
