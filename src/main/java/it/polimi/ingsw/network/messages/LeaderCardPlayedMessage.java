package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.LeaderCard;
import it.polimi.ingsw.model.Resources;

import java.io.Serializable;

public class LeaderCardPlayedMessage extends Message implements Serializable {
    private Resources r;
    private int victoryPoints;
    private int extraResources;

    public LeaderCardPlayedMessage(LeaderCard LC) {
        this.r = LC.getAbility().getResType();
        this.victoryPoints = LC.getVictoryPoints();
        this.extraResources = LC.getAbility().getStashedResources();
    }
}
