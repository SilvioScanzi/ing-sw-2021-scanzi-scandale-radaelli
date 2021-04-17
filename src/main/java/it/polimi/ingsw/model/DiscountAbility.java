package it.polimi.ingsw.model;
import java.util.HashMap;

public class DiscountAbility extends Ability{
    private Resources restype;
    private int discount;

    public DiscountAbility(Resources restype, int discount){
        this.discount=discount;
        this.restype=restype;
    }

    @Override
    public Resources getRestype(){
        return restype;
    }

    @Override
    public HashMap<Resources, Integer> doDiscount(HashMap<Resources, Integer> cost){
        for(Resources r : cost.keySet()){
            if(r.equals(restype)){
                cost.put(r,cost.get(r) + discount);
            }
        }
        return cost;
    }

    @Override
    public String toString(){
        return "Sconto su " + restype.toString() +" di: "+ -discount;
    }
}
