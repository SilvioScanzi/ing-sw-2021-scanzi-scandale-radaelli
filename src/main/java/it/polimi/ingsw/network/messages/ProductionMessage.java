package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.commons.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ProductionMessage extends Message implements Serializable {
    private final HashMap<Integer, ArrayList<Pair<String,Integer>>> userChoice;

    public ProductionMessage(HashMap<Integer, ArrayList<Pair<String, Integer>>> userChoice) {
        this.userChoice = userChoice;
    }

    public HashMap<Integer, ArrayList<Pair<String, Integer>>> getUserChoice() {
        return userChoice;
    }
}
