package it.polimi.ingsw.model;

public class ProductionPowerAbility extends Ability{
    private Resources resType;

    public  ProductionPowerAbility(Resources resType){
        this.resType = resType;
    }

    @Override
    public String toString(){
        return "Potere di produzione: richiesta la risorsa "+ resType.toString();
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
