package it.polimi.ingsw.exceptions;

//Exception used when trying to access an empty deck of cards
public class EmptyException extends Exception{
    public EmptyException(String message) {
        super(message);
    }
}
