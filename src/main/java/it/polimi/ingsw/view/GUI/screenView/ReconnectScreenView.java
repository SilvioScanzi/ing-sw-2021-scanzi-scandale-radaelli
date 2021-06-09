package it.polimi.ingsw.view.GUI.screenView;

import it.polimi.ingsw.observers.ViewObservable;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


public class ReconnectScreenView extends ViewObservable{

    public void submitOnClickHandlerConfirm() {
        notifyReconnection(true);
    }

    public void submitOnClickHandlerCancel() {
        notifyReconnection(false);
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER) || keyEvent.getCode().equals(KeyCode.Y) || keyEvent.getCode().equals(KeyCode.S)) submitOnClickHandlerConfirm();
        if (keyEvent.getCode().equals(KeyCode.CANCEL) || keyEvent.getCode().equals(KeyCode.BACK_SPACE) || keyEvent.getCode().equals(KeyCode.N)) submitOnClickHandlerCancel();
    }
}
