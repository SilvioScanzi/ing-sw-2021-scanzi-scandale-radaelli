package it.polimi.ingsw.view.GUI.screenView;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class WaitScreenView {

    @FXML
    private Text message;

    public void changeMessage(String S){
        message.setText(S);
    }

}