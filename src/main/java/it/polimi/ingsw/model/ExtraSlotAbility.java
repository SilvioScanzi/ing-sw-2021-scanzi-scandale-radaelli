package it.polimi.ingsw.model;

public class ExtraSlotAbility extends Ability{
    private Resources restype;
    private int capacity;
    private int stashedResources;

    public ExtraSlotAbility(Resources restype, int capacity){
        this.restype=restype;
        this.capacity = capacity;
        stashedResources = 0;
    }

    @Override
    public Resources getRestype(){
        return restype;
    }

    @Override
    public boolean doUpdateSlot(Resources resource, int amount){
        if(!restype.equals(resource)) return false;
        if(amount+capacity<0 || amount + stashedResources > capacity) return false;
        stashedResources = stashedResources + amount;
        return true;
    }

    @Override
    public int getStashedResources(){
        return stashedResources;
    }

    @Override
    public int getCapacity(){ return capacity; }

    @Override
    public String toString(){
        return capacity +" Slot extra per "+restype.toString()+" attualmente sono presenti: "+stashedResources+" "+restype.toString();
    }
}
