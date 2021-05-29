package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.commons.Triplet;

import java.io.Serializable;
import java.util.ArrayList;

public class MoveResourcesMessage extends Message implements Serializable {
    private final ArrayList<Triplet<String,Integer,Integer>> userChoice;
    private String errorMsg;

    public MoveResourcesMessage(ArrayList<Triplet<String, Integer, Integer>> userChoice) {
        this.userChoice = userChoice;
    }

    public ArrayList<Triplet<String, Integer, Integer>> getUserChoice() {
        return userChoice;
    }
}
