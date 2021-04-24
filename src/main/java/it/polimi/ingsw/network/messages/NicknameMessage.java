package it.polimi.ingsw.network.messages;

import java.io.Serializable;

public class NicknameMessage extends Message implements Serializable {
    private final String nickname;

    public NicknameMessage(String nickname){
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
