package it.polimi.ingsw.network.messages;

import java.io.Serializable;

public class PlayLeaderCardMessage extends Message implements Serializable {
    private final int n;

    public PlayLeaderCardMessage(int i){
        n = i;
    }

    public int getN() {
        return n;
    }
}
