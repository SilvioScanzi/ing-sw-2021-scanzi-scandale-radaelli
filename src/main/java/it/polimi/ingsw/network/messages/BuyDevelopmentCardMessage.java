package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.commons.Colours;
import it.polimi.ingsw.commons.Pair;

import java.io.Serializable;
import java.util.ArrayList;

public class BuyDevelopmentCardMessage extends Message implements Serializable {
    private final Colours c;
    private final int level;
    private final int slotNumber;
    private final ArrayList<Pair<String, Integer>> userChoice;

    public BuyDevelopmentCardMessage(Colours c, int level, int slotNumber, ArrayList<Pair<String, Integer>> userChoice) {
        this.c = c;
        this.level = level;
        this.slotNumber = slotNumber;
        this.userChoice = userChoice;
    }

    public Colours getC() {
        return c;
    }

    public int getLevel() {
        return level;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public ArrayList<Pair<String, Integer>> getUserChoice() {
        return userChoice;
    }
}
