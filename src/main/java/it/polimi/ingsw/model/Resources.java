package it.polimi.ingsw.model;


public enum Resources {
    Coins("MO"),
    Stones("PI"),
    Servants("SE"),
    Shields("SC");

    public final String label;
    Resources(String label) {
        this.label = label;
    }

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

    public static Resources getResourceFromString(String string) throws IllegalArgumentException {
        switch(string){
            case "MO" : return Coins;
            case "PI" : return Stones;
            case "SC" : return Shields;
            case "SE" : return Servants;
            default : throw new IllegalArgumentException();
        }
    }
}
