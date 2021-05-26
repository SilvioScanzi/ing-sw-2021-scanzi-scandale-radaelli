package it.polimi.ingsw.view.GUI.controllers;

import it.polimi.ingsw.commons.*;
import it.polimi.ingsw.observers.ViewObservable;
import it.polimi.ingsw.view.GUI.GUI;
import it.polimi.ingsw.view.clientModel.ClientBoard;
import javafx.fxml.FXML;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;


public class GameScreenController extends ViewObservable {

    @FXML
    private GridPane ResourceMarket;
    @FXML
    private ImageView rm;
    @FXML
    private GridPane CardMarket;
    @FXML
    private GridPane LeaderCardsPlayed;
    @FXML
    private GridPane FaithTrack;

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

    //TODO: mancano solo gli slot
    @FXML
    private ImageView SLOT_1;
    @FXML
    private ImageView SLOT_2;
    @FXML
    private ImageView SLOT_3;



    @FXML
    public void initialize() {}

    public void addMarbles(Marbles[][] market, Marbles remainingMarble){
        ResourceMarket.getChildren().clear();

        for(int i=0;i<3;i++) {
            for(int j=0;j<4;j++) {
                String path = "/images/marbles/" + market[i][j].getID()+".png";
                ImageView marbleView = new ImageView(new Image(GUI.class.getResource(path).toString()));
                marbleView.setFitHeight(33.0);
                marbleView.setTranslateX(4.0);
                marbleView.setTranslateY(2.0);
                marbleView.setPreserveRatio(true);
                ResourceMarket.add(marbleView,j,i);
            }
        }
        String path = "/images/marbles/" + remainingMarble.getID()+".png";
        rm.setImage(new Image(GUI.class.getResource(path).toString()));
    }

    public void addDevelopment(HashMap<Pair<Colours, Integer>, Integer> DCM){
        CardMarket.getChildren().clear();

        for(Colours C : Colours.values()){
            for(int i=3;i>=1;i--){
                Pair<Colours,Integer> P = new Pair<>(C,i);
                String path = "/images/developmentCards/" + P.getKey().ColourToString() + DCM.get(new Pair<>(P)) +".png";
                ImageView DCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
                DCView.setFitWidth(140.0);
                DCView.setPreserveRatio(true);
                DCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
                CardMarket.add(DCView,P.getKey().ColourToColumn(),3-P.getValue());
            }
        }
    }

    //TODO: all methods need heavy testing
    //TODO: check if the before-all clear() are necessary
    public void addBoard(ClientBoard board){
        addStrongBox(board.getStrongBox());
        addWarehouse(board.getWarehouse());
        addFaithTrack(board.getFaithMarker());
        addLeaderCards(board.getLeaderCardsPlayed());
    }

    private void addStrongBox(HashMap<Resources, Integer> strongBox) {
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


    private void addWarehouse(HashMap<Integer, Pair<Resources, Integer>> warehouse){
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
                        IV.setFitWidth(25.0);
                        IV.setPreserveRatio(true);
                        W1_1.getChildren().add(IV);
                    }
                }
                case 2 -> {
                    path = path + res_num.getKey().getID() + ".png";
                    ImageView IV = new ImageView(new Image(GUI.class.getResource(path).toString()));
                    if(res_num.getValue() >= 1) {
                        IV.setFitWidth(25.0);
                        IV.setPreserveRatio(true);
                        W2_1.getChildren().add(IV);
                    }
                    if(res_num.getValue() == 2){
                        IV.setFitWidth(25.0);
                        IV.setPreserveRatio(true);
                        W2_2.getChildren().add(IV);
                    }
                }
                case 3-> {
                    path = path + res_num.getKey().getID() + ".png";
                    ImageView IV = new ImageView(new Image(GUI.class.getResource(path).toString()));
                    if(res_num.getValue() >= 1){
                        IV.setFitWidth(25.0);
                        IV.setPreserveRatio(true);
                        W3_1.getChildren().add(IV);
                    }
                    if(res_num.getValue() >= 2){
                        IV.setFitWidth(25.0);
                        IV.setPreserveRatio(true);
                        W3_2.getChildren().add(IV);
                    }
                    if(res_num.getValue() == 3){
                        IV.setFitWidth(25.0);
                        IV.setPreserveRatio(true);
                        W3_3.getChildren().add(IV);
                    }
                }
            }
        }
    }

    private void addFaithTrack(int faithMarker){
        FaithTrack.getChildren().clear();
        String path = "/images/FaithMarker.png";
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

    }

    //TODO: mettere un pane con imageView sulle leader card; viene mostrata una risorsa solo se fa parte delle leadercardsPlayed
    private void addLeaderCards(ArrayList<Triplet<Resources,Integer,Integer>> LCPlayed){

        if(LCPlayed.size()==1){
            //add played leaderCards
            String path = "/images/leaderCards/" + LCPlayed.get(0).get_1().abbreviation() + LCPlayed.get(0).get_2() + ".png";
            ImageView LCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
            LCView.setFitWidth(140.0);
            LCView.setPreserveRatio(true);
            LCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
            LeaderCardsPlayed.add(LCView,0,0);

            //add not played leaderCards
            path = "/images/LeaderCardBack.png";
            ImageView backLCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
            backLCView.setFitWidth(140.0);
            backLCView.setPreserveRatio(true);
            backLCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
            LeaderCardsPlayed.add(backLCView,1,0);
        }
        else if(LCPlayed.size()==2){
            //add played leaderCards
            String path = "/images/leaderCards/" + LCPlayed.get(0).get_1().abbreviation() + LCPlayed.get(0).get_2() + ".png";
            ImageView LC1View = new ImageView(new Image(GUI.class.getResource(path).toString()));
            LC1View.setFitWidth(140.0);
            LC1View.setPreserveRatio(true);
            LC1View.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
            LeaderCardsPlayed.add(LC1View,0,0);

            //add not played leaderCards
            path = "/images/leaderCards/" + LCPlayed.get(1).get_1().abbreviation() + LCPlayed.get(1).get_2() + ".png";
            ImageView LC2View = new ImageView(new Image(GUI.class.getResource(path).toString()));
            LC2View.setFitWidth(140.0);
            LC2View.setPreserveRatio(true);
            LC2View.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
            LeaderCardsPlayed.add(LC2View,1,0);
        }
        else {
            //add not played leaderCards
            String path = "/images/LeaderCardBack.png";
            ImageView backLCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
            backLCView.setFitWidth(140.0);
            backLCView.setPreserveRatio(true);
            backLCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
            ImageView back2LCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
            back2LCView.setFitWidth(140.0);
            back2LCView.setPreserveRatio(true);
            back2LCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
            LeaderCardsPlayed.add(backLCView,0,0);
            LeaderCardsPlayed.add(back2LCView,1,0);
        }
    }
}