package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.commons.*;
import it.polimi.ingsw.network.client.NetworkHandler;
import it.polimi.ingsw.network.messages.StandardMessages;
import it.polimi.ingsw.view.GUI.screenView.*;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.ViewState;
import it.polimi.ingsw.view.clientModel.ClientBoard;
import javafx.animation.PauseTransition;
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
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
    private SetupScreenView setupScreenView;
    private WaitScreenView waitScreenView;
    private GameScreenView gameScreenView = null;
    private LeaderBoardScreenView leaderBoardScreenView;
    private PlayerVersusScreenView playerVersusScreenView;
    private OpponentBoardScreenView opponentBoardScreenView;

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
            primaryStage.setMaximized(false);
            primaryStage.setFullScreen(false);

            currentScene.widthProperty().addListener((obs, oldVal, newVal) -> scale(1600,900));

            currentScene.heightProperty().addListener((obs, oldVal, newVal) -> scale(1600,900));

            currentScene.setOnKeyReleased(e -> {if(e.getCode().equals(KeyCode.F) && primaryStage.isResizable()) primaryStage.setFullScreen(true);});
        }catch(IOException e){e.printStackTrace();}
    }

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
                    primaryStage.setFullScreen(false);
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
                    primaryStage.setResizable(false);
                    primaryStage.setFullScreen(false);
                    if(currentScene.getWidth()!=700.0 || currentScene.getHeight()!=700.0) {
                        primaryStage.setMaximized(false);
                        scale(700.0,700.0);
                    }
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
                    primaryStage.setFullScreen(false);
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
        else if(state.equals(ViewState.finishSetupOneResource) || state.equals(ViewState.finishSetupTwoResources)){
            Platform.runLater(() -> {
                currentScene.setRoot(gameScreen);
                if (state.equals(ViewState.finishSetupOneResource)) setupScreenView.addResources(1);
                if (state.equals(ViewState.finishSetupTwoResources)) setupScreenView.addResources(2);
                String message = "Scegli " + ((state.equals(ViewState.finishSetupOneResource)) ? "una" : "la prima") + " risorsa da ottenere";
                setupScreenView.setMessage(message);
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
                        if(NH.getClientModel().getPlayerNumber()==1){
                            gameScreenView.setLorenzoTrue();
                        }
                        gameScreenView.addLCMap(NH.getClientModel().getLCMap(), NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()).getLeaderCardsPlayed());
                        gameScreenView.addMarbles(NH.getClientModel().getResourceMarket(), NH.getClientModel().getRemainingMarble());
                        gameScreenView.addDevelopment(NH.getClientModel().getCardMarket());
                        gameScreenView.addBoard(NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()));
                        gameScreenView.addPlayers(NH.getClientModel().getBoards(),NH.getClientModel().getMyNickname());
                        gameScreenView.setCharacter(NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()).getPosition());
                    }

                    currentScene.setRoot(gameScreen);
                    gameScreenView.grayOut(true);

                    gameScreenView.setMyTurn(state.equals(ViewState.myTurn));

                    if(state.equals(ViewState.myTurn)) {
                        gameScreenView.grayOut(false);
                        gameScreenView.setActionDone(false);
                        gameScreenView.addMessage("È il tuo turno!",false);
                    }
                    else if(NH.getClientModel().getPlayerNumber() > 1){
                        gameScreenView.grayBoards(false);
                    }

                    primaryStage.setResizable(true);
                    primaryStage.setMaximized(true);
                    scale(1600,900);
                }catch(IOException e){e.printStackTrace();}
            });
        }
        else if(state.equals(ViewState.endGame)){
            Platform.runLater(() -> {
                primaryStage.setFullScreen(false);
                gameScreenView.grayOut(true);
                PauseTransition PT = new PauseTransition();
                PT.setDuration(Duration.seconds(10));
                if(NH.getClientModel().getPlayerNumber()==1){
                    if(NH.getClientModel().getLorenzo()) {
                        gameScreenView.addMessage("Lorenzo il magnifico ha vinto!",true);
                    }
                    else{
                        gameScreenView.addMessage("Hai battuto Lorenzo il magnifico!",true);
                    }
                }
                else{
                    String player = "";
                    int max = 0;
                    for(String s : NH.getClientModel().getLeaderBoard().keySet()){
                        if(NH.getClientModel().getLeaderBoard().get(s)>max) {
                            max = NH.getClientModel().getLeaderBoard().get(s);
                            player = s;
                        }
                    }
                    if(player.equals(NH.getClientModel().getMyNickname())) gameScreenView.addMessage("Hai vinto la partita!",true);
                    else gameScreenView.addMessage(player+" ha vinto la partita!",true);
                }
                PT.setOnFinished(e -> {
                    Platform.runLater(() -> {
                        try {
                            primaryStage.setMaximized(false);
                            primaryStage.setFullScreen(false);

                            fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(getClass().getResource("/fxml/LeaderBoardScreen.fxml"));
                            primaryStage.setTitle("Maestri del rinascimento - Fine partita");

                            Pane root = fxmlLoader.load();
                            currentScene.setRoot(root);
                            currentScene.setOnKeyPressed(null);
                            leaderBoardScreenView = fxmlLoader.getController();
                            leaderBoardScreenView.addObserver(NH);
                            leaderBoardScreenView.setLeaderBoard(NH.getClientModel().getLeaderBoard(), NH.getClientModel().getLorenzo());

                            scale(700.0,700.0);
                            primaryStage.setResizable(false);
                        }catch(IOException x){x.printStackTrace();}
                    });
                });

                PT.play();
            });
        }
        else if(state.equals(ViewState.disconnected)){
            if(NH.getClientModel().getLeaderBoard() == null) {
                NH = new NetworkHandler(this);

                fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/ConnectionScreen.fxml"));

                try {
                    Pane root = fxmlLoader.load();
                    Platform.runLater(() -> {
                        currentScene = new Scene(root);
                        primaryStage.setTitle("Maestri del rinascimento - Launcher");
                        connectionScreenView = fxmlLoader.getController();
                        connectionScreenView.addObserver(NH);
                        primaryStage.setScene(currentScene);
                        primaryStage.show();
                        primaryStage.setResizable(false);
                        primaryStage.setMaximized(false);
                        primaryStage.setFullScreen(false);

                        connectionScreenView.setErrormsg("C'è stato un errore lato Server, riconnettiti e ricomincia a giocare");

                        currentScene.widthProperty().addListener((obs, oldVal, newVal) -> scale(1600, 900));

                        currentScene.heightProperty().addListener((obs, oldVal, newVal) -> scale(1600, 900));

                        currentScene.setOnKeyReleased(x -> {
                            if (x.getCode().equals(KeyCode.F) && primaryStage.isResizable())
                                primaryStage.setFullScreen(true);
                        });
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        this.state = state;
    }

    @Override
    public void clearView() {
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/NicknameScreen.fxml"));
        gameScreenView = null;
        gameScreen = null;
        state = ViewState.chooseNickName;
        try {
            Pane root = fxmlLoader.load();
            currentScene = new Scene(root);
            primaryStage.setTitle("Maestri del rinascimento - Launcher");
            nicknameScreenView = fxmlLoader.getController();
            nicknameScreenView.addObserver(NH);
            primaryStage.setScene(currentScene);
            primaryStage.setResizable(false);

        }catch(IOException e){e.printStackTrace();}
    }

    @Override
    public void demolish() {
        Platform.exit();
        System.exit(0);
    }

    private void scale(double width,double height){
        double scaleFactorX = currentScene.getWidth()/width;
        double scaleFactorY = currentScene.getHeight()/height;

        Scale scale = new Scale(scaleFactorX, scaleFactorY);
        scale.setPivotX(0);
        scale.setPivotY(0);
        currentScene.getRoot().getTransforms().setAll(scale);
    }

    private void goToSetup(){
        if(state.equals(ViewState.discardLeaderCard))
        Platform.runLater(() -> {
            primaryStage.setTitle("Maestri del rinascimento - Setup");
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/SetupScreen.fxml"));
            try {
                Pane root = fxmlLoader.load();
                currentScene.setRoot(root);
                gameScreen = root;
                setupScreenView = fxmlLoader.getController();
                setupScreenView.addObserver(NH);
                setupScreenView.addMarbles(NH.getClientModel().getResourceMarket(),NH.getClientModel().getRemainingMarble());
                setupScreenView.addDevelopment(NH.getClientModel().getCardMarket());
                setupScreenView.addLeader(NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()).getLeaderCardsHand());
                setupScreenView.setMessage("Scegli le due carte Leader da scartare");
                currentScene.setOnKeyPressed(e -> setupScreenView.handleKeyPressed(e));
                primaryStage.setResizable(true);
                primaryStage.setMaximized(true);
                scale(1600,900);
            }catch(IOException e){e.printStackTrace();}
        });
    }

    @Override
    public void printDisconnected(String name) {
        Platform.runLater(() -> {
            currentScene.setRoot(gameScreen);
            gameScreenView.addMessage("Il giocatore " + name + " si è disconnesso", false);
        });
    }

    @Override
    public void printReconnect(String name){
        Platform.runLater(() -> {
            currentScene.setRoot(gameScreen);
            gameScreenView.addMessage("Il giocatore " + name + " si è riconnesso", false);
        });
    }

    @Override
    public void print(String string) {}

    @Override
    public void printBoard(ClientBoard board) {
        Platform.runLater(() -> {
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/OpponentBoardScreen.fxml"));
            try {
                Pane root = fxmlLoader.load();
                currentScene.setRoot(root);
                opponentBoardScreenView = fxmlLoader.getController();
                opponentBoardScreenView.addObserver(NH);
                opponentBoardScreenView.addBoard(board);
                opponentBoardScreenView.addScreen(gameScreen,currentScene);
                primaryStage.setResizable(true);
                primaryStage.setMaximized(true);
                scale(1600,900);
            }catch(IOException e){e.printStackTrace();}
        });
    }

    @Override
    public void printStandardMessage(StandardMessages message) {
        String msg = message.toString();
        if(msg.startsWith("@")){
            msg = msg.substring(2);
        }

        String finalMsg = msg;

        switch(message){
            case nicknameAlreadyInUse -> Platform.runLater(() -> nicknameScreenView.setErrormsg("Il nickname scelto è già in uso"));
            case unavailableConnection -> Platform.runLater(() -> connectionScreenView.setErrormsg("La connessione al server di gioco scelto non è disponibile"));
            case actionDone -> Platform.runLater(() -> gameScreenView.setActionDone(true));
            case moveActionWrong, buyDevelopmentWrong, activateProductionWrong -> Platform.runLater(() -> {
                gameScreenView.addMessage(finalMsg,true);
                gameScreenView.addBoard(NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()));
                gameScreenView.grayOut(false);
                if(NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()).getActionDone()) gameScreenView.grayOutActionDone();
                if(message.equals(StandardMessages.moveActionWrong)){
                    gameScreenView.setIsMoveAction();
                    if(NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()).getHand().size()>0){
                        gameScreenView.setConfirm();
                    }
                }
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
            case resourceBuyDone -> Platform.runLater(() -> {
                gameScreenView.setPrevResBuyAction();
                gameScreenView.setActionDone(true);
            });
            case requirementsNotMet -> Platform.runLater(() ->{
                gameScreenView.addMessage("Non possiedi i requisiti per giocare questa carta leader",true);
                gameScreenView.addLeaderCards(NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()).getLeaderCardsPlayed(),NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()).getLeaderCardsHand());
            });
        }
    }

    @Override
    public void printNames(HashMap<String, Integer> names, int inkwell) {
        if(!state.equals(ViewState.reconnecting) && !state.equals(ViewState.myTurn) && !state.equals(ViewState.notMyTurn)) {
            Platform.runLater(() -> {
                fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/PlayerVersusScreen.fxml"));
                try {
                    Pane root = fxmlLoader.load();
                    currentScene.setRoot(root);
                    playerVersusScreenView = fxmlLoader.getController();
                    playerVersusScreenView.setScreen(names);
                    primaryStage.setResizable(true);
                    primaryStage.setMaximized(true);
                    scale(1600,900);
                    PauseTransition PT = new PauseTransition();
                    PT.setDuration(Duration.seconds(5));
                    PT.setOnFinished(e -> goToSetup());
                    PT.play();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void printResourceMarket(Marbles[][] a, Marbles b) {
        if(gameScreenView != null && !state.equals(ViewState.reconnecting)) {
            Platform.runLater(() -> gameScreenView.addMarbles(a, b));
        }
    }

    @Override
    public void printLeaderCardHand(ArrayList<Triplet<Resources, Integer, Integer>> LC) {}

    @Override
    public void printLeaderCardPlayed(ArrayList<Triplet<Resources, Integer, Integer>> LC, String nickname) {
        if (gameScreenView != null && !state.equals(ViewState.reconnecting)) {
            Platform.runLater(() -> {
                if ((NH.getClientModel().getMyNickname().equals(nickname))) {
                    gameScreenView.addLeaderCards(LC, NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()).getLeaderCardsHand());
                    if(state.equals(ViewState.myTurn)){
                        if(!NH.getClientModel().getBoard(nickname).getActionDone()){
                            gameScreenView.grayPlayedLeaderCard(false,"default");
                        }
                        else gameScreenView.grayPlayedLeaderCard(false,"move");
                    }
                }
                else {
                    gameScreenView.addPlayerBoard(NH.getClientModel().getBoard(nickname));
                    if (currentScene.getRoot().getId()!=null && currentScene.getRoot().getId().equals("opponentboard") && opponentBoardScreenView.getNick().equals(nickname)) {
                        opponentBoardScreenView.addLeaderCards(LC,NH.getClientModel().getBoard(nickname).getLeaderCardsHand());
                    }
                }
            });
        }
    }

    @Override
    public void printResourceHand(ArrayList<Resources> H, String nickname) {
        if(NH.getClientModel().getMyNickname().equals(nickname) && gameScreenView !=null && !state.equals(ViewState.reconnecting)) Platform.runLater(() -> gameScreenView.addHand(H));
    }

    @Override
    public void printAT(ActionToken AT) {
        if(gameScreenView !=null && !state.equals(ViewState.reconnecting)) {
            Platform.runLater(() -> gameScreenView.addActionToken(AT.abbreviation()));
        }
    }

    @Override
    public void printBlackCross(int BC) {
        if(gameScreenView !=null && !state.equals(ViewState.reconnecting)) {
            Platform.runLater(() -> gameScreenView.addFaithTrack(BC, NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()).getPopeFavor(),true));
        }
    }

    @Override
    public void printCardMarket(HashMap<Pair<Colours, Integer>, Pair<Integer,Integer>> CM) {
        if(gameScreenView !=null&& !state.equals(ViewState.reconnecting)) Platform.runLater(() -> gameScreenView.addDevelopment(CM));
    }

    @Override
    public void printFaithTrack(int FM, boolean[] PF, String nickname) {
        if (gameScreenView != null && !state.equals(ViewState.reconnecting)) {
            Platform.runLater(() -> {
                if ((NH.getClientModel().getMyNickname().equals(nickname)))
                    Platform.runLater(() -> gameScreenView.addFaithTrack(FM, PF,false));
                else {
                    gameScreenView.addPlayerBoard(NH.getClientModel().getBoard(nickname));
                    if (currentScene.getRoot().getId()!=null && currentScene.getRoot().getId().equals("opponentboard") && opponentBoardScreenView.getNick().equals(nickname)) {
                        opponentBoardScreenView.addFaithTrack(FM,PF);
                    }
                }
            });
        }
    }

    @Override
    public void printSlot(ArrayList<ArrayList<Pair<Colours, Integer>>> slots, String nickname) {
        if (gameScreenView != null && !state.equals(ViewState.reconnecting)) {
            Platform.runLater(() -> {
                if ((NH.getClientModel().getMyNickname().equals(nickname)))
                    gameScreenView.addSlots(slots.get(0),slots.get(1),slots.get(2));
                else {
                    gameScreenView.addPlayerBoard(NH.getClientModel().getBoard(nickname));
                    if (currentScene.getRoot().getId()!=null && currentScene.getRoot().getId().equals("opponentboard") && opponentBoardScreenView.getNick().equals(nickname)) {
                        opponentBoardScreenView.addSlots(slots.get(0),slots.get(1),slots.get(2));
                    }
                }
            });
        }
    }

    @Override
    public void printStrongBox(HashMap<Resources, Integer> SB, String nickname) {
        if (gameScreenView != null && !state.equals(ViewState.reconnecting)) {
            Platform.runLater(() -> {
                if ((NH.getClientModel().getMyNickname().equals(nickname)))
                    gameScreenView.addStrongBox(SB);
                else {
                    gameScreenView.addPlayerBoard(NH.getClientModel().getBoard(nickname));
                    if (currentScene.getRoot().getId()!=null && currentScene.getRoot().getId().equals("opponentboard") && opponentBoardScreenView.getNick().equals(nickname)) {
                        opponentBoardScreenView.addStrongBox(SB);
                    }
                }
            });
        }
    }

    @Override
    public void printWarehouse(HashMap<Integer, Pair<Resources, Integer>> WH, String nickname) {
        if (gameScreenView != null && !state.equals(ViewState.reconnecting)) {
            Platform.runLater(() -> {
                if ((NH.getClientModel().getMyNickname().equals(nickname))) {
                    gameScreenView.addWarehouse(WH);
                    gameScreenView.grayOut(false);
                    if(NH.getClientModel().getBoard(NH.getClientModel().getMyNickname()).getActionDone()) gameScreenView.grayOutActionDone();
                }
                else {
                    gameScreenView.addPlayerBoard(NH.getClientModel().getBoard(nickname));
                    if (currentScene.getRoot().getId()!=null && currentScene.getRoot().getId().equals("opponentboard") && opponentBoardScreenView.getNick().equals(nickname)) {
                        opponentBoardScreenView.addWarehouse(WH);
                    }
                }
            });
        }
    }
}
