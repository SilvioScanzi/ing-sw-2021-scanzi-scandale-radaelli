package it.polimi.ingsw.exceptions;

//Exception used when trying to access an empty deck of cards
public class EmptyDeckException extends Exception{
    private String deck;
    public EmptyDeckException(String deck) {
        this.deck=deck;
    }
}
