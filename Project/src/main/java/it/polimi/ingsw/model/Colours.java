package it.polimi.ingsw.model;

public enum Colours {
    Purple,
    Yellow,
    Green,
    Blue;

    @Override
    public String toString(){
        switch(this){
            case Yellow: return "Giallo";
            case Purple: return "Viola";
            case Blue: return "Blu";
            case Green: return "Verde";
            default: return "Error";
        }
    }
}
