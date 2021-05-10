package it.polimi.ingsw.network.messages;

import java.io.Serializable;

public class DiscardLeaderCardSetupMessage extends Message implements Serializable {
    private final int[] discardedLC;

    public DiscardLeaderCardSetupMessage(int[] discardedLC) {
        this.discardedLC = discardedLC;
    }

    public int[] getDiscardedLC() {
        return discardedLC;
    }
}
