package it.polimi.ingsw.network.messages;

import java.io.Serializable;

public enum StandardMessages implements Serializable {
    connectionEstablished("Connessione effettuata al server del gioco"),
    welcomeMessage("Benvenuto al gioco \"Maestri del Rinascimento\"!"),
    gameIsStarting("Sei stato inserito in una partita, resta in attesa che si colleghino abbastanza giocatori!"),
    choosePlayerNumber("Scegli il numero dei giocatori"),
    chooseNickName("Scegli il nickname che utilizzerai nel gioco"),
    chooseOneResource("Scegli una risorsa extra da ottenere"),
    chooseTwoResource("Scegli due risorse extra da ottenere"),
    chooseDiscardedLC("Scegli le Leader Card da scartare"),
    yourTurn("É il tuo turno"),
    actionDone("Hai eseguito l'azione per il turno"),
    lorenzoWin("Lorenzo ha vinto!"),
    endGame("La partita è conclusa"),
    fatalError("Un giocatore si è disconnesso prima dell'inizio della partita, la partita è terminata"),
    unavailableConnection("La connessione al server indicato non è disponibile"),

    //Error messages
    gameNotCreated("Resta in attesa che venga creata una partita"),
    wrongObject("Hai inviato dei dati non validi"),
    developmentCardMarketEmpty("Non ci sono development card in questa pila"),
    nicknameAlreadyInUse("Il nickname scelto è già in uso"),
    lobbyNotReady("Aspetta che inizi il gioco"),
    wait("Gli altri giocatori stanno compiendo delle scelte"),
    notYourTurn("Non è ancora il tuo turno"),
    leaderCardOutOfBounds("La carta leader selezionata non esiste"),
    leaderCardWrongFormat("Non hai selezionato il giusto numero di carte leader da scartare"),
    actionAlreadyDone("Hai già fatto un azione per questo turno"),
    indexOutOfBound("Hai scelto un elemento non esistente"),
    whiteMarbleNotCongruent("Hai scelto un numero di biglie bianche da convertire non congruente"),
    emptyDCStack("La carta scelta è già stata acquistata da un altro giocatore"),
    invalidSlot("Lo slot scelto non può accogliere questa carta"),
    invalidChoice("La scelta effettuata non è valida"),
    baseProductionError("La scelta della produzione di base non è valida"),
    emptySlot("Lo slot selezionato è vuoto"),
    resourcesWrong("Le risorse sono state scelte in maniera errata"),
    moveActionNeeded("Devi scegliere dove posizionare le risorse appena ottenute"),
    resourcesLeftInHand("Devi riposizionare le risorse che hai nella mano"),
    incompatibleResources("L'allocazione delle risorse è fallita perchè sono presenti risorse non compatibili"),
    leaderCardWrongAbility("La carta leader scelta non ha un'abilità compatibile con l'azione svolta"),
    requirementsNotMet("Non possiedi i requisiti necessari per compiere questa azione"),
    notEnoughResources("Non possiedi abbastanza risorse per compiere questa azione"),
    actionNotDone("Non hai ancora compiuto un'azione per questo turno"),
    disconnectedMessage("Il giocatore si è disconnesso");
    //...

    private final String message;
    StandardMessages(String message){
        this.message=message;
    }

    @Override
    public String toString() {
        return message;
    }
}
