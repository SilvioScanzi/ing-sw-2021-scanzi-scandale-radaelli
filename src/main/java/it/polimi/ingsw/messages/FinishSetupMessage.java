package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.Resources;

import java.io.Serializable;
import java.util.ArrayList;

public class FinishSetupMessage extends Message implements Serializable {
    private final ArrayList<Resources> userChoice;

    public FinishSetupMessage(ArrayList<Resources> userChoice) {
        this.userChoice = userChoice;
    }

    public ArrayList<Resources> getUserChoice() {
        return userChoice;
    }
}
