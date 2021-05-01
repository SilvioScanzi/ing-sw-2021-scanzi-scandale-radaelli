package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.Colours;

import java.io.Serializable;

public class DCMarketMessage extends Message implements Serializable {
    //manda solo la carta che viene scoperta nello stack
    private Colours c;
    private int victoryPoints;
    private String s;

    public DCMarketMessage(Colours col, int vp){
        c = col;
        victoryPoints = vp;
    }
}
