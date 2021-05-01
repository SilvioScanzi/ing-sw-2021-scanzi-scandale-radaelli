package it.polimi.ingsw.model;

public class FaithTrack{
    private int FaithMarker;
    private boolean[] PopeFavor;

    public FaithTrack() {
        FaithMarker = 0;
        PopeFavor = new boolean[] {false,false,false};
    }

    @Override
    public String toString(){
        String tmp = new String("Il segnalino fede è sullo spazio numero: "+FaithMarker);
        boolean flag=false;
        for(int i=0;i<3;i++){
            if(PopeFavor[i]) tmp=tmp.concat("\nHai ottenuto il segnalino papale numero: "+i);
            flag=true;
        }
        if(!flag){
            tmp=tmp.concat("\nNon hai ottenuto nessun segnalino favore papale");
        }
        return tmp;
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

    //checks if the player reached the pope event
    public int checkPopeFavor(){
        if(FaithMarker == 8 || FaithMarker == 16 || FaithMarker == 24){
            return FaithMarker/8;
        }
        else return -1;
    }

    public void setPopeFavor(int index){
        //index is the progressive number of the pope favor event, 1 at cell 8, 2 at cell 16, 3 at cell 24
        if(index==1 && (FaithMarker>=5 && FaithMarker<=8)) PopeFavor[0]=true;
        else if(index==2 && (FaithMarker>=12 && FaithMarker<=16)) PopeFavor[1]=true;
        else if(index==3 && (FaithMarker>=19 && FaithMarker<=24)) PopeFavor[2]=true;
    }
}