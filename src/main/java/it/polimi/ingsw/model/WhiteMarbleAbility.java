package it.polimi.ingsw.model;

import java.util.HashMap;

public class WhiteMarbleAbility implements Ability{
    private Resources restype;

    public WhiteMarbleAbility(Resources restype){
        this.restype = restype;
    }

    public HashMap<Resources, Integer> doDiscount(HashMap<Resources, Integer> cost){
        return cost;
    }

    public boolean doActivate(){
        return false;
    }

    public boolean doConvert(){
        return true;
    }

    public boolean doUpdateSlot(Resources resource, int amount){
        return false;
    }

    public Resources getRestype() {
        return restype;
    }

    public int getCapacity(){
        return 0;
    }

    public String toString(){
        return "Conversione della biglia bianca in "+restype.toString();
    }
}
