package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.commons.Resources;
import it.polimi.ingsw.model.Strongbox;

import java.io.Serializable;
import java.util.HashMap;

public class StrongboxMessage extends Message implements Serializable {
    private final HashMap<Resources,Integer> storage;
    private final String nickname;

    public StrongboxMessage(Strongbox sb, String nickname) {
        this.storage = sb.getStorage();
        this.nickname = nickname;
    }

    public HashMap<Resources, Integer> getStorage() {
        return storage;
    }

    public String getNickname() {
        return nickname;
    }
}
