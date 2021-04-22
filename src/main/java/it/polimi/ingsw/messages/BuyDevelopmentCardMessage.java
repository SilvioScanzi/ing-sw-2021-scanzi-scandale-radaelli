package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.Colours;
import it.polimi.ingsw.model.Pair;

import java.io.Serializable;
import java.util.ArrayList;

public class BuyDevelopmentCardMessage extends Message implements Serializable {
    private Colours c;
    private int level;
    private int slotNumber;
    private ArrayList<Pair<String, Integer>> userChoice;

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
