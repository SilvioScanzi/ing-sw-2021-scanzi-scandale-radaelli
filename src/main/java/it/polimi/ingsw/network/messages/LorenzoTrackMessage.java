package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.LorenzoTrack;

import java.io.Serializable;

public class LorenzoTrackMessage extends Message implements Serializable {
    private final int blackCross;

    public LorenzoTrackMessage(LorenzoTrack lorenzoTrack) {
        blackCross = lorenzoTrack.getBlackCross();
    }

    public int getBlackCross() {
        return blackCross;
    }
}
