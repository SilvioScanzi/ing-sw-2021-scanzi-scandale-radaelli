package it.polimi.ingsw.model;

import java.util.Arrays;

public class FaithTrack{
    private int FaithMarker;
    private final boolean[] PopeFavor;

    public FaithTrack() {
        FaithMarker = 0;
        PopeFavor = new boolean[] {false,false,false};
    }

    public int getFaithMarker() {
        return FaithMarker;
    }

    public boolean[] getPopeFavor() {
        return PopeFavor;
    }

    public void advanceTrack(){
        if(FaithMarker<24) FaithMarker = FaithMarker+1;
    }

    /**
     * Method used to check if a pope event has to be activated.
     * @return an integer: -1 if nothing was activated, 1,2,3 if the player is on a pope favor tile.
     *  The integer is the progressive number of pope event to activate (1 for tile 8, 2 for tile 16, 3 for tile 24)
     */
    public int checkPopeFavor(){
        if(FaithMarker == 8 || FaithMarker == 16 || FaithMarker == 24){
            return FaithMarker/8;
        }
        else return -1;
    }

    /**
     * Method used to check if the player qualifies for a pope favor tile and if so, it sets the relative tile.
     * @param index is the progressive number of pope event activated (1 for tile 8, 2 for tile 16, 3 for tile 24)
     */
    public void setPopeFavor(int index){
        //index is the progressive number of the pope favor event, 1 at cell 8, 2 at cell 16, 3 at cell 24
        if(index==1 && (FaithMarker>=5 && FaithMarker<=8)) PopeFavor[0]=true;
        else if(index==2 && (FaithMarker>=12 && FaithMarker<=16)) PopeFavor[1]=true;
        else if(index==3 && (FaithMarker>=19 && FaithMarker<=24)) PopeFavor[2]=true;
    }
}