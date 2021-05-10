package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.FaithTrack;

import java.io.Serializable;

public class FaithTrackMessage extends Message implements Serializable {
    private final int FaithMarker;
    private final boolean[] PopeFavor;
    private final String nickname;

    public FaithTrackMessage(FaithTrack ft, String nickname) {
        FaithMarker = ft.getFaithMarker();
        PopeFavor = ft.getPopeFavor();
        this.nickname = nickname;
    }

    public int getFaithMarker() {
        return FaithMarker;
    }

    public boolean[] getPopeFavor() {
        return PopeFavor;
    }

    public String getNickname() {
        return nickname;
    }
}
