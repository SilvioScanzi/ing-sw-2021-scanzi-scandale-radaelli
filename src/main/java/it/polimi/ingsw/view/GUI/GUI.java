package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.commons.*;
import it.polimi.ingsw.network.client.NetworkHandler;
import it.polimi.ingsw.network.messages.StandardMessages;
import it.polimi.ingsw.view.GUI.screenView.*;
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
    private ConnectionScreenView connectionScreenView;
    private ReconnectScreenView reconnectScreenView;
    private NicknameScreenView nicknameScreenView;
    private PlayerNumberScreenView playerNumberScreenView;
    private SetupScreenView ssetupScreenView;
    private WaitScreenView waitScreenView;
    private GameScreenView gameScreenView = null;

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
            connectionScreenView = fxmlLoader.getController();
            connectionScreenView.addObserver(NH);
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
                    nicknameScreenView = fxmlLoader.getController();
                    nicknameScreenView.addObserver(NH);
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
                    playerNumberScreenView = fxmlLoader.getController();
                    playerNumberScreenView.addObserver(NH);
                }catch(IOException e){e.printStackTrace();}
            });
        }
        else if(state.equals(ViewState.reconnecting)){
            Platform.runLater(() -> {
                fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/ReconnectScreen.fxml"));
                try {
                    Pane root = fxmlLoader.load();
                    currentScene.setRoot(root);
                    reconnectScreenView = fxmlLoader.getController();
                    reconnectScreenView.addObserver(NH);
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
                    waitScreenView = fxmlLoader.getController();
                    waitScreenView.changeMessage("Resta in attesa che venga creata una partita");
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
                    waitScreenView = fxmlLoader.getController();
                    waitScreenView.changeMessage("Sei stato inserito in una partita, resta in attesa che si colleghino abbastanza giocatori!");
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
                    waitScreenView = fxmlLoader.getController();
                    waitScreenView.changeMessage("Gli altri giocatori stanno compiendo delle scelte, resta in attesa");
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
                    ssetupScreenView = fxmlLoader.getController();
                    ssetupScreenView.addObserver(NH);
                    ssetupScreenView.addMarbles(NH.getClientModel().getResourceMarket(),NH.getClientModel().getRemainingMarble());
                    ssetupScreenView.addDevelopment(NH.getClientModel().getCardMarket());
                    ssetupScreenView.addLeader(NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()).getLeaderCardsHand());
                    ssetupScreenView.setMessage("Scegli le due carte Leader da scartare");
                    currentScene.setOnKeyPressed(e -> ssetupScreenView.handleKeyPressed(e));
                    primaryStage.setResizable(true);
                    primaryStage.setMaximized(true);
                    scale(1600,900);
                }catch(IOException e){e.printStackTrace();}
            });
        }
        else if(state.equals(ViewState.finishSetupOneResource) || state.equals(ViewState.finishSetupTwoResources)){
            Platform.runLater(() -> {
                currentScene.setRoot(gameScreen);
                if (state.equals(ViewState.finishSetupOneResource)) ssetupScreenView.addResources(1);
                if (state.equals(ViewState.finishSetupTwoResources)) ssetupScreenView.addResources(2);
                String message = "Scegli " + ((state.equals(ViewState.finishSetupOneResource)) ? "una" : "la prima") + " risorsa da ottenere";
                ssetupScreenView.setMessage(message);
                primaryStage.setResizable(true);
                primaryStage.setMaximized(true);
                scale(1600, 900);
            });
        }
        else if(state.equals(ViewState.myTurn) || state.equals(ViewState.notMyTurn)){
            Platform.runLater(() -> {
                try {
                    if(gameScreenView == null) {
                        fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("/fxml/GameScreen.fxml"));
                        primaryStage.setTitle("Maestri del rinascimento - In gioco");
                        gameScreen = fxmlLoader.load();
                        currentScene.setOnKeyPressed(null);
                        gameScreenView = fxmlLoader.getController();
                        gameScreenView.addObserver(NH);
                        gameScreenView.addLCMap(NH.getClientModel().getLCMap(), NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()).getLeaderCardsPlayed());
                        gameScreenView.addMarbles(NH.getClientModel().getResourceMarket(), NH.getClientModel().getRemainingMarble());
                        gameScreenView.addDevelopment(NH.getClientModel().getCardMarket());
                        gameScreenView.addBoard(NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()));
                    }

                    currentScene.setRoot(gameScreen);

                    if(state.equals(ViewState.myTurn)) gameScreenView.setActionDone(false);
                    gameScreenView.grayOut(state.equals(ViewState.notMyTurn));
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
        System.out.println(string);
    }

    @Override
    public void printBoard(ClientBoard board) {

    }

    @Override
    public void printStandardMessage(StandardMessages message) {
        switch(message){
            case nicknameAlreadyInUse -> Platform.runLater(() -> nicknameScreenView.setErrormsg("Il nickname scelto è già in uso"));
            case unavailableConnection -> Platform.runLater(() -> connectionScreenView.setErrormsg("La connessione al server di gioco scelto non è disponibile"));
            case actionDone -> Platform.runLater(() -> gameScreenView.setActionDone(true));
            case incompatibleResources -> Platform.runLater(() -> {
                gameScreenView.addBoard(NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()));
                gameScreenView.grayWarehouse(false,"move");
                gameScreenView.grayHand(false);
            });
            case waitForReconnection -> Platform.runLater(() -> {
                fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/WaitScreen.fxml"));
                try {
                    Pane root = fxmlLoader.load();
                    currentScene.setRoot(root);
                    waitScreenView = fxmlLoader.getController();
                    waitScreenView.changeMessage("Resta in attesa che si riconnettano tutti gli altri giocatori");
                    primaryStage.setResizable(true);
                    primaryStage.setMaximized(true);
                    scale(1600,900);
                }catch(IOException e){e.printStackTrace();}
            });
        }
    }

    @Override
    public void printNames(HashMap<String, Integer> names, int inkwell) {

    }

    @Override
    public void printResourceMarket(Marbles[][] a, Marbles b) {
        if(gameScreenView != null && !state.equals(ViewState.reconnecting)) {
            Platform.runLater(() -> gameScreenView.addMarbles(a, b));
        }
    }

    @Override
    public void printLeaderCardHand(ArrayList<Triplet<Resources, Integer, Integer>> LC) {
        if(gameScreenView !=null && !state.equals(ViewState.reconnecting)) {
            Platform.runLater(() -> gameScreenView.addLeaderCards(NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()).getLeaderCardsPlayed(), LC));
        }
    }

    @Override
    public void printLeaderCardPlayed(ArrayList<Triplet<Resources, Integer, Integer>> LC, String nickname) {
        if(gameScreenView !=null && !state.equals(ViewState.reconnecting)) {
            Platform.runLater(() -> {if((NH.getClientModel().getMyNickname().equals(nickname))) gameScreenView.addLeaderCards(LC, NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()).getLeaderCardsHand());});
        }
    }

    @Override
    public void printResourceHand(ArrayList<Resources> H, String nickname) {
        if(NH.getClientModel().getMyNickname().equals(nickname) && gameScreenView !=null && !state.equals(ViewState.reconnecting)) Platform.runLater(() -> gameScreenView.addHand(H));
    }

    @Override
    public void printAT(ActionToken AT) {

    }

    @Override
    public void printBlackCross(int BC) {

    }

    @Override
    public void printCardMarket(HashMap<Pair<Colours, Integer>, Pair<Integer,Integer>> CM) {
        if(gameScreenView !=null&& !state.equals(ViewState.reconnecting)) Platform.runLater(() -> gameScreenView.addDevelopment(CM));
    }

    @Override
    public void printFaithTrack(int FM, boolean[] PF, String nickname) {
        if(NH.getClientModel().getMyNickname().equals(nickname) && gameScreenView !=null && !state.equals(ViewState.reconnecting)) {
            Platform.runLater(() -> gameScreenView.addFaithTrack(FM, PF));
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
