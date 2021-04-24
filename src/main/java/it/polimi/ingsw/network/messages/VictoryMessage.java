package it.polimi.ingsw.network.messages;

import java.io.Serializable;

public class VictoryMessage extends Message implements Serializable {
    private final String string;

    public VictoryMessage(String nickname,int victoryPoints){
        string = "Il vincitore è " + nickname + " con " + victoryPoints + " punti vittoria";
    }

    public String getString() {
        return string;
    }
}
