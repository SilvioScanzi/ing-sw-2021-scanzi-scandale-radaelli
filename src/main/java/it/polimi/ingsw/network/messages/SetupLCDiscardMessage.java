package it.polimi.ingsw.network.messages;

import java.io.Serializable;

public class SetupLCDiscardMessage extends Message implements Serializable {
    private final int[] discardedLC;

    public SetupLCDiscardMessage(int[] discardedLC) {
        this.discardedLC = discardedLC;
    }

    public int[] getDiscardedLC() {
        return discardedLC;
    }
}
