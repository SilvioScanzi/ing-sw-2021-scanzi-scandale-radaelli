package it.polimi.ingsw.model;

import java.util.HashMap;

public class DiscountAbility implements Ability{
    private Resources restype;
    private int discount;

    public DiscountAbility(Resources restype, int discount){
        this.discount=discount;
        this.restype=restype;
    }
    public HashMap<Resources, Integer> doDiscount(HashMap<Resources, Integer> cost){
        for(Resources r : cost.keySet()){
            if(r.equals(restype)){
                cost.put(r,cost.get(r) + discount);
            }
        }
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

    public Resources getRestype() {
        return restype;
    }

    public int getCapacity(){
        return 0;
    }

    public String toString(){
        return "Sconto su " + restype.toString() +" di: "+ -discount;
    }
}
