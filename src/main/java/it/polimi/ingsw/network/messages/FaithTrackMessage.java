package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.FaithTrack;

import java.io.Serializable;

public class FaithTrackMessage extends Message implements Serializable {
    private int FaithMarker;
    private boolean[] PopeFavor;

    public FaithTrackMessage(FaithTrack ft) {
        FaithMarker = ft.getFaithMarker();
        PopeFavor = ft.getPopeFavor();
    }
}
