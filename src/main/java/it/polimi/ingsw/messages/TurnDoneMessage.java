package it.polimi.ingsw.messages;

public class TurnDoneMessage extends Message {
    private boolean done;

    public TurnDoneMessage(boolean done){
        this.done = done;
    }

    public boolean getDone() {
        return done;
    }
}
