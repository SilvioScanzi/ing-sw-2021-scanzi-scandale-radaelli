package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.observers.ViewObservable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


public class ConnectionScreenController extends ViewObservable {

    @FXML
    private TextField IP;

    @FXML
    private TextField portNr;

    @FXML
    public void initialize() {}

    @FXML
    public void submitOnClickHandler() {
        String ipAddress = IP.getText();
        int portNumber = 0;
        try{
            portNumber = Integer.parseInt(portNr.getText());
        }catch (NumberFormatException e){return;}
        notifyAddress(ipAddress,portNumber);
    }
}
