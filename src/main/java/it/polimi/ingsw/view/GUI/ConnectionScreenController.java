package it.polimi.ingsw.view.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


public class ConnectionScreenController {
    @FXML
    private TextField IP;

    @FXML
    private TextField portNr;

    @FXML
    private Button submit;

    @FXML
    public void initialize() {}

    @FXML
    public void submitOnClickHandler() {
        System.out.println("Touched");
    }
}
