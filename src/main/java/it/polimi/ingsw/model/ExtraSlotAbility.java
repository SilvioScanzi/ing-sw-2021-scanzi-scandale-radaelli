package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Resources;

public class ExtraSlotAbility extends Ability{
    private final Resources resType;
    private final int capacity;
    private int stashedResources;

    public ExtraSlotAbility(Resources resType, int capacity){
        this.resType = resType;
        this.capacity = capacity;
        stashedResources = 0;
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

    /**
     * Method used to store or retrieve resources from the leader card
     * @param resource type of resource to store or retrieve
     * @param amount amount of resources to store or retrieve
     * @return true if the update was done, false otherwise
     */
    @Override
    public boolean doUpdateSlot(Resources resource, int amount){
        if(!resType.equals(resource)) return false;
        if(amount+capacity<0 || amount + stashedResources > capacity) return false;
        stashedResources = stashedResources + amount;
        return true;
    }
}
