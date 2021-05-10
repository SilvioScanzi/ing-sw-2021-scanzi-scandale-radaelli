package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.LeaderCard;
import it.polimi.ingsw.model.Resources;
import it.polimi.ingsw.model.Triplet;

import java.io.Serializable;
import java.util.ArrayList;

public class LeaderCardPlayedMessage extends Message implements Serializable {
    private final ArrayList<Triplet<Resources,Integer,Integer>> LC;
    private final String nickname;

    public LeaderCardPlayedMessage(ArrayList<LeaderCard> leaderCards, String nickname) {
        LC = new ArrayList<>();
        for(LeaderCard leadCard : leaderCards){
            LC.add(new Triplet<>(leadCard.getAbility().getResType(),
                    leadCard.getVictoryPoints(),leadCard.getAbility().getStashedResources()));
        }
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public ArrayList<Triplet<Resources, Integer, Integer>> getLC() {
        return LC;
    }
}
