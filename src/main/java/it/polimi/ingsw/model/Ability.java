package it.polimi.ingsw.model;
import it.polimi.ingsw.commons.Resources;

import java.util.HashMap;

public abstract class Ability {

    public Resources getResType(){
        return null;
    }

    public int getStashedResources(){
        return 0;
    }

    public int getCapacity(){
        return 0;
    }

    public HashMap<Resources, Integer> doDiscount(HashMap<Resources, Integer> cost){
        return cost;
    }

    public boolean doActivate(){
        return false;
    }

    public boolean doConvert(){
        return false;
    }

    public boolean doUpdateSlot(Resources resource, int amount){
        return false;
    }
}
