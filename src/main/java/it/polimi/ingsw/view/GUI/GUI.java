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
import javafx.scene.input.KeyCode;
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
    private Pane gameScreen;
    private FXMLLoader fxmlLoader;
    private ConnectionScreenView connectionScreenController;
    private NicknameScreenView nicknameScreenController;
    private PlayerNumberScreenView playerNumberScreenController;
    private SetupScreenView setupScreenController;
    private WaitScreenView waitScreenController;
    private GameScreenView gameScreenController = null;

    private NetworkHandler NH;

    public void begin(){
        Application.launch();
    }

    @Override
    public void start(Stage stage) {
        Font.loadFont(getClass().getResourceAsStream("/fonts/Enchanted_Land.otf"), 28);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Medici_Text.ttf"), 28);

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

            currentScene.widthProperty().addListener((obs, oldVal, newVal) -> {
                scale(1600,900);
            });

            currentScene.heightProperty().addListener((obs, oldVal, newVal) -> {
                scale(1600,900);
            });
            currentScene.setOnKeyReleased(e -> {if(e.getCode().equals(KeyCode.F) && primaryStage.isResizable()) primaryStage.setFullScreen(true);});
        }catch(IOException e){e.printStackTrace();}
    }

    //TODO: scene da finire
    @Override
    public void setState(ViewState state) {
        if(state.equals(ViewState.chooseNickName)){
            Platform.runLater(() -> {
                fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/NicknameScreen.fxml"));
                try {
                    Pane root = fxmlLoader.load();
                    currentScene.setRoot(root);
                    nicknameScreenController = fxmlLoader.getController();
                    nicknameScreenController.addObserver(NH);
                }catch(IOException e){e.printStackTrace();}
            });
        }
        else if(state.equals(ViewState.choosePlayerNumber)){
            Platform.runLater(() -> {
                fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/PlayerNumberScreen.fxml"));
                try {
                    Pane root = fxmlLoader.load();
                    currentScene.setRoot(root);
                    playerNumberScreenController = fxmlLoader.getController();
                    playerNumberScreenController.addObserver(NH);
                }catch(IOException e){e.printStackTrace();}
            });
        }
        else if(state.equals(ViewState.gameNotCreated)){
            Platform.runLater(() -> {
                fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/WaitScreen.fxml"));
                try {
                    Pane root = fxmlLoader.load();
                    currentScene.setRoot(root);
                    waitScreenController = fxmlLoader.getController();
                    waitScreenController.changeMessage("Resta in attesa che venga creata una partita");
                    primaryStage.setResizable(true);
                    primaryStage.setMaximized(true);
                    scale(1600,900);
                }catch(IOException e){e.printStackTrace();}
            });
        }
        else if(state.equals(ViewState.lobbyNotReady)){
            Platform.runLater(() -> {
                fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/WaitScreen.fxml"));
                try {
                    Pane root = fxmlLoader.load();
                    currentScene.setRoot(root);
                    waitScreenController = fxmlLoader.getController();
                    waitScreenController.changeMessage("Sei stato inserito in una partita, resta in attesa che si colleghino abbastanza giocatori!");
                    primaryStage.setResizable(true);
                    primaryStage.setMaximized(true);
                    scale(1600,900);
                }catch(IOException e){e.printStackTrace();}
            });
        }
        else if(state.equals(ViewState.wait)){
            Platform.runLater(() -> {
                fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/WaitScreen.fxml"));
                try {
                    Pane root = fxmlLoader.load();
                    currentScene.setRoot(root);
                    waitScreenController = fxmlLoader.getController();
                    waitScreenController.changeMessage("Gli altri giocatori stanno compiendo delle scelte, resta in attesa");
                    primaryStage.setResizable(true);
                    primaryStage.setMaximized(true);
                    scale(1600,900);
                }catch(IOException e){e.printStackTrace();}
            });
        }
        else if(state.equals(ViewState.discardLeaderCard)){
            Platform.runLater(() -> {
                primaryStage.setTitle("Maestri del rinascimento - Setup");
                fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/SetupScreen.fxml"));
                try {
                    Pane root = fxmlLoader.load();
                    currentScene.setRoot(root);
                    gameScreen = root;
                    setupScreenController = fxmlLoader.getController();
                    setupScreenController.addObserver(NH);
                    setupScreenController.addMarbles(NH.getClientModel().getResourceMarket(),NH.getClientModel().getRemainingMarble());
                    setupScreenController.addDevelopment(NH.getClientModel().getCardMarket());
                    setupScreenController.addLeader(NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()).getLeaderCardsHand());
                    setupScreenController.setMessage("Scegli le due carte Leader da scartare");
                    currentScene.setOnKeyPressed(e -> setupScreenController.handleKeyPressed(e));
                    primaryStage.setResizable(true);
                    primaryStage.setMaximized(true);
                    scale(1600,900);
                }catch(IOException e){e.printStackTrace();}
            });
        }
        else if(state.equals(ViewState.finishSetupOneResource) || state.equals(ViewState.finishSetupTwoResources)){
            Platform.runLater(() -> {
                currentScene.setRoot(gameScreen);
                if (state.equals(ViewState.finishSetupOneResource)) setupScreenController.addResources(1);
                if (state.equals(ViewState.finishSetupTwoResources)) setupScreenController.addResources(2);
                String message = "Scegli " + ((state.equals(ViewState.finishSetupOneResource)) ? "una" : "la prima") + " risorsa da ottenere";
                setupScreenController.setMessage(message);
                primaryStage.setResizable(true);
                primaryStage.setMaximized(true);
                scale(1600, 900);
            });
        }
        else if(state.equals(ViewState.myTurn) || state.equals(ViewState.notMyTurn)){
            Platform.runLater(() -> {
                try {

                    if(gameScreenController == null) {
                        fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("/fxml/GameScreen.fxml"));
                        primaryStage.setTitle("Maestri del rinascimento - In gioco");
                        gameScreen = fxmlLoader.load();
                        currentScene.setOnKeyPressed(null);
                        gameScreenController = fxmlLoader.getController();
                        gameScreenController.addObserver(NH);
                        gameScreenController.addLCMap(NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()).getLeaderCardsHand());
                        gameScreenController.addMarbles(NH.getClientModel().getResourceMarket(), NH.getClientModel().getRemainingMarble());
                        gameScreenController.addDevelopment(NH.getClientModel().getCardMarket());
                        gameScreenController.addBoard(NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()));
                    }

                    currentScene.setRoot(gameScreen);

                    if(state.equals(ViewState.notMyTurn)) gameScreenController.grayOut(true);
                    else gameScreenController.grayOut(false);

                    primaryStage.setResizable(true);
                    primaryStage.setMaximized(true);
                    scale(1600,900);
                }catch(IOException e){e.printStackTrace();}
            });
        }
        this.state = state;
    }

    private void scale(double width,double height){
        double scaleFactorX = primaryStage.getWidth()/width;
        double scaleFactorY = primaryStage.getHeight()/height;

        Scale scale = new Scale(scaleFactorX, scaleFactorY);
        scale.setPivotX(0);
        scale.setPivotY(0);
        currentScene.getRoot().getTransforms().setAll(scale);
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
        switch(message){
            case nicknameAlreadyInUse -> nicknameScreenController.setErrormsg("Il nickname scelto è già in uso");
            case unavailableConnection -> connectionScreenController.setErrormsg("La connessione al server di gioco scelto non è disponibile");
            case actionDone -> gameScreenController.setActionDone();
            case incompatibleResources -> gameScreenController.addBoard(NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()));
        }
    }

    @Override
    public void printNames(HashMap<String, Integer> names, int inkwell) {

    }

    @Override
    public void printResourceMarket(Marbles[][] a, Marbles b) {
        if(gameScreenController!=null) {
            Platform.runLater(() -> gameScreenController.addMarbles(a, b));
        }
    }

    @Override
    public void printLeaderCardHand(ArrayList<Triplet<Resources, Integer, Integer>> LC) {

    }

    @Override
    public void printLeaderCardPlayed(ArrayList<Triplet<Resources, Integer, Integer>> LC, String nickname) {

    }

    @Override
    public void printResourceHand(ArrayList<Resources> H, String nickname) {
        if(NH.getClientModel().getMyNickname().equals(nickname) && gameScreenController!=null) Platform.runLater(() -> gameScreenController.populateHand(H));
    }

    @Override
    public void printAT(ActionToken AT) {

    }

    @Override
    public void printBlackCross(int BC) {

    }

    @Override
    public void printCardMarket(HashMap<Pair<Colours, Integer>, Integer> CM) {
        if(gameScreenController!=null) Platform.runLater(() -> gameScreenController.addDevelopment(CM));
    }

    @Override
    public void printFaithTrack(int FM, boolean[] PF, String nickname) {
        if(NH.getClientModel().getMyNickname().equals(nickname) && gameScreenController!=null) {
            Platform.runLater(() -> gameScreenController.addFaithTrack(FM, PF));
        }
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
