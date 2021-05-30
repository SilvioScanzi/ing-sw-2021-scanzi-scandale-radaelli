package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.LeaderCard;
import it.polimi.ingsw.commons.Resources;
import it.polimi.ingsw.commons.Triplet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class LeaderCardPlayedMessage extends Message implements Serializable {
    private final ArrayList<Triplet<Resources,Integer,Integer>> LC;
    private final String nickname;

    public LeaderCardPlayedMessage(HashMap<Integer,LeaderCard> leaderCards, String nickname) {
        LC = new ArrayList<>();
        for(Integer I : leaderCards.keySet()){
            LeaderCard leadCard = leaderCards.get(I);
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
