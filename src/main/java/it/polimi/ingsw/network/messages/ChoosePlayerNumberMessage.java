package it.polimi.ingsw.network.messages;

import java.io.Serializable;

public class ChoosePlayerNumberMessage extends Message implements Serializable {
    private final int n;

    public ChoosePlayerNumberMessage(int i){
        n = i;
    }

    public int getN() {
        return n;
    }
}
