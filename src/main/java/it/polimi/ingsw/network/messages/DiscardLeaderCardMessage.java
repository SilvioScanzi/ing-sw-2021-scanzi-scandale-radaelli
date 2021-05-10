package it.polimi.ingsw.network.messages;

import java.io.Serializable;

public class DiscardLeaderCardMessage extends Message implements Serializable {
    private final int index;

    public DiscardLeaderCardMessage(int index){
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
