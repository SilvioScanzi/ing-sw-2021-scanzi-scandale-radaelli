package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.commons.Resources;

import java.io.Serializable;
import java.util.ArrayList;

public class ResourceHandMessage extends Message implements Serializable {
    private final ArrayList<Resources> hand;
    private final String nickname;

    public ResourceHandMessage(ArrayList<Resources> hand, String nickname) {
        this.hand = hand;
        this.nickname = nickname;
    }

    public ArrayList<Resources> getHand() {
        return hand;
    }

    public String getNickname() {
        return nickname;
    }
}
