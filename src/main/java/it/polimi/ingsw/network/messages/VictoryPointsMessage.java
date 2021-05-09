package it.polimi.ingsw.network.messages;

import java.io.Serializable;

public class VictoryPointsMessage extends Message implements Serializable {
    private int vp;
    private String nickname;

    public VictoryPointsMessage(int vp, String nickname) {
        this.vp = vp;
        this.nickname = nickname;
    }

    public int getVp() {
        return vp;
    }

    public String getNickname() {
        return nickname;
    }
}
