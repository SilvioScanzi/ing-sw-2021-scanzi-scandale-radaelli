package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.commons.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ProductionMessage extends Message implements Serializable {
    private final HashMap<Integer, ArrayList<Pair<String,Integer>>> userChoice;
    //HashMap: integer for board choice (which slot, ...), arraylist for resource and location of resource in warehouse

    public ProductionMessage(HashMap<Integer, ArrayList<Pair<String, Integer>>> userChoice) {
        this.userChoice = userChoice;
    }

    public HashMap<Integer, ArrayList<Pair<String, Integer>>> getUserChoice() {
        return userChoice;
    }
}
