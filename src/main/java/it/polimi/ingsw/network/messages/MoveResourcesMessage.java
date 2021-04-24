package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.Triplet;

import java.io.Serializable;
import java.util.ArrayList;

public class MoveResourcesMessage extends Message implements Serializable {
    private final int player;
    private final ArrayList<Triplet<String,Integer,Integer>> userChoice;

    public MoveResourcesMessage(int player, ArrayList<Triplet<String, Integer, Integer>> userChoice) {
        this.player = player;
        this.userChoice = userChoice;
    }

    public int getPlayer() {
        return player;
    }

    public ArrayList<Triplet<String, Integer, Integer>> getUserChoice() {
        return userChoice;
    }
}
