package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.commons.Marbles;
import it.polimi.ingsw.commons.Resources;

import java.io.Serializable;
import java.util.ArrayList;

public class MarketHandMessage extends Message implements Serializable {
    private final ArrayList<Resources> hand;
    private final String nickname;
    private final Marbles[][] grid;
    private final Marbles remainingMarble;

    public MarketHandMessage(ArrayList<Resources> hand, String nickname, Marbles[][] grid, Marbles remainingMarble) {
        this.hand = hand;
        this.nickname = nickname;
        this.grid = grid;
        this.remainingMarble = remainingMarble;
    }

    public ArrayList<Resources> getHand() {
        return hand;
    }

    public String getNickname() {
        return nickname;
    }

    public Marbles[][] getGrid() {
        return grid;
    }

    public Marbles getRemainingMarble() {
        return remainingMarble;
    }
}
