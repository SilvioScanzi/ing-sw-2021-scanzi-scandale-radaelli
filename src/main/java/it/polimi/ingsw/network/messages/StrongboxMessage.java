package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.Resources;
import it.polimi.ingsw.model.Strongbox;

import java.io.Serializable;
import java.util.HashMap;

public class StrongboxMessage extends Message implements Serializable {
    private HashMap<Resources,Integer> storage;
    private String nickname;

    public StrongboxMessage(Strongbox sb, String s) {
        this.storage = sb.getStorage();
        nickname = s;
    }

    public HashMap<Resources, Integer> getStorage() {
        return storage;
    }

    public String getNickname() {
        return nickname;
    }
}
