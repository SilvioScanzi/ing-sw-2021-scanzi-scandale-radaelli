package it.polimi.ingsw.network.messages;

public class ReconnectMessage {
    private String nickname;

    public ReconnectMessage(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
