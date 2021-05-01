package it.polimi.ingsw.network.messages;

import java.util.ArrayList;

public class DisconnectedMessage extends Message{
    public String message = "La partità verrà terminata a causa della disconnessione di: ";

    public DisconnectedMessage(ArrayList<String> nicknames){
        for(String s : nicknames){
            message = message + ", " + s;
        }
    }

    public String toString(){
        return message;
    }
}
