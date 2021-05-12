package it.polimi.ingsw.network.messages;

import java.io.Serializable;
import java.util.ArrayList;

public class FinishSetupMessage extends Message implements Serializable {
    private final ArrayList<String> userChoice;

    public FinishSetupMessage(ArrayList<String> userChoice) {
        this.userChoice = userChoice;
    }
    public ArrayList<String> getUserChoice() {
        return userChoice;
    }
}
