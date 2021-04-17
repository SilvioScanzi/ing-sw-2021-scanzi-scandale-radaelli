package it.polimi.ingsw.model;

public class ProductionPowerAbility extends Ability{
    private Resources restype;

    public  ProductionPowerAbility(Resources restype){
        this.restype = restype;
    }

    @Override
    public Resources getRestype(){
        return restype;
    }

    @Override
    public boolean doActivate(){
        return true;
    }

    @Override
    public String toString(){
        return "Potere di produzione: richiesta la risorsa "+restype.toString();
    }
}
