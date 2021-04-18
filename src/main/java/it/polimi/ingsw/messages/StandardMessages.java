package it.polimi.ingsw.messages;

import java.io.Serializable;

public enum StandardMessages implements Serializable {
    welcomeMessage("Benvenuto al gioco \"Maestri del Rinascimento\"!"),
    winMessage("Hai vinto"),
    loseMessage("Hai perso");
    //...

    private final String message;
    StandardMessages(String message){
        this.message=message;
    }


}
