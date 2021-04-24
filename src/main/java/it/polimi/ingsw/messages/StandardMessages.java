package it.polimi.ingsw.messages;

import java.io.Serializable;

public enum StandardMessages implements Serializable {
    welcomeMessage("Benvenuto al gioco \"Maestri del Rinascimento\"!"),
    choosePlayerNumber("Scegli il numero dei giocatori"),
    chooseNickName("Scegli il nickname che utilizzerai nel gioco"),
    chooseOneResource("Scegli una risorsa extra da ottenere"),
    chooseTwoResource("Scegli due risorse extra da ottenere"),
    yourTurn("É il tuo turno"),
    //Error messages
    wrongObject("Hai inviato dei dati non validi"),
    nicknameAlreadyInUse("Il nickname scelto è già in uso"),
    lobbyNotReady("Aspetta che inizi il gioco"),
    waitALittleMore("Aspetta un attimo..."),
    notYourTurn("Non è ancora il tuo turno"),
    leaderCardOutOfBounds("La carta leader selezionata non esiste"),
    leaderCardWrongFormat("Non hai selezionato il giusto numero di carte leader da scartare"),
    actionAlreadyDone("Hai già fatto un azione per questo turno"),
    IndexOutOfBound("Hai scelto un elemento non esistente"),
    whiteMarbleNotCongruent("Hai scelto un numero di biglie bianche da convertire non congruente"),
    emptyDCStack("La carta scelta è già stata acquistata da un altro giocatore"),
    invalidSlot("Lo slot scelto non può accogliere questa carta"),
    invalidChoice("La scelta effettuata sulla locazione della risorsa non è valida");
    //...

    private final String message;
    StandardMessages(String message){
        this.message=message;
    }


}
