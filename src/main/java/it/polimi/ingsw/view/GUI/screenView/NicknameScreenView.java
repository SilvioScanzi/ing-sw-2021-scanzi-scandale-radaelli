package it.polimi.ingsw.view.GUI.screenView;

import it.polimi.ingsw.observers.ViewObservable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;


public class NicknameScreenView extends ViewObservable{

    @FXML
    private TextField nickname;

    @FXML
    private Button button;

    @FXML
    private Text errormsg;

    @FXML
    public void initialize() {
        button.setDisable(true);
        nickname.setOnKeyTyped(e -> {
            button.setDisable(nickname.getText().equals(""));
        });
    }

    @FXML
    public void submitOnClickHandler() {
        String nick = nickname.getText();
        if(!nick.equals("")) {notifyNickname(nick);}
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            submitOnClickHandler();
        }
    }

    public void setErrormsg(String S){
        errormsg.setText(S);
    }
}