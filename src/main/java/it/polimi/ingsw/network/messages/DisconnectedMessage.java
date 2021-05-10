package it.polimi.ingsw.network.messages;

public class DisconnectedMessage extends Message{
    public final String nickname;

    public DisconnectedMessage(String nickname){
            this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
