package it.polimi.ingsw.commons;


public enum ActionToken {
    Advance2,
    AdvanceAndRefresh,
    DeleteGreen,
    DeleteYellow,
    DeletePurple,
    DeleteBlue;

    @Override
    public String toString(){
        return switch (this) {
            case Advance2 -> "La croce nera avanza di due spazi";
            case AdvanceAndRefresh -> "La croce nera avanza di uno spazio e si mescola la pila dei segnalini azione";
            case DeleteBlue -> "Le prime due carte di colore blu vengono eliminate";
            case DeleteGreen -> "Le prime due carte di colore verde vengono eliminate";
            case DeletePurple -> "Le prime due carte di colore viola vengono eliminate";
            case DeleteYellow -> "Le prime due carte di colore giallo vengono eliminate";
        };
    }

    public String abbreviation(){
        return switch (this) {
            case Advance2 -> "A_2";
            case AdvanceAndRefresh -> "A_1";
            case DeleteGreen -> "D_G";
            case DeleteYellow -> "D_Y";
            case DeletePurple -> "D_P";
            case DeleteBlue -> "D_B";
        };
    }
}
