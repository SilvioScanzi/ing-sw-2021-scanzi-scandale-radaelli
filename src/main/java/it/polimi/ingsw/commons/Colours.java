package it.polimi.ingsw.commons;

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

    public static Colours getColourFromString(String string) throws IllegalArgumentException {
        switch(string){
            case "BL" : return Blue;
            case "VE" : return Green;
            case "GI" : return Yellow;
            case "VI" : return Purple;
            default : throw new IllegalArgumentException();
        }
    }
}
