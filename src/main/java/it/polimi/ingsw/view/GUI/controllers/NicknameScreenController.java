package it.polimi.ingsw.view.GUI.controllers;

import it.polimi.ingsw.observers.ViewObservable;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;


public class NicknameScreenController extends ViewObservable {

    @FXML
    private TextField nicknameID;

    @FXML
    public void initialize() {}

    @FXML
    public void submitOnClickHandler() {
        String nickname = nicknameID.getText();
        notifyNickname(nickname);
    }
}