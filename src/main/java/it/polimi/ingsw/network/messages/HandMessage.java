package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.Resources;

import java.io.Serializable;
import java.util.ArrayList;

public class HandMessage extends Message implements Serializable {
    private ArrayList<Resources> hand;
    private String nickname;

    public HandMessage(ArrayList<Resources> hand,String s) {
        this.hand = hand;
        nickname = s;
    }

    public ArrayList<Resources> getHand() {
        return hand;
    }

    public String getNickname() {
        return nickname;
    }
}
