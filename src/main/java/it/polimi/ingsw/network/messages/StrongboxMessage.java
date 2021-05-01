package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.Resources;
import it.polimi.ingsw.model.Strongbox;

import java.io.Serializable;
import java.util.HashMap;

public class StrongboxMessage extends Message implements Serializable {
    private HashMap<Resources,Integer> storage;

    public StrongboxMessage(Strongbox sb) {
        this.storage = sb.getStorage();
    }
}
