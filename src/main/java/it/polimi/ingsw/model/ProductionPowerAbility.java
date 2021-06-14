package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Resources;

public class ProductionPowerAbility extends Ability{
    private final Resources resType;

    public  ProductionPowerAbility(Resources resType){
        this.resType = resType;
    }

    @Override
    public Resources getResType(){
        return resType;
    }

    @Override
    public boolean doActivate(){
        return true;
    }
}
