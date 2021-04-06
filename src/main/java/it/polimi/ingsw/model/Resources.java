package it.polimi.ingsw.model;

public enum Resources {
    Coins,
    Stones,
    Servants,
    Shields;

    @Override
    public String toString(){
        switch(this){
            case Coins: return "Monete";
            case Stones: return "Pietre";
            case Shields: return "Scudi";
            case Servants: return "Servitori";
            default: return "Error";
        }
    }

    public String abbreviation(){
        switch(this){
            case Coins: return "MO";
            case Stones: return "PI";
            case Shields: return "SC";
            case Servants: return "SE";
            default: return "Error";
        }
    }
}
