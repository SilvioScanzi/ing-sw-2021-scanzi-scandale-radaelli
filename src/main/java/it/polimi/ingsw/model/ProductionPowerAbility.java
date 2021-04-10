package it.polimi.ingsw.model;

import java.util.HashMap;

public class ProductionPowerAbility implements Ability{
    private Resources restype;

    public  ProductionPowerAbility(Resources restype){
        this.restype = restype;
    }

    public HashMap<Resources, Integer> doDiscount(HashMap<Resources, Integer> cost){
        return cost;
    }

    public boolean doActivate(){
        return true;
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
        return "Potere di produzione: richiesta la risorsa "+restype.toString();
    }
}
