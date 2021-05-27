package it.polimi.ingsw.commons;

public enum Colours {
    Purple,
    Yellow,
    Green,
    Blue;

    @Override
    public String toString(){
        return switch (this) {
            case Yellow -> "Giallo";
            case Purple -> "Viola";
            case Blue -> "Blu";
            case Green -> "Verde";
        };
    }

    public static Colours getColourFromString(String string) throws IllegalArgumentException {
        return switch (string) {
            case "BL" -> Blue;
            case "VE" -> Green;
            case "GI" -> Yellow;
            case "VI" -> Purple;
            default -> throw new IllegalStateException("Unexpected value: " + string);
        };
    }

    public static Colours getColourFromColumn(String s){
        return switch (s){
            case "0" -> Green;
            case "1" -> Blue;
            case "2" -> Yellow;
            case "3" -> Purple;
            default -> throw new IllegalStateException("Unexpected value: " + s);
        };
    }

    public String ColourToString(){
        return switch(this){
            case Yellow -> "Y";
            case Purple -> "P";
            case Blue -> "B";
            case Green -> "G";
        };
    }

    public int ColourToColumn(){
        return switch(this){
            case Yellow -> 2;
            case Purple -> 3;
            case Blue -> 1;
            case Green -> 0;
        };
    }
}
