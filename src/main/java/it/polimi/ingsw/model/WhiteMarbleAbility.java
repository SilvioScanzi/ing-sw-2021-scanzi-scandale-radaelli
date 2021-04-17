package it.polimi.ingsw.model;

public class WhiteMarbleAbility extends Ability{
    private Resources restype;

    public WhiteMarbleAbility(Resources restype){
        this.restype = restype;
    }

    @Override
    public Resources getRestype(){
        return restype;
    }

    @Override
    public boolean doConvert(){
        return true;
    }

    @Override
    public String toString(){
        return "Conversione della biglia bianca in "+restype.toString();
    }
}
