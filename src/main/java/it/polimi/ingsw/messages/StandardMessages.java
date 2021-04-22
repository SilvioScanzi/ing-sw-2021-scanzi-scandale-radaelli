package it.polimi.ingsw.messages;

import java.io.Serializable;

public enum StandardMessages implements Serializable {
    welcomeMessage("Benvenuto al gioco \"Maestri del Rinascimento\"!"),
    choosePlayerNumber("Scegli il numero dei giocatori"),
    chooseNickName("Scegli il nickname che utilizzerai nel gioco"),
    wrongObject("Hai inviato dei dati non validi"),
    nicknameAlreadyInUse("Il nickname scelto è già in uso"),
    lobbyNotReady("Aspetta che inizi il gioco"),
    waitALittleMore("Aspetta un attimo..."),
    notYourTurn("Non è ancora il tuo turno");
    //...

    private final String message;
    StandardMessages(String message){
        this.message=message;
    }


}
