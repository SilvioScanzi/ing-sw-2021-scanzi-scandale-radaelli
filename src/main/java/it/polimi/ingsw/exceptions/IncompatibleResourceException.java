package it.polimi.ingsw.exceptions;
import it.polimi.ingsw.model.*;

public class IncompatibleResourceException extends Exception{
    public IncompatibleResourceException(String message){
        super(message);
    }
}
