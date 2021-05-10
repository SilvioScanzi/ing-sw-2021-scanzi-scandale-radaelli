package it.polimi.ingsw.network.messages;

import java.io.Serializable;

public class PlayLeaderCardMessage extends Message implements Serializable {
    private final int index;

    public PlayLeaderCardMessage(int index){
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
