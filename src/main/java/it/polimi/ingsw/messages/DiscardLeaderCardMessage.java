package it.polimi.ingsw.messages;

import java.io.Serializable;

public class DiscardLeaderCardMessage extends Message implements Serializable {
    private final int n;

    public DiscardLeaderCardMessage(int i){
        n = i;
    }

    public int getN() {
        return n;
    }
}
