package it.polimi.ingsw.commons;

import java.io.Serializable;

public enum Marbles implements Serializable {
    Grey,
    Purple,
    Red,
    Blue,
    Yellow,
    White;

    @Override
    public String toString(){
        return switch (this) {
            case Blue -> "Biglia blu";
            case Grey -> "Biglia grigia";
            case Purple -> "Biglia viola";
            case Yellow -> "Biglia gialla";
            case White -> "Biglia bianca";
            case Red -> "Biglia rossa";
        };
    }

    public String abbreviation(){
        return switch (this) {
            case Blue -> "BL";
            case Grey -> "GR";
            case Purple -> "VI";
            case Yellow -> "GI";
            case White -> "BI";
            case Red -> "RO";
        };
    }

    public String getID(){
        return switch (this) {
            case Blue -> "blue";
            case Grey -> "grey";
            case Purple -> "purple";
            case Yellow -> "yellow";
            case White -> "white";
            case Red -> "red";
        };
    }
}
