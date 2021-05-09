package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.LeaderCard;
import it.polimi.ingsw.model.Resources;

import java.io.Serializable;
import java.util.ArrayList;

public class LeaderCardPlayedMessage extends Message implements Serializable {
    private ArrayList<Resources> r;
    private ArrayList<Integer> victoryPoints;
    private ArrayList<Integer> extraResources;
    private String nickname;

    public LeaderCardPlayedMessage(ArrayList<LeaderCard> LC, String s) {
        for(LeaderCard LCP : LC){
            r.add(LCP.getAbility().getResType());
            victoryPoints.add(LCP.getVictoryPoints());
            extraResources.add(LCP.getAbility().getStashedResources());
        }
        nickname = s;
    }

    public String getNickname() {
        return nickname;
    }

    public ArrayList<Resources> getR() {
        return r;
    }

    public ArrayList<Integer> getVictoryPoints() {
        return victoryPoints;
    }

    public ArrayList<Integer> getExtraResources() {
        return extraResources;
    }
}
