package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Resources;

public class ExtraSlotAbility extends Ability{
    private Resources resType;
    private int capacity;
    private int stashedResources;

    public ExtraSlotAbility(Resources resType, int capacity){
        this.resType = resType;
        this.capacity = capacity;
        stashedResources = 0;
    }

    @Override
    public String toString(){
        return capacity +" Slot extra per "+resType.toString()+" attualmente sono presenti: "+stashedResources+" "+resType.toString();
    }

    @Override
    public Resources getResType(){
        return resType;
    }

    @Override
    public int getStashedResources(){
        return stashedResources;
    }

    @Override
    public int getCapacity(){ return capacity; }

    @Override
    public boolean doUpdateSlot(Resources resource, int amount){
        if(!resType.equals(resource)) return false;
        if(amount+capacity<0 || amount + stashedResources > capacity) return false;
        stashedResources = stashedResources + amount;
        return true;
    }
}
