package it.polimi.ingsw.view.GUI.controllers;

import it.polimi.ingsw.observers.ViewObservable;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class WaitScreenView {

    @FXML
    private Text message;

    @FXML
    public void initialize() {}

    public void changeMessage(String S){
        message.setText(S);
    }

}