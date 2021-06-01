package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.commons.Pair;
import it.polimi.ingsw.commons.Resources;

import java.io.Serializable;
import java.util.HashMap;

public class LCMapMessage extends Message implements Serializable {
    private final HashMap<Integer, Pair<Resources,Integer>> LCMap;

    public LCMapMessage(HashMap<Integer, Pair<Resources,Integer>> LCMap){
        this.LCMap = LCMap;
    }

    public HashMap<Integer, Pair<Resources, Integer>> getLCMap() {
        return LCMap;
    }
}
