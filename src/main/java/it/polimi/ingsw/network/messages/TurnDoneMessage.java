package it.polimi.ingsw.network.messages;

import java.io.Serializable;

public class TurnDoneMessage extends Message implements Serializable{
    private final boolean done;

    public TurnDoneMessage(boolean done){
        this.done = done;
    }

    public boolean getDone() {
        return done;
    }
}
