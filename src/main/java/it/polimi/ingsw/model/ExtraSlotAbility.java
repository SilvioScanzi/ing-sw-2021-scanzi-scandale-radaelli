package it.polimi.ingsw.model;

import java.util.HashMap;

public class ExtraSlotAbility implements Ability{
    private Resources restype;
    private int capacity;
    private int stashedResources;

    public ExtraSlotAbility(Resources restype, int capacity){
        this.restype=restype;
        this.capacity = capacity;
        stashedResources = 0;
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
        if(!restype.equals(resource)) return false;
        if(amount+capacity<0 || amount + stashedResources > capacity) return false;
        stashedResources = stashedResources + amount;
        return true;
    }

    public Resources getRestype() {
        return restype;
    }

    //other abilities return 0; here returns the number of stored resources
    public int getStashedResources(){
        return stashedResources;
    }

    public int getCapacity(){ return capacity; }

    public String toString(){
        return capacity +" Slot extra per "+restype.toString()+" attualmente sono presenti: "+stashedResources+" "+restype.toString();
    }
}
