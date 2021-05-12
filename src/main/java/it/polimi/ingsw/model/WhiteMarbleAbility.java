package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Resources;

public class WhiteMarbleAbility extends Ability{
    private Resources resType;

    public WhiteMarbleAbility(Resources resType){
        this.resType = resType;
    }

    @Override
    public String toString(){
        return "Conversione della biglia bianca in "+ resType.toString();
    }

    @Override
    public Resources getResType(){
        return resType;
    }

    @Override
    public boolean doConvert(){
        return true;
    }
}
