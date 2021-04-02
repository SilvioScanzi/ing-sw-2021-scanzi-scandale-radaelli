package it.polimi.ingsw.model;

public class FaithTrack {
    private int FaithMarker;
    private boolean[] PopeFavor;

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
        FaithMarker = FaithMarker+1;
    }

    public void setPopeFavor(int number) throws IllegalArgumentException{
        if(number<=0 || number>3)
            throw new IllegalArgumentException("Inexistent Pope Favor");
        PopeFavor[number-1]=true;
    }
}
