package it.polimi.ingsw.model;

public enum Marbles {
    Grey,
    Purple,
    Red,
    Blue,
    Yellow,
    White;

    @Override
    public String toString(){
        switch(this){
            case Blue: return "Biglia blu";
            case Grey: return "Biglia grigia";
            case Purple: return "Biglia viola";
            case Yellow: return "Biglia gialla";
            case White: return "Biglia bianca";
            case Red: return "Biglia rossa";
            default: return "Error";
        }
    }

    public String abbreviation(){
        switch(this){
            case Blue: return "BL";
            case Grey: return "GR";
            case Purple: return "VI";
            case Yellow: return "GI";
            case White: return "BI";
            case Red: return "RO";
            default: return "Error";
        }
    }
}
