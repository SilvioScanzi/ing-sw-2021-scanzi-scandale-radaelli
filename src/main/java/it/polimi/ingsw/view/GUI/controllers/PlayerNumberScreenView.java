package it.polimi.ingsw.view.GUI.controllers;

import it.polimi.ingsw.observers.ViewObservable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


public class PlayerNumberScreenView extends ViewObservable {

    @FXML
    private ComboBox<Integer> choice;

    @FXML
    private Button button;

    @FXML
    public void initialize() {
        choice.getItems().addAll(1,2,3,4);
        button.setDisable(true);
        choice.setOnAction(e -> {
            button.setDisable(false);
        });
    }

    @FXML
    public void submitOnClickHandler() {
        choice.setEditable(false);
        int playerNumber = choice.getValue();
        notifyPlayerNumber(playerNumber);
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            submitOnClickHandler();
        }
        else if(keyEvent.getCode().equals(KeyCode.DIGIT1) || keyEvent.getCode().equals(KeyCode.NUMPAD1)){
            choice.setEditable(false);
            notifyPlayerNumber(1);
        }
        else if(keyEvent.getCode().equals(KeyCode.DIGIT2) || keyEvent.getCode().equals(KeyCode.NUMPAD2)){
            choice.setEditable(false);
            notifyPlayerNumber(2);
        }
        else if(keyEvent.getCode().equals(KeyCode.DIGIT3) || keyEvent.getCode().equals(KeyCode.NUMPAD3)){
            choice.setEditable(false);
            notifyPlayerNumber(3);
        }
        else if(keyEvent.getCode().equals(KeyCode.DIGIT4) || keyEvent.getCode().equals(KeyCode.NUMPAD4)){
            choice.setEditable(false);
            notifyPlayerNumber(4);
        }
    }
}