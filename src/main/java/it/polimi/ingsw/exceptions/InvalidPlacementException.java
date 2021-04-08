package it.polimi.ingsw.exceptions;

public class InvalidPlacementException extends Exception{
    int position;

    public InvalidPlacementException(String message, int position) {
        super(message);
        this.position=position;
    }
}
