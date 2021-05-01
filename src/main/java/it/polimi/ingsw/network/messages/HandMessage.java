package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.Resources;

import java.io.Serializable;
import java.util.ArrayList;

public class HandMessage extends Message implements Serializable {
    ArrayList<Resources> hand;

    public HandMessage(ArrayList<Resources> hand) {
        this.hand = hand;
    }
}
