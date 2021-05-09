package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.LorenzoTrack;

import java.io.Serializable;

public class LorenzoTrackMessage extends Message implements Serializable {
    private LorenzoTrack lorenzoTrack;

    public LorenzoTrackMessage(LorenzoTrack lorenzoTrack) {
        this.lorenzoTrack = lorenzoTrack;
    }

    public LorenzoTrack getLorenzoTrack() {
        return lorenzoTrack;
    }
}
