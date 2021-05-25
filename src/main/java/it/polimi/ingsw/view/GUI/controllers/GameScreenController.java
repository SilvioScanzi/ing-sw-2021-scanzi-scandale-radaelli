package it.polimi.ingsw.view.GUI.controllers;

import it.polimi.ingsw.commons.*;
import it.polimi.ingsw.observers.ViewObservable;
import it.polimi.ingsw.view.GUI.GUI;
import it.polimi.ingsw.view.clientModel.ClientBoard;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;


public class GameScreenController extends ViewObservable {

    @FXML
    private GridPane ResourceMarket;
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

    public void addMarbles(Marbles[][] market){
        ResourceMarket.getChildren().clear();

        for(int i=0;i<3;i++) {
            for(int j=0;j<4;j++) {
                String path = "/images/marbles/" + market[i][j].getID()+".png";
                ImageView marbleView = new ImageView(new Image(GUI.class.getResource(path).toString()));
                marbleView.setFitHeight(94.0);
                marbleView.setPreserveRatio(true);
                ResourceMarket.add(marbleView,j,i);
            }
        }
    }

    public void addDevelopment(HashMap<Pair<Colours, Integer>, Integer> DCM){
        CardMarket.getChildren().clear();

        for(Colours C : Colours.values()){
            for(int i=3;i>=1;i--){
                Pair<Colours,Integer> P = new Pair<>(C,i);
                String path = "/images/developmentCards/" + P.getKey().ColourToString() + DCM.get(new Pair<>(P)) +".png";
                ImageView DCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
                DCView.setFitWidth(175.0);
                DCView.setPreserveRatio(true);
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

    private void addStrongBox(HashMap<Resources, Integer> strongBox){
        for(Resources R : Resources.values()){
            int n = 0;
            if(strongBox.containsKey(R)){
                n = strongBox.get(R);
                switch(R){
                    case Coins -> SB_CO.setText(""+n);
                    case Stones -> SB_ST.setText(""+n);
                    case Shields -> SB_SH.setText(""+n);
                    case Servants -> SB_SE.setText(""+n);
                }
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
            switch(I){
                case 1 -> {
                    if(res_num.getValue() == 1) {
                        ImageView IV = new ImageView("/images/resources/" + res_num.getKey().abbreviation() + ".png");
                        IV.setFitWidth(25.0);
                        IV.setPreserveRatio(true);
                        W1_1.getChildren().add(IV);
                    }
                }
                case 2 -> {
                    ImageView IV = new ImageView("/images/resources/" + res_num.getKey().abbreviation() + ".png");
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
                    ImageView IV = new ImageView("/images/resources/" + res_num.getKey().abbreviation() + ".png");
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
        ImageView FMImage = new ImageView("/images/FaitMarker.png");

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
            ImageView LCView = new ImageView("/images/leaderCards/" + LCPlayed.get(0).get_1().abbreviation() + LCPlayed.get(0).get_2() + ".png");
            LCView.setFitWidth(120.0);
            LCView.setPreserveRatio(true);
            LeaderCardsPlayed.add(LCView,0,0);

            //add not played leaderCards
            ImageView backLCView = new ImageView("/images/LeaderCardBack.png");
            backLCView.setFitWidth(120.0);
            backLCView.setPreserveRatio(true);
            LeaderCardsPlayed.add(backLCView,1,0);
        }
        else if(LCPlayed.size()==2){
            //add played leaderCards
            ImageView LC1View = new ImageView("/images/leaderCards/" + LCPlayed.get(0).get_1().abbreviation() + LCPlayed.get(0).get_2() + ".png");
            LC1View.setFitWidth(120.0);
            LC1View.setPreserveRatio(true);
            LeaderCardsPlayed.add(LC1View,0,0);

            //add not played leaderCards
            ImageView LC2View = new ImageView("/images/leaderCards/" + LCPlayed.get(1).get_1().abbreviation() + LCPlayed.get(1).get_2() + ".png");
            LC2View.setFitWidth(120.0);
            LC2View.setPreserveRatio(true);
            LeaderCardsPlayed.add(LC2View,1,0);
        }
        else {
            //add not played leaderCards
            ImageView backLCView = new ImageView("/images/LeaderCardBack.png");
            backLCView.setFitWidth(120.0);
            backLCView.setPreserveRatio(true);
            LeaderCardsPlayed.add(backLCView,0,0);
            LeaderCardsPlayed.add(backLCView,1,0);
        }
    }
}