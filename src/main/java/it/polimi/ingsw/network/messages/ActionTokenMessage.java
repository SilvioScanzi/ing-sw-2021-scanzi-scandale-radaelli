package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.commons.ActionToken;

import java.io.Serializable;

public class ActionTokenMessage extends Message implements Serializable {
    private final ActionToken AT;

    public ActionTokenMessage(ActionToken AT) {
        this.AT = AT;
    }

    public ActionToken getAT() {
        return AT;
    }
}
