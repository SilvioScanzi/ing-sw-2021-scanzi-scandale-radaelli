package it.polimi.ingsw.network.messages;

import java.io.Serializable;
import java.util.ArrayList;

public class BuyResourcesMessage extends Message implements Serializable {
    private final boolean row;
    private final int n;
    private final ArrayList<Integer> requestedWMConversion;

    public BuyResourcesMessage(boolean row, int n, ArrayList<Integer> requestedWMConversion) {
        this.row = row;
        this.n = n;
        this.requestedWMConversion = requestedWMConversion;
    }

    public ArrayList<Integer> getRequestedWMConversion() {
        return requestedWMConversion;
    }

    public boolean getRow() {
        return row;
    }

    public int getN() {
        return n;
    }
}
