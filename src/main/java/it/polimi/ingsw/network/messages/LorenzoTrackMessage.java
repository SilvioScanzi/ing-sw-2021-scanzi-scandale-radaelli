package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.LorenzoTrack;

import java.io.Serializable;

public class LorenzoTrackMessage extends Message implements Serializable {
    private final int blackCross;
    private boolean[] popeFavor;

    public LorenzoTrackMessage(LorenzoTrack lorenzoTrack, boolean[] popeFavor) {
        blackCross = lorenzoTrack.getBlackCross();
        this.popeFavor = popeFavor;
    }

    public int getBlackCross() {
        return blackCross;
    }

    public boolean[] getPopeFavor() {
        return popeFavor;
    }
}
