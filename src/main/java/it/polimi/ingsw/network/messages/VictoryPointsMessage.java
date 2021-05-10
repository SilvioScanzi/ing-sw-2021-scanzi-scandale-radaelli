package it.polimi.ingsw.network.messages;

import java.io.Serializable;

public class VictoryPointsMessage extends Message implements Serializable {
    private final String nickname;
    private final int victoryPoints;

    public VictoryPointsMessage(int victoryPoints, String nickname){
        this.nickname = nickname;
        this.victoryPoints = victoryPoints;
    }

    public String getNickname() {
        return nickname;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }
}
