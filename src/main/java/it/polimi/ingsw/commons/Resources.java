package it.polimi.ingsw.commons;

public enum Resources{
    Coins,
    Stones,
    Servants,
    Shields;

    @Override
    public String toString(){
        return switch (this) {
            case Coins -> "Monete";
            case Stones -> "Pietre";
            case Shields -> "Scudi";
            case Servants -> "Servitori";
        };
    }

    public String abbreviation(){
        return switch (this) {
            case Coins -> "MO";
            case Stones -> "PI";
            case Shields -> "SC";
            case Servants -> "SE";
        };
    }

    public static Resources getResourceFromString(String string) throws IllegalArgumentException {
        return switch (string) {
            case "MO" -> Coins;
            case "PI" -> Stones;
            case "SC" -> Shields;
            case "SE" -> Servants;
            default -> throw new IllegalArgumentException();
        };
    }

    public String getID(){
        return switch (this) {
            case Coins -> "CO";
            case Stones -> "ST";
            case Shields -> "SH";
            case Servants -> "SE";
        };
    }
}
