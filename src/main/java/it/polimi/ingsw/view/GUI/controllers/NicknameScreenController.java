package it.polimi.ingsw.view.GUI.controllers;

import it.polimi.ingsw.observers.ViewObservable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


public class NicknameScreenController extends ViewObservable{

    @FXML
    private TextField nicknameID;

    @FXML
    private Button button;

    @FXML
    public void initialize() {
        button.setDisable(true);
        nicknameID.setOnKeyTyped(e -> {
            button.setDisable(nicknameID.getText().equals(""));
        });
    }

    @FXML
    public void submitOnClickHandler() {
        String nickname = nicknameID.getText();
        if(!nickname.equals("")) {notifyNickname(nickname);}
    }


    public void handleKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            submitOnClickHandler();
        }
    }
}