package it.polimi.ingsw.network.messages;

import java.io.Serializable;
import java.util.HashMap;

public class VictoryPointsMessage extends Message implements Serializable {
    private final HashMap<String,Integer> vp;

    public VictoryPointsMessage(HashMap<String,Integer> vp){
        this.vp = vp;
    }

    public HashMap<String, Integer> getVp() {
        return vp;
    }
}
