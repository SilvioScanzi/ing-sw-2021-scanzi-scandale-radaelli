package it.polimi.ingsw.exceptions;
import it.polimi.ingsw.model.*;

public class IncompatibleResourceException extends Exception{
    Resources requested;
    Resources got;
    public IncompatibleResourceException(String message, Resources rr, Resources rg){
        super(message);
        requested=rr;
        got=rg;
    }
}
