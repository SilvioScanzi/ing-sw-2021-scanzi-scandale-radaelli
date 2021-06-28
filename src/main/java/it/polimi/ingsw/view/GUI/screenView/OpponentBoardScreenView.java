package it.polimi.ingsw.view.GUI.screenView;


import it.polimi.ingsw.commons.*;
import it.polimi.ingsw.observers.ViewObservable;

import it.polimi.ingsw.view.GUI.GUI;
import it.polimi.ingsw.view.ViewState;
import it.polimi.ingsw.view.clientModel.ClientBoard;
import javafx.application.Platform;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class OpponentBoardScreenView extends ViewObservable{

    @FXML
    private GridPane FaithTrack;
    @FXML
    private ImageView PF1;
    @FXML
    private ImageView PF2;
    @FXML
    private ImageView PF3;

    @FXML
    private Text SB_CO;
    @FXML
    private Text SB_SE;
    @FXML
    private Text SB_ST;
    @FXML
    private Text SB_SH;

    @FXML
    private Pane W1_1;
    @FXML
    private Pane W2_1;
    @FXML
    private Pane W2_2;
    @FXML
    private Pane W3_1;
    @FXML
    private Pane W3_2;
    @FXML
    private Pane W3_3;

    @FXML
    private GridPane Slots;

    @FXML
    private GridPane LC;

    @FXML
    private Text nickname;

    private Pane pane;
    private Scene scene;
    private String nick;

    public String getNick() {
        return nick;
    }

    public void addScreen(Pane gameScreen, Scene currentScene){
        pane = gameScreen;
        scene = currentScene;
    }

    public void handleGoBack(){
        Platform.runLater(() -> {
                scene.setRoot(pane);
        });
    }

    public void addBoard(ClientBoard board){
        addStrongBox(board.getStrongBox());
        addWarehouse(board.getWarehouse());
        addFaithTrack(board.getFaithMarker(), board.getPopeFavor());
        addLeaderCards(board.getLeaderCardsPlayed(),board.getLeaderCardsHand());
        addSlots(board.getSlot_1(),board.getSlot_2(),board.getSlot_3());
        nickname.setText(board.getNickname());
        nick = board.getNickname();
    }

    public void addSlots(ArrayList<Pair<Colours,Integer>> Slot_1, ArrayList<Pair<Colours,Integer>> Slot_2, ArrayList<Pair<Colours,Integer>> Slot_3){
        for(Node n : Slots.getChildren()){
            ((StackPane)n).getChildren().clear();
        }
        int j = 0;
        int b = 30;
        for (Pair<Colours, Integer> P : Slot_1) {
            String path = "/images/developmentCards/" + P.getKey().ColourToString() + P.getValue() + ".png";
            StackPane SP = null;
            for (Node n : Slots.getChildren()) {
                if (GridPane.getColumnIndex(n) == 0) {
                    SP = (StackPane) n;
                }
            }
            ImageView DCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
            DCView.setFitWidth(80.0);
            DCView.setPreserveRatio(true);
            DCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
            DCView.setTranslateY(b -(25.0)*j);
            if(j == Slot_1.size()-1){
                DCView.setId("P_S_1");
            }
            SP.getChildren().add(DCView);
            j++;
        }
        j = 0;
        b = 30;
        for (Pair<Colours, Integer> P : Slot_2) {
            String path = "/images/developmentCards/" + P.getKey().ColourToString() + P.getValue() + ".png";
            StackPane SP = null;
            for (Node n : Slots.getChildren()) {
                if (GridPane.getColumnIndex(n) == 1) {
                    SP = (StackPane) n;
                }
            }
            ImageView DCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
            DCView.setFitWidth(80.0);
            DCView.setPreserveRatio(true);
            DCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
            DCView.setTranslateY(b -(25.0)*j);
            SP.getChildren().add(DCView);
            if(j == Slot_2.size()-1){
                DCView.setId("P_S_2");
            }
            j++;
        }
        j = 0;
        b = 30;
        for (Pair<Colours, Integer> P : Slot_3) {
            String path = "/images/developmentCards/" + P.getKey().ColourToString() + P.getValue() + ".png";
            StackPane SP = null;
            for (Node n : Slots.getChildren()) {
                if (GridPane.getColumnIndex(n) == 2) {
                    SP = (StackPane) n;
                }
            }
            ImageView DCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
            DCView.setFitWidth(80.0);
            DCView.setPreserveRatio(true);
            DCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
            DCView.setTranslateY(b -(25.0)*j);
            SP.getChildren().add(DCView);
            if(j == Slot_3.size()-1){
                DCView.setId("P_S_3");
            }
            j++;
        }
    }

    public void addStrongBox(HashMap<Resources, Integer> strongBox) {
        for (Resources R : Resources.values()) {
            int n = 0;
            if (strongBox.containsKey(R)) {
                n = strongBox.get(R);
            }
            switch (R) {
                case Coins -> SB_CO.setText("" + n);
                case Stones -> SB_ST.setText("" + n);
                case Shields -> SB_SH.setText("" + n);
                case Servants -> SB_SE.setText("" + n);
            }
        }
    }

    public void addWarehouse(HashMap<Integer, Pair<Resources, Integer>> warehouse){
        W1_1.getChildren().clear();
        W2_1.getChildren().clear();
        W2_2.getChildren().clear();
        W3_1.getChildren().clear();
        W3_2.getChildren().clear();
        W3_3.getChildren().clear();

        for(Integer I : warehouse.keySet()){
            Pair<Resources,Integer> res_num = warehouse.get(I);
            String path = "/images/resources/";
            switch(I){
                case 1 -> {
                    if(res_num.getValue() == 1) {
                        path = path + res_num.getKey().getID() + ".png";
                        ImageView IV = new ImageView(new Image(GUI.class.getResource(path).toString()));
                        IV.setId(res_num.getKey().getID() + "_W11");
                        IV.setFitWidth(25.0);
                        IV.setPreserveRatio(true);
                        W1_1.getChildren().add(IV);
                    }
                }
                case 2 -> {
                    path = path + res_num.getKey().getID() + ".png";
                    if(res_num.getValue() >= 1) {
                        ImageView IV1 = new ImageView(new Image(GUI.class.getResource(path).toString()));
                        IV1.setId(res_num.getKey().getID() + "_W21");
                        IV1.setFitWidth(25.0);
                        IV1.setPreserveRatio(true);
                        W2_1.getChildren().add(IV1);
                    }
                    if(res_num.getValue() == 2){
                        ImageView IV2 = new ImageView(new Image(GUI.class.getResource(path).toString()));
                        IV2.setId(res_num.getKey().getID()  + "_W22");
                        IV2.setFitWidth(25.0);
                        IV2.setPreserveRatio(true);
                        W2_2.getChildren().add(IV2);
                    }
                }
                case 3-> {
                    path = path + res_num.getKey().getID() + ".png";
                    if(res_num.getValue() >= 1){
                        ImageView IV1 = new ImageView(new Image(GUI.class.getResource(path).toString()));
                        IV1.setId(res_num.getKey().getID() + "_W31");
                        IV1.setFitWidth(25.0);
                        IV1.setPreserveRatio(true);
                        W3_1.getChildren().add(IV1);
                    }
                    if(res_num.getValue() >= 2){
                        ImageView IV2 = new ImageView(new Image(GUI.class.getResource(path).toString()));
                        IV2.setId(res_num.getKey().getID() + "_W32");
                        IV2.setFitWidth(25.0);
                        IV2.setPreserveRatio(true);
                        W3_2.getChildren().add(IV2);
                    }
                    if(res_num.getValue() == 3){
                        ImageView IV3 = new ImageView(new Image(GUI.class.getResource(path).toString()));
                        IV3.setId(res_num.getKey().getID()  + "_W33");
                        IV3.setFitWidth(25.0);
                        IV3.setPreserveRatio(true);
                        W3_3.getChildren().add(IV3);
                    }
                }
            }
        }
    }

    public void addFaithTrack(int faithMarker, boolean[] popeFavor){
        FaithTrack.getChildren().clear();
        String path = "/images/faithTrack/FaithMarker.png";
        ImageView FMImage = new ImageView(new Image(GUI.class.getResource(path).toString()));
        FMImage.setPreserveRatio(true);
        FMImage.setFitWidth(29);

        if(faithMarker<3)
            FaithTrack.add(FMImage,faithMarker,2);
        else if(faithMarker<5)
            FaithTrack.add(FMImage,2,4-faithMarker);
        else if(faithMarker<10)
            FaithTrack.add(FMImage,faithMarker-2,0);
        else if(faithMarker<12)
            FaithTrack.add(FMImage,7,faithMarker-9);
        else if(faithMarker<17)
            FaithTrack.add(FMImage,faithMarker-4,2);
        else if(faithMarker<19)
            FaithTrack.add(FMImage,12,18-faithMarker);
        else if(faithMarker<25)
            FaithTrack.add(FMImage,faithMarker-6,0);


        path = "/images/faithTrack/1";
        if(popeFavor[0]) path = path + "_F.png";
        else path = path + "_B.png";
        PF1.setImage(new Image(GUI.class.getResource(path).toString()));
        path = "/images/faithTrack/2";
        if(popeFavor[1]) path = path + "_F.png";
        else path = path + "_B.png";
        PF2.setImage(new Image(GUI.class.getResource(path).toString()));
        path = "/images/faithTrack/3";
        if(popeFavor[2]) path = path + "_F.png";
        else path = path + "_B.png";
        PF3.setImage(new Image(GUI.class.getResource(path).toString()));
    }

    public void addLeaderCards(ArrayList<Triplet<Resources,Integer,Integer>> LCPlayed, ArrayList<Triplet<Resources,Integer,Integer>> LCHand){
        int i = 0;
        for(Triplet<Resources,Integer,Integer> T : LCPlayed){
            String path = "/images/leaderCards/" + T.get_1().getID() + T.get_2() + ".png";
            ImageView LCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
            LCView.setFitWidth(210.0);
            LCView.setPreserveRatio(true);
            LCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
            LC.add(LCView,0,i);

            if (T.get_2() == 3) {
                AnchorPane AP = new AnchorPane();
                LC.add(AP, 0, i);
                int j;
                for (j = 0; j < T.get_3(); j++) {
                    Pane P = new Pane();
                    P.setPrefHeight(34);
                    P.setPrefWidth(34);
                    AP.getChildren().add(P);
                    if (j == 0) P.setLayoutX(33);
                    else P.setLayoutX(112);
                    P.setLayoutY(265);
                    path = "/images/resources/" + T.get_1().getID() + ".png";
                    ImageView R = new ImageView(new Image(GUI.class.getResource(path).toString()));
                    R.setFitWidth(74);
                    R.setPreserveRatio(true);
                    P.getChildren().add(R);
                }
            }

            i++;
        }
        for(Triplet<Resources,Integer,Integer> T : LCHand){
            String path = "/images/LeaderCardBack.png";
            ImageView backLCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
            backLCView.setFitWidth(210.0);
            backLCView.setPreserveRatio(true);
            backLCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
            LC.add(backLCView,0,i);
            i++;
        }
    }
}
