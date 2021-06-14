package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Resources;

public class WhiteMarbleAbility extends Ability{
    private final Resources resType;

    public WhiteMarbleAbility(Resources resType){
        this.resType = resType;
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
