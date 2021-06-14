package it.polimi.ingsw.model;
import it.polimi.ingsw.commons.Resources;

import java.util.HashMap;

public class DiscountAbility extends Ability{
    private final Resources resType;
    private final int discount;

    public DiscountAbility(Resources restype, int discount){
        this.discount = discount;
        this.resType = restype;
    }

    @Override
    public Resources getResType(){
        return resType;
    }

    /**
     * Method used to reduce the cost of the development card to buy. Discount is a negative number.
     * @param cost cost of the development card to buy
     * @return updated cost, adding the discount provided by the card
     */
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
