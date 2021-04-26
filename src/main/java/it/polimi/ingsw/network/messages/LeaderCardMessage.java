package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.LeaderCard;
import it.polimi.ingsw.model.Resources;
import it.polimi.ingsw.model.Triplet;

import java.io.Serializable;
import java.util.ArrayList;

public class LeaderCardMessage extends Message implements Serializable {
    ArrayList<Triplet<Resources,Integer,Integer>> LC;

    public LeaderCardMessage(ArrayList<LeaderCard> leaderCards) {
        LC = new ArrayList<>();
        for(LeaderCard leadCard : leaderCards){
            LC.add(new Triplet<>(leadCard.getAbility().getResType(),
                    leadCard.getVictoryPoints(),leadCard.getAbility().getStashedResources()));
        }
    }

    public ArrayList<Triplet<Resources, Integer, Integer>> getLC() {
        return LC;
    }
}
