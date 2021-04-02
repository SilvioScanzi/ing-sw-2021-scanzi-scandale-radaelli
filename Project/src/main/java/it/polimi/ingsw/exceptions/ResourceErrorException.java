package it.polimi.ingsw.exceptions;
import it.polimi.ingsw.model.*;

public class ResourceErrorException extends Exception{
    Resources resource;
    int quantity;
    public ResourceErrorException(String message, Resources resource, int quantity) {
        super(message);
        this.resource=resource;
        this.quantity = quantity;
        }

}
