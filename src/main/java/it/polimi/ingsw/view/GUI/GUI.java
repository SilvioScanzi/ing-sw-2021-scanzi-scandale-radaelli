package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.commons.*;
import it.polimi.ingsw.network.client.NetworkHandler;
import it.polimi.ingsw.network.messages.StandardMessages;
import it.polimi.ingsw.view.GUI.controllers.*;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.ViewState;
import it.polimi.ingsw.view.clientModel.ClientBoard;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GUI extends Application implements View{

    private ViewState state = ViewState.start;
    private Stage primaryStage;
    private Scene currentScene;
    private FXMLLoader fxmlLoader;
    private ConnectionScreenController connectionScreenController;
    private NicknameScreenController nicknameScreenController;
    private PlayerNumberController playerNumberController;
    private SetupScreenController setupScreenController;
    private WaitScreenController waitScreenController;
    private GameScreenController gameScreenController;
    private NetworkHandler NH;

    public static void main(String[] args){
        Application.launch();
    }

    @Override
    public void start(Stage stage) {
        Font.loadFont(getClass().getResourceAsStream("/fonts/EnchantedLand.otf"), 28);

        NH = new NetworkHandler(this);

        primaryStage = stage;

        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/ConnectionScreen.fxml"));

        primaryStage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });

        try {
            Pane root = fxmlLoader.load();
            currentScene = new Scene(root);
            primaryStage.setTitle("Maestri del rinascimento - Launcher");
            connectionScreenController = fxmlLoader.getController();
            connectionScreenController.addObserver(NH);
            primaryStage.setScene(currentScene);
            primaryStage.show();
            primaryStage.setResizable(false);
        }catch(IOException e){e.printStackTrace();}
    }

    //TODO: scene da fare
    @Override
    public void setState(ViewState state) {
        if(state.equals(ViewState.chooseNickName)){
            Platform.runLater(() -> {
                fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/NicknameScreen.fxml"));
                try {
                    currentScene = new Scene(fxmlLoader.load());
                    nicknameScreenController = fxmlLoader.getController();
                    nicknameScreenController.addObserver(NH);
                    primaryStage.setScene(currentScene);
                }catch(IOException e){e.printStackTrace();}
            });
        }
        else if(state.equals(ViewState.choosePlayerNumber)){
            Platform.runLater(() -> {
                fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/PlayerNumberScreen.fxml"));
                try {
                    currentScene = new Scene(fxmlLoader.load());
                    playerNumberController = fxmlLoader.getController();
                    playerNumberController.addObserver(NH);
                    playerNumberController.addNumbers();
                    primaryStage.setScene(currentScene);
                }catch(IOException e){e.printStackTrace();}
            });
        }
        else if(state.equals(ViewState.gameNotCreated)){
            Platform.runLater(() -> {
                fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/WaitScreen.fxml"));
                try {
                    currentScene = new Scene(fxmlLoader.load());
                    waitScreenController = fxmlLoader.getController();
                    waitScreenController.changeMessage("Resta in attesa che venga creata una partita");
                    primaryStage.setScene(currentScene);
                }catch(IOException e){e.printStackTrace();}
            });
        }
        else if(state.equals(ViewState.lobbyNotReady)){
            Platform.runLater(() -> {
                fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/WaitScreen.fxml"));
                try {
                    currentScene = new Scene(fxmlLoader.load());
                    waitScreenController = fxmlLoader.getController();
                    waitScreenController.changeMessage("Sei stato inserito in una partita, resta in attesa che si colleghino abbastanza giocatori!");
                    primaryStage.setScene(currentScene);
                }catch(IOException e){e.printStackTrace();}
            });
        }
        else if(state.equals(ViewState.wait)){
            Platform.runLater(() -> {
                fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/WaitScreen.fxml"));
                try {
                    currentScene = new Scene(fxmlLoader.load());
                    waitScreenController = fxmlLoader.getController();
                    waitScreenController.changeMessage("Gli altri giocatori stanno compiendo delle scelte, resta in attesa");
                    primaryStage.setScene(currentScene);
                }catch(IOException e){e.printStackTrace();}
            });
        }
        else if(state.equals(ViewState.discardLeaderCard)){
            Platform.runLater(() -> {
                fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/SetupScreen.fxml"));
                try {
                    currentScene = new Scene(fxmlLoader.load());
                    setupScreenController = fxmlLoader.getController();
                    setupScreenController.initialize();
                    setupScreenController.addObserver(NH);
                    setupScreenController.addMarbles(NH.getClientModel().getResourceMarket());
                    setupScreenController.addDevelopment(NH.getClientModel().getCardMarket());
                    setupScreenController.addLeader(NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()).getLeaderCardsHand());
                    primaryStage.setScene(currentScene);
                    primaryStage.setMaximized(true);
                    scale();

                }catch(IOException e){e.printStackTrace();}
            });
        }
        else if(state.equals(ViewState.finishSetupOneResource) || state.equals(ViewState.finishSetupTwoResources)){
            Platform.runLater(() -> {
                fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/SetupScreen.fxml"));
                try {
                    currentScene = new Scene(fxmlLoader.load());
                    setupScreenController = fxmlLoader.getController();
                    setupScreenController.initialize();
                    setupScreenController.addMarbles(NH.getClientModel().getResourceMarket());
                    setupScreenController.addDevelopment(NH.getClientModel().getCardMarket());
                    if(state.equals(ViewState.finishSetupOneResource)) setupScreenController.addResources(1);
                    if(state.equals(ViewState.finishSetupTwoResources)) setupScreenController.addResources(2);
                    primaryStage.setScene(currentScene);
                    primaryStage.setMaximized(true);
                    scale();
                }catch(IOException e){e.printStackTrace();}
            });
        }
        else if(state.equals(ViewState.myTurn)){
            Platform.runLater(() -> {
                fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/GameScreen.fxml"));
                try {
                    currentScene = new Scene(fxmlLoader.load());
                    gameScreenController = fxmlLoader.getController();
                    gameScreenController.addMarbles(NH.getClientModel().getResourceMarket());
                    gameScreenController.addDevelopment(NH.getClientModel().getCardMarket());
                    //gameScreenController.addBoard(NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()));
                    primaryStage.setScene(currentScene);
                    primaryStage.setMaximized(true);
                    scale();
                }catch(IOException e){e.printStackTrace();}
            });
        }


        this.state = state;
    }

    private void scale(){
        double scaleFactor = primaryStage.getHeight()/900;
        Scale scale = new Scale(scaleFactor, scaleFactor);
        scale.setPivotX(0);
        scale.setPivotY(0);
        currentScene.getRoot().getTransforms().setAll(scale);
        var image = new Image(getClass().getResource("/images/Parchment.jpg").toString(),true);
        var background = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                new BackgroundSize(primaryStage.getWidth(), primaryStage.getHeight(), false, false, false, false)
        );
        ((Pane) currentScene.getRoot()).setBackground(new Background(background));
    }

    @Override
    public void printDisconnected(String name) {

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
