package it.polimi.ingsw.messages;

public class PlayLeaderCardMessage extends Message {
    private final int n;

    public PlayLeaderCardMessage(int i){
        n = i;
    }

    public int getN() {
        return n;
    }
}
