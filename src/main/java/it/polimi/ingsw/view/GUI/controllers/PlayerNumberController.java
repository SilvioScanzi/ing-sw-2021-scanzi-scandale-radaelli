package it.polimi.ingsw.view.GUI.controllers;

import it.polimi.ingsw.observers.ViewObservable;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;


public class PlayerNumberController extends ViewObservable {

    @FXML
    private ComboBox<Integer> choice;

    @FXML
    public void initialize() {}

    @FXML
    public void submitOnClickHandler() {
        int playerNumber = choice.getValue();
        notifyPlayerNumber(playerNumber);
    }

    public void addNumbers(){
        choice.getItems().addAll(1,2,3,4);
    }
}