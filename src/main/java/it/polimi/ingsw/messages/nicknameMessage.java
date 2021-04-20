package it.polimi.ingsw.messages;

import java.io.Serializable;

public class nicknameMessage extends Message implements Serializable {
    private final String nickname;
    public nicknameMessage(String nickname){
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
