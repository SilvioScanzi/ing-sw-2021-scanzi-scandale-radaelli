package it.polimi.ingsw.network.messages;

import java.io.Serializable;
import java.util.HashMap;

public class NicknameMapMessage extends Message implements Serializable {
    private final String myNickname;
    private final HashMap<Integer,String> playerMap;
    private final int inkwell;

    public NicknameMapMessage(String myNickname, HashMap<Integer,String> playerMap, int inkwell){
        this.myNickname = myNickname;
        this.playerMap = playerMap;
        this.inkwell = inkwell;
    }

    public String getMyNickname() {
        return myNickname;
    }

    public HashMap<Integer, String> getPlayerMap() {
        return playerMap;
    }

    public int getInkwell() {
        return inkwell;
    }
}
