package it.polimi.ingsw.network.messages;

import java.io.Serializable;

public class LCHandUpdateMessage extends Message implements Serializable {
    private String nickname;

    public LCHandUpdateMessage(String s) {
        this.nickname = s;
    }

    public String getNickname() {
        return nickname;
    }
}
