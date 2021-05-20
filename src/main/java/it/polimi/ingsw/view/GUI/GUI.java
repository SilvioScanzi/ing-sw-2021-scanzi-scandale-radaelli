package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.commons.*;
import it.polimi.ingsw.network.messages.StandardMessages;
import it.polimi.ingsw.observers.ViewObservable;
import it.polimi.ingsw.view.CLI;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.clientModel.ClientBoard;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GUI extends Application implements View{

    private Stage primaryStage;
    private FXMLLoader fxmlLoader;
    private ConnectionScreenController connectionScreenController;

    @Override
    public void start(Stage stage) {

        primaryStage = stage;

        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/ConnectionScreen.fxml"));

        primaryStage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });

        try {
            Scene scene = new Scene(fxmlLoader.load());
            primaryStage.setTitle("Maestri del rinascimento - Connessione al server");
            connectionScreenController = fxmlLoader.getController();
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        }catch(IOException e){e.printStackTrace();}
    }

    @Override
    public void setState(CLI.ViewState state) {

    }

    @Override
    public void print(String string) {

    }

    @Override
    public void printBoard(ClientBoard board) {

    }

    @Override
    public void printStandardMessage(StandardMessages message) {

    }

    @Override
    public void printNames(HashMap<String, Integer> names, int inkwell) {

    }

    @Override
    public void printResourceMarket(Marbles[][] a, Marbles b) {

    }

    @Override
    public void printLeaderCardHand(ArrayList<Triplet<Resources, Integer, Integer>> LC) {

    }

    @Override
    public void printLeaderCardPlayed(ArrayList<Triplet<Resources, Integer, Integer>> LC, String nickname) {

    }

    @Override
    public void printResourceHand(ArrayList<Resources> H, String nickname) {

    }

    @Override
    public void printAT(ActionToken AT) {

    }

    @Override
    public void printBlackCross(int BC) {

    }

    @Override
    public void printCardMarket(HashMap<Pair<Colours, Integer>, Integer> CM) {

    }

    @Override
    public void printFaithTrack(int FM, boolean[] PF, String nickname) {

    }

    @Override
    public void printSlot(int I, Colours C, int VP, String nickname) {

    }

    @Override
    public void printStrongBox(HashMap<Resources, Integer> SB, String nickname) {

    }

    @Override
    public void printWarehouse(HashMap<Integer, Pair<Resources, Integer>> WH, String nickname) {

    }
}
