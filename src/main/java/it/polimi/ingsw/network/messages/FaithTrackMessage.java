package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.FaithTrack;

import java.io.Serializable;

public class FaithTrackMessage extends Message implements Serializable {
    private int FaithMarker;
    private boolean[] PopeFavor;
    private String nickname;

    public FaithTrackMessage(FaithTrack ft,String s) {
        FaithMarker = ft.getFaithMarker();
        PopeFavor = ft.getPopeFavor();
        nickname = s;
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
