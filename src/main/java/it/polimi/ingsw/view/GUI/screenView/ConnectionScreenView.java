package it.polimi.ingsw.view.GUI.screenView;

import it.polimi.ingsw.observers.ViewObservable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;


public class ConnectionScreenView extends ViewObservable{

    @FXML
    private TextField IP;

    @FXML
    private TextField portNr;

    @FXML
    private Button button;

    @FXML
    private Text errormsg;

    /**
     * Method used to disable the confirm button in the initial screen if both the port number and IP are not given
     */
    @FXML
    public void initialize() {
        button.setDisable(true);
        IP.setOnKeyTyped(e -> {
            button.setDisable(portNr.getText().equals("") || IP.getText().equals(""));
        });
        portNr.setOnKeyTyped(e -> {
            button.setDisable(portNr.getText().equals("") || IP.getText().equals(""));
        });
    }

    /**
     *Method used to notify IP and port number when the users clicks the confirm button
     */
    @FXML
    public void submitOnClickHandler() {
        String ipAddress = IP.getText();
        if(ipAddress.equals("")) return;
        int portNumber;
        try{
            portNumber = Integer.parseInt(portNr.getText());
        }catch (NumberFormatException e){return;}
        notifyAddress(ipAddress, portNumber);
    }

    /**
     * method used to notify IP and port number when the users clicks enter on the keyboard
     * @param keyEvent contains what key was pressed on the keyboard
     */
    public void handleKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)) submitOnClickHandler();
    }

    public void setErrormsg(String S){
        errormsg.setText(S);
    }
}
