package it.polimi.ingsw.model;
import it.polimi.ingsw.commons.Resources;

import java.util.HashMap;

public class DiscountAbility extends Ability{
    private Resources resType;
    private int discount;

    public DiscountAbility(Resources restype, int discount){
        this.discount=discount;
        this.resType=restype;
    }

    @Override
    public String toString(){
        return "Sconto su " + resType.toString() +" di: "+ -discount;
    }

    @Override
    public Resources getResType(){
        return resType;
    }

    @Override
    public HashMap<Resources, Integer> doDiscount(HashMap<Resources, Integer> cost){
        for(Resources r : cost.keySet()){
            if(r.equals(resType)){
                cost.put(r,cost.get(r) + discount);
            }
        }
        return cost;
    }

}
