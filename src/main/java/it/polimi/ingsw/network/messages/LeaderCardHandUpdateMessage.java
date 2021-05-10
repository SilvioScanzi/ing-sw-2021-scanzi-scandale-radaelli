package it.polimi.ingsw.network.messages;

import java.io.Serializable;

public class LeaderCardHandUpdateMessage extends Message implements Serializable {
    private final String nickname;

    public LeaderCardHandUpdateMessage(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
