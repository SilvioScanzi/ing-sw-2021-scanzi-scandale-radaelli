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
        switch(this){
            case Advance2: return "La croce nera avanza di due spazi";
            case AdvanceAndRefresh: return "La croce nera avanza di uno spazio e si mescola la pila dei segnalini azione";
            case DeleteBlue: return "Le prime due carte di colore blu vengono eliminate";
            case DeleteGreen: return "Le prime due carte di colore verde vengono eliminate";
            case DeletePurple: return "Le prime due carte di colore viola vengono eliminate";
            case DeleteYellow: return "Le prime due carte di colore giallo vengono eliminate";
            default: return "Error";
        }
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
