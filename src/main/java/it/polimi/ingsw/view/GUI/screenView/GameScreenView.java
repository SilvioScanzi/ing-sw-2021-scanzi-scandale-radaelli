package it.polimi.ingsw.view.GUI.screenView;

import it.polimi.ingsw.commons.*;
import it.polimi.ingsw.observers.ViewObservable;
import it.polimi.ingsw.view.GUI.GUI;
import it.polimi.ingsw.view.clientModel.ClientBoard;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;

public class GameScreenView extends ViewObservable {

    @FXML
    private GridPane ResourceMarket;
    @FXML
    private ImageView rm;
    @FXML
    private ImageView R_1;
    @FXML
    private ImageView R_2;
    @FXML
    private ImageView R_3;
    @FXML
    private ImageView C_1;
    @FXML
    private ImageView C_2;
    @FXML
    private ImageView C_3;
    @FXML
    private ImageView C_4;

    @FXML
    private GridPane CardMarket;

    @FXML
    private GridPane Hand;

    @FXML
    private GridPane LeaderCardsPlayed;

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
    private ImageView SB_CO_ICON;
    @FXML
    private Text SB_SE;
    @FXML
    private ImageView SB_SE_ICON;
    @FXML
    private Text SB_ST;
    @FXML
    private ImageView SB_ST_ICON;
    @FXML
    private Text SB_SH;
    @FXML
    private ImageView SB_SH_ICON;

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
    private GridPane Slots;
    @FXML
    private ImageView P_B;

    @FXML
    private Button confirmAction;
    @FXML
    private Button endTurn;


    //key is the index, value is: Resource of the card, Victory points and if played or not
    private final HashMap<Integer,Triplet<Resources,Integer,Boolean>> LCMap = new HashMap<>();
    private final boolean[][] grid = new boolean[3][4];
    private final ArrayList<Integer> selectedRMLC = new ArrayList<>();
    private Node selected = null;
    private final ArrayList<Triplet<String,Integer,Integer>> moveAction = new ArrayList<>();
    private final ArrayList<Pair<String, Integer>> buyDevelopmentAction = new ArrayList<>();
    private Pair<Colours,Integer> buyDevelopmentCard;
    private boolean isMoveAction = false;
    private boolean isBuyAction = false;
    private boolean actionDone = false;
    private boolean prevResBuyAction = false;

    @FXML
    public void initialize(){
        endTurn.setDisable(true);
        confirmAction.setDisable(true);
    }

    public void setActionDone(boolean actionDone) {
        this.actionDone = actionDone;
        if(actionDone){
            endTurn.setDisable(false);
            grayOutActionDone();
        }
        else{
            endTurn.setDisable(true);
            prevResBuyAction = false;
            isMoveAction = false;
            selectedRMLC.clear();
            moveAction.clear();
            selected = null;
        }
    }

    //Add methods
    public void addLCMap(HashMap<Integer, Pair<Resources,Integer>> LC, ArrayList<Triplet<Resources,Integer,Integer>> leaderCardsPlayed){
        for(Integer i : LC.keySet()){
            boolean played = false;
            for(Triplet<Resources,Integer,Integer> T : leaderCardsPlayed){
                if(T.get_1().equals(LC.get(i).getKey()) && T.get_2().equals(LC.get(i).getValue())){
                    played = true;
                }
            }
            LCMap.put(i+1,new Triplet<>(LC.get(i).getKey(),LC.get(i).getValue(),played));
        }
    }

    public void addBoard(ClientBoard board){
        addStrongBox(board.getStrongBox());
        addWarehouse(board.getWarehouse());
        addFaithTrack(board.getFaithMarker(), board.getPopeFavor());
        addLeaderCards(board.getLeaderCardsPlayed(),board.getLeaderCardsHand());
        addHand(board.getHand());
        addSlots(board.getSlot_1(),board.getSlot_2(),board.getSlot_3());
    }

    public void addSlots(ArrayList<Pair<Colours,Integer>> Slot_1,ArrayList<Pair<Colours,Integer>> Slot_2,ArrayList<Pair<Colours,Integer>> Slot_3){
        for(Node n : Slots.getChildren()){
            ((StackPane)n).getChildren().clear();
        }
        int j = 0;
        int b = 15;
        for (Pair<Colours, Integer> P : Slot_1) {
            String path = "/images/developmentCards/" + P.getKey().ColourToString() + P.getValue() + ".png";
            StackPane SP = null;
            for (Node n : Slots.getChildren()) {
                if (GridPane.getColumnIndex(n) == 0) {
                    SP = (StackPane) n;
                }
            }
            ImageView DCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
            DCView.setFitWidth(109.0);
            DCView.setPreserveRatio(true);
            DCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
            DCView.setTranslateY(b -(25.0)*j);
            DCView.setTranslateX(-5);
            if(j == Slot_1.size()-1){
                DCView.setId("P_S_1");
            }
            SP.getChildren().add(DCView);
            j++;
        }
        j = 0;
        b = 15;
        for (Pair<Colours, Integer> P : Slot_2) {
            String path = "/images/developmentCards/" + P.getKey().ColourToString() + P.getValue() + ".png";
            StackPane SP = null;
            for (Node n : Slots.getChildren()) {
                if (GridPane.getColumnIndex(n) == 1) {
                    SP = (StackPane) n;
                }
            }
            ImageView DCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
            DCView.setFitWidth(109.0);
            DCView.setPreserveRatio(true);
            DCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
            DCView.setTranslateY(b -(25.0)*j);
            DCView.setTranslateX(-2);
            SP.getChildren().add(DCView);
            if(j == Slot_2.size()-1){
                DCView.setId("P_S_2");
            }
            j++;
        }
        j = 0;
        b = 15;
        for (Pair<Colours, Integer> P : Slot_3) {
            String path = "/images/developmentCards/" + P.getKey().ColourToString() + P.getValue() + ".png";
            StackPane SP = null;
            for (Node n : Slots.getChildren()) {
                if (GridPane.getColumnIndex(n) == 2) {
                    SP = (StackPane) n;
                }
            }
            ImageView DCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
            DCView.setFitWidth(109.0);
            DCView.setPreserveRatio(true);
            DCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
            DCView.setTranslateY(b -(25.0)*j);
            DCView.setTranslateX(5);
            SP.getChildren().add(DCView);
            if(j == Slot_3.size()-1){
                DCView.setId("P_S_3");
            }
            j++;
        }
    }

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
                grid[i][j] = market[i][j].equals(Marbles.White);
            }
        }
        String path = "/images/marbles/" + remainingMarble.getID()+".png";
        rm.setImage(new Image(GUI.class.getResource(path).toString()));
    }

    public void addDevelopment(HashMap<Pair<Colours, Integer>, Pair<Integer,Integer>> DCM){
        for(Node n : CardMarket.getChildren()){
            ((StackPane) n).getChildren().clear();
        }

        for(Colours C : Colours.values()){
            for(int i=3;i>=1;i--){
                Pair<Colours,Integer> P = new Pair<>(C,i);
                String path = "/images/developmentCards/" + P.getKey().ColourToString() + DCM.get(P).getKey() +".png";
                StackPane SP = null;
                for(Node n : CardMarket.getChildren()){
                    if(GridPane.getColumnIndex(n) == P.getKey().ColourToColumn() && GridPane.getRowIndex(n) == 3-P.getValue()){
                        SP = (StackPane) n;
                    }
                }
                for(int j = 0; j < DCM.get(P).getValue()-1; j++){
                    ImageView DCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
                    DCView.setFitWidth(140.0);
                    DCView.setPreserveRatio(true);
                    DCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
                    DCView.setTranslateY((-10.0)*j);
                    SP.getChildren().add(DCView);
                }

                ImageView DCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
                DCView.setFitWidth(140.0);
                DCView.setPreserveRatio(true);
                DCView.setId(P.getKey().ColourToColumn()+"_"+P.getValue().toString());
                DCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
                SP.getChildren().add(DCView);
                DCView.setTranslateY((-10.0)*(DCM.get(P).getValue()-1));
            }
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

    //TODO: mettere un pane con imageView sulle leader card; viene mostrata una risorsa solo se fa parte delle leadercardsPlayed
    public void addLeaderCards(ArrayList<Triplet<Resources,Integer,Integer>> LCPlayed, ArrayList<Triplet<Resources,Integer,Integer>> LCHand){
        for(Triplet<Resources,Integer,Integer> T : LCPlayed){
            String path = "/images/leaderCards/" + T.get_1().getID() + T.get_2() + ".png";
            ImageView LCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
            LCView.setFitWidth(140.0);
            LCView.setPreserveRatio(true);
            LCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));

            for(int i=1;i<3;i++){
                if(LCMap.containsKey(i) && LCMap.get(i).get_1().equals(T.get_1()) && LCMap.get(i).get_2().equals(T.get_2())){
                    LCView.setId("F_L_"+(i));
                    int finalI = i;
                    LeaderCardsPlayed.getChildren().removeIf(n -> GridPane.getColumnIndex(n) == (finalI -1));
                    LeaderCardsPlayed.add(LCView,i-1,0);
                    LCMap.get(i).set_3(true);
                }
            }
        }

        for(Triplet<Resources,Integer,Integer> T : LCHand){
            String path = "/images/LeaderCardBack.png";
            ImageView backLCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
            backLCView.setFitWidth(140.0);
            backLCView.setPreserveRatio(true);
            backLCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
            for(int i=1;i<3;i++){
                if(LCMap.containsKey(i) && LCMap.get(i).get_1().equals(T.get_1()) && LCMap.get(i).get_2().equals(T.get_2())){
                    backLCView.setId("B_L_"+(i));
                    int finalI = i;
                    LeaderCardsPlayed.getChildren().removeIf(n -> GridPane.getColumnIndex(n) == (finalI -1));
                    LeaderCardsPlayed.add(backLCView,i-1,0);
                    LCMap.get(i).set_3(false);
                    backLCView.setOnMouseEntered(e -> {
                        String p = "/images/leaderCards/"+LCMap.get(finalI).get_1().getID()+LCMap.get(finalI).get_2()+".png";
                        backLCView.setImage(new Image(GUI.class.getResource(p).toString()));
                    });
                    backLCView.setOnMouseExited(e ->{
                        String p = "/images/LeaderCardBack.png";
                        backLCView.setImage(new Image(GUI.class.getResource(p).toString()));
                    });
                    grayNotPlayedLeaderCard(false,finalI-1,backLCView);
                }
            }
        }
    }

    public void addHand(ArrayList<Resources> hand){
        Hand.getChildren().clear();
        int i = 0,j = 0;
        for(int size=0;size<hand.size();size++){
            i = size%5;
            if(size>5) {
                j = 1;
            }

            String path = "/images/resources/" + hand.get(size).getID() + ".png";

            ImageView IV = new ImageView(new Image(GUI.class.getResource(path).toString()));
            IV.setId(hand.get(size).getID() + "_W00");
            IV.setFitWidth(25.0);
            IV.setPreserveRatio(true);
            Hand.add(IV,j,i);
        }
        grayHand(false);
    }


    //Gray out methods
    public void grayOut(boolean gray){
        if(gray){
            grayOutActionDone();
            grayWarehouse(true,"");
            grayPlayedLeaderCard(true);
            for(int i = 1; i<=2;i++){
                ImageView IM = null;
                for(Node n : LeaderCardsPlayed.getChildren()){
                    if(GridPane.getColumnIndex(n) == i - 1) IM = (ImageView) n;
                }
                if(LCMap.containsKey(i) && !LCMap.get(i).get_3()) grayNotPlayedLeaderCard(true,i-1,IM);
            }
            grayEmptyWarehouse(true);
            grayHand(true);
            grayStrongBox(true);
        }
        else{
            //Clickable board
            grayWarehouse(false,"move");
            grayCardMarket(false);
            grayResourceMarket(false);
            for(int i = 1; i<=2;i++){
                ImageView IM = null;
                for(Node n : LeaderCardsPlayed.getChildren()){
                    if(GridPane.getColumnIndex(n) == i - 1) IM = (ImageView) n;
                }
                if(LCMap.containsKey(i) && !LCMap.get(i).get_3()) grayNotPlayedLeaderCard(false,i-1,IM);
            }
            grayProduction(false);
            graySlots(false,"production");
        }
    }

    public void grayOutActionDone(){
        graySlots(true,"");
        grayResourceMarket(true);
        grayCardMarket(true);
        grayProduction(true);
    }

    public void grayWarehouse(boolean gray, String type){
        if(gray){
            if (W1_1.getChildren().size() > 0) {
               W1_1.getChildren().get(0).getStyleClass().remove("selectable");
                W1_1.getChildren().get(0).setOnMouseClicked(null);
            }
            if (W2_1.getChildren().size() > 0) {
                W2_1.getChildren().get(0).getStyleClass().remove("selectable");
                W2_1.getChildren().get(0).setOnMouseClicked(null);
            }
            if (W2_2.getChildren().size() > 0) {
                W2_2.getChildren().get(0).getStyleClass().remove("selectable");
                W2_2.getChildren().get(0).setOnMouseClicked(null);
            }
            if (W3_1.getChildren().size() > 0) {
                W3_1.getChildren().get(0).getStyleClass().remove("selectable");
                W3_1.getChildren().get(0).setOnMouseClicked(null);
            }
            if (W3_2.getChildren().size() > 0) {
                W3_2.getChildren().get(0).getStyleClass().remove("selectable");
                W3_2.getChildren().get(0).setOnMouseClicked(null);
            }
            if (W3_3.getChildren().size() > 0) {
                W3_3.getChildren().get(0).getStyleClass().remove("selectable");
                W3_3.getChildren().get(0).setOnMouseClicked(null);
            }
        }
        else if (type.equals("move")){
            if (W1_1.getChildren().size() > 0) {
                if(!W1_1.getChildren().get(0).getStyleClass().contains("selectable")) W1_1.getChildren().get(0).getStyleClass().add("selectable");
                W1_1.getChildren().get(0).setOnMouseClicked(e -> {
                    if (selected != null && selected.getId().split("_")[1].startsWith("W"))
                        handleMoveAction((ImageView) W1_1.getChildren().get(0));
                    else handleSelect((ImageView) W1_1.getChildren().get(0));
                });
            }
            if (W2_1.getChildren().size() > 0) {
                if(!W2_1.getChildren().get(0).getStyleClass().contains("selectable")) W2_1.getChildren().get(0).getStyleClass().add("selectable");
                W2_1.getChildren().get(0).setOnMouseClicked(e -> {
                    if (selected != null && selected.getId().split("_")[1].startsWith("W"))
                        handleMoveAction((ImageView) W2_1.getChildren().get(0));
                    else handleSelect((ImageView) W2_1.getChildren().get(0));
                });
            }
            if (W2_2.getChildren().size() > 0) {
                if(!W2_2.getChildren().get(0).getStyleClass().contains("selectable")) W2_2.getChildren().get(0).getStyleClass().add("selectable");
                W2_2.getChildren().get(0).setOnMouseClicked(e -> {
                    if (selected != null && selected.getId().split("_")[1].startsWith("W"))
                        handleMoveAction((ImageView) W2_2.getChildren().get(0));
                    else handleSelect((ImageView) W2_2.getChildren().get(0));
                });
            }
            if (W3_1.getChildren().size() > 0) {
                if(!W3_1.getChildren().get(0).getStyleClass().contains("selectable")) W3_1.getChildren().get(0).getStyleClass().add("selectable");
                W3_1.getChildren().get(0).setOnMouseClicked(e -> {
                    if (selected != null && selected.getId().split("_")[1].startsWith("W"))
                        handleMoveAction((ImageView) W3_1.getChildren().get(0));
                    else handleSelect((ImageView) W3_1.getChildren().get(0));
                });
            }
            if (W3_2.getChildren().size() > 0) {
                if(!W3_2.getChildren().get(0).getStyleClass().contains("selectable")) W3_2.getChildren().get(0).getStyleClass().add("selectable");
                W3_2.getChildren().get(0).setOnMouseClicked(e -> {
                    if (selected != null && selected.getId().split("_")[1].startsWith("W"))
                        handleMoveAction((ImageView) W3_2.getChildren().get(0));
                    else handleSelect((ImageView) W3_2.getChildren().get(0));
                });
            }
            if (W3_3.getChildren().size() > 0) {
                if(!W3_3.getChildren().get(0).getStyleClass().contains("selectable")) W3_3.getChildren().get(0).getStyleClass().add("selectable");
                W3_3.getChildren().get(0).setOnMouseClicked(e -> {
                    if (selected != null && selected.getId().split("_")[1].startsWith("W"))
                        handleMoveAction((ImageView) W3_3.getChildren().get(0));
                    else handleSelect((ImageView) W3_3.getChildren().get(0));
                });
            }
        }
        else if(type.equals("buy")) {
            if (W1_1.getChildren().size() > 0) {
                if(!W1_1.getChildren().get(0).getStyleClass().contains("selectable")) W1_1.getChildren().get(0).getStyleClass().add("selectable");
                W1_1.getChildren().get(0).setOnMouseClicked(e -> handleBuySelect(W1_1));
            }
            if (W2_1.getChildren().size() > 0) {
                if(!W2_1.getChildren().get(0).getStyleClass().contains("selectable")) W2_1.getChildren().get(0).getStyleClass().add("selectable");
                W2_1.getChildren().get(0).setOnMouseClicked(e -> handleBuySelect(W2_1));
            }
            if (W2_2.getChildren().size() > 0) {
                if(!W2_2.getChildren().get(0).getStyleClass().contains("selectable")) W2_2.getChildren().get(0).getStyleClass().add("selectable");
                W2_2.getChildren().get(0).setOnMouseClicked(e -> handleBuySelect(W2_2));
            }
            if (W3_1.getChildren().size() > 0) {
                if(!W3_1.getChildren().get(0).getStyleClass().contains("selectable")) W3_1.getChildren().get(0).getStyleClass().add("selectable");
                W3_1.getChildren().get(0).setOnMouseClicked(e -> handleBuySelect(W3_1));
            }
            if (W3_2.getChildren().size() > 0) {
                if(!W3_2.getChildren().get(0).getStyleClass().contains("selectable")) W3_2.getChildren().get(0).getStyleClass().add("selectable");
                W3_2.getChildren().get(0).setOnMouseClicked(e -> handleBuySelect(W3_2));
            }
            if (W3_3.getChildren().size() > 0) {
                if(!W3_3.getChildren().get(0).getStyleClass().contains("selectable")) W3_3.getChildren().get(0).getStyleClass().add("selectable");
                W3_3.getChildren().get(0).setOnMouseClicked(e -> handleBuySelect(W3_3));
            }
        }
        else if(type.equals("production")){

        }
    }

    public void grayCardMarket(boolean gray){
        if(gray){
            for(Node n : CardMarket.getChildren()){
                ((StackPane) n).getChildren().get(((StackPane) n).getChildren().size()-1).getStyleClass().remove("selectable");
                ((StackPane) n).getChildren().get(((StackPane) n).getChildren().size()-1).setOnMouseClicked(null);
            }
        }
        else{
            for(Node n : CardMarket.getChildren()){
                if(!((StackPane) n).getChildren().get(((StackPane) n).getChildren().size()-1).getStyleClass().contains("selectable"))
                    ((StackPane) n).getChildren().get(((StackPane) n).getChildren().size()-1).getStyleClass().add("selectable");
                ((StackPane) n).getChildren().get(((StackPane) n).getChildren().size()-1).setOnMouseClicked(e -> {
                    handleSelect((ImageView) ((StackPane) n).getChildren().get(((StackPane) n).getChildren().size()-1));
                });
            }
        }
    }

    public void grayResourceMarket(boolean gray){
        if (gray) {
            R_1.getStyleClass().remove("bigselectable");
            R_2.getStyleClass().remove("bigselectable");
            R_3.getStyleClass().remove("bigselectable");
            C_1.getStyleClass().remove("bigselectable");
            C_2.getStyleClass().remove("bigselectable");
            C_3.getStyleClass().remove("bigselectable");
            C_4.getStyleClass().remove("bigselectable");
            R_1.setOnMouseClicked(null);
            R_2.setOnMouseClicked(null);
            R_3.setOnMouseClicked(null);
            C_1.setOnMouseClicked(null);
            C_2.setOnMouseClicked(null);
            C_3.setOnMouseClicked(null);
            C_4.setOnMouseClicked(null);
        }
        else{
            if(!R_1.getStyleClass().contains("bigselectable")) R_1.getStyleClass().add("bigselectable");
            if(!R_2.getStyleClass().contains("bigselectable")) R_2.getStyleClass().add("bigselectable");
            if(!R_3.getStyleClass().contains("bigselectable")) R_3.getStyleClass().add("bigselectable");
            if(!C_1.getStyleClass().contains("bigselectable")) C_1.getStyleClass().add("bigselectable");
            if(!C_2.getStyleClass().contains("bigselectable")) C_2.getStyleClass().add("bigselectable");
            if(!C_3.getStyleClass().contains("bigselectable")) C_3.getStyleClass().add("bigselectable");
            if(!C_4.getStyleClass().contains("bigselectable")) C_4.getStyleClass().add("bigselectable");
            R_1.setOnMouseClicked(e -> { handleSelect(R_1); });
            R_2.setOnMouseClicked(e -> { handleSelect(R_2); });
            R_3.setOnMouseClicked(e -> { handleSelect(R_3); });
            C_1.setOnMouseClicked(e -> { handleSelect(C_1); });
            C_2.setOnMouseClicked(e -> { handleSelect(C_2); });
            C_3.setOnMouseClicked(e -> { handleSelect(C_3); });
            C_4.setOnMouseClicked(e -> { handleSelect(C_4); });
        }
    }

    public void grayEmptyWarehouse(boolean gray) {
        if (gray) {
            W1_1.setId(null);
            W1_1.setOnMouseClicked(null);

            W2_1.setId(null);
            W2_1.setOnMouseClicked(null);

            W2_2.setId(null);
            W2_2.setOnMouseClicked(null);

            W3_1.setId(null);
            W3_1.setOnMouseClicked(null);

            W3_2.setId(null);
            W3_2.setOnMouseClicked(null);

            W3_3.setId(null);
            W3_3.setOnMouseClicked(null);
        } else {
            if (W1_1.getChildren().size() == 0) {
                W1_1.setId("W1_1Empty");
                W1_1.setOnMouseClicked(e -> {
                    handleMoveAction(W1_1);
                });
            }
            if (W2_1.getChildren().size() == 0) {
                W2_1.setId("W2_1Empty");
                W2_1.setOnMouseClicked(e -> {
                    handleMoveAction(W2_1);
                });
            }
            if (W2_2.getChildren().size() == 0) {
                W2_2.setId("W2_2Empty");
                W2_2.setOnMouseClicked(e -> {
                    handleMoveAction(W2_2);
                });
            }
            if (W3_1.getChildren().size() == 0) {
                W3_1.setId("W3_1Empty");
                W3_1.setOnMouseClicked(e -> {
                    handleMoveAction(W3_1);
                });
            }
            if (W3_2.getChildren().size() == 0) {
                W3_2.setId("W3_2Empty");
                W3_2.setOnMouseClicked(e -> {
                    handleMoveAction(W3_2);
                });
            }
            if (W3_3.getChildren().size() == 0) {
                W3_3.setId("W3_3Empty");
                W3_3.setOnMouseClicked(e -> {
                    handleMoveAction(W3_3);
                });
            }
        }
    }

    public void grayHand(boolean gray){
        if(gray){
            for(Node node : Hand.getChildren()){
                node.getStyleClass().remove("selectable");
                node.setOnMouseClicked(null);
            }
        }
        else{
            for(Node n : Hand.getChildren()){
                if(!n.getStyleClass().contains("selectable")) n.getStyleClass().add("selectable");
                n.setOnMouseClicked(e -> {
                    if(selected!=null && selected.getId().split("_")[1].startsWith("W")) handleMoveAction((ImageView) n);
                    else handleSelect((ImageView) n); });
            }
        }
    }

    public void grayNotPlayedLeaderCard(boolean gray, int finalJ, ImageView backLCView) {
        if (gray) {
            for (Integer i : LCMap.keySet()) {
                if (!LCMap.get(i).get_3()) {
                    for (Node n : LeaderCardsPlayed.getChildren()) {
                        if (GridPane.getColumnIndex(n) == (i - 1)) {
                            n.getStyleClass().remove("selectable");
                            n.setOnMouseClicked(null);
                        }
                    }
                }
            }
        } else {
            for (Integer i : LCMap.keySet()) {
                if (!LCMap.get(i).get_3()) {
                    for (Node n : LeaderCardsPlayed.getChildren()) {
                        if (GridPane.getColumnIndex(n) == (i - 1)) {
                            if(!n.getStyleClass().contains("selectable")) n.getStyleClass().add("selectable");
                            backLCView.setOnMouseClicked(e -> handleLeaderCardClick(backLCView));
                        }
                    }
                }
            }
        }
    }

    public void grayPlayedLeaderCard(boolean gray) {
        if (gray) {
            for (Integer i : LCMap.keySet()) {
                if (LCMap.get(i).get_3()) {
                    for (Node n : LeaderCardsPlayed.getChildren()) {
                        if (GridPane.getColumnIndex(n) == (i - 1)) {
                            n.getStyleClass().remove("selectable");
                            n.setOnMouseClicked(null);
                        }
                    }
                }
            }
        } else {
            for (Integer i : LCMap.keySet()) {
                if (LCMap.get(i).get_3()) {
                    for (Node n : LeaderCardsPlayed.getChildren()) {
                        if (GridPane.getColumnIndex(n) == (i - 1)) {
                            if(!n.getStyleClass().contains("selectable")) n.getStyleClass().add("selectable");
                            //n.setOnMouseClicked(null);
                        }
                    }
                }
            }
        }
    }

    public void grayStrongBox(boolean gray){
        if(gray){
            if(SB_CO_ICON.getStyleClass().contains("selectable")) SB_CO_ICON.getStyleClass().remove("selectable");
            SB_CO_ICON.setOnMouseClicked(null);
            if(SB_SH_ICON.getStyleClass().contains("selectable")) SB_SH_ICON.getStyleClass().remove("selectable");
            SB_SH_ICON.setOnMouseClicked(null);
            if(SB_ST_ICON.getStyleClass().contains("selectable")) SB_ST_ICON.getStyleClass().remove("selectable");
            SB_ST_ICON.setOnMouseClicked(null);
            if(SB_SE_ICON.getStyleClass().contains("selectable")) SB_SE_ICON.getStyleClass().remove("selectable");
            SB_SE_ICON.setOnMouseClicked(null);
        }else{
            SB_CO_ICON.getStyleClass().remove("selectable");
            //SB_CO_ICON.setOnMouseClicked(null);
            SB_SH_ICON.getStyleClass().remove("selectable");
            //SB_SH_ICON.setOnMouseClicked(null);
            SB_ST_ICON.getStyleClass().remove("selectable");
            //SB_ST_ICON.setOnMouseClicked(null);
            SB_SE_ICON.getStyleClass().remove("selectable");
            //SB_SE_ICON.setOnMouseClicked(null);
        }
    }

    public void grayProduction(boolean gray){
        if(gray){
            P_B.getStyleClass().remove("selectable");
            P_B.setOnMouseClicked(null);
        }else{
            if(!P_B.getStyleClass().contains("selectable")) P_B.getStyleClass().add("selectable");
            P_B.setOnMouseClicked(e -> handleSelect(P_B));
        }
    }

    public void graySlots(boolean gray, String type){
        if(gray){
            for(Node n : Slots.getChildren()) {
                for(Node d : ((StackPane) n).getChildren()) {
                    d.getStyleClass().remove("selectable");
                    d.setOnMouseClicked(null);
                }
            }
        }else if(type.equals("production")){
            for(Node n : Slots.getChildren()) {
                for(Node d : ((StackPane) n).getChildren()) {
                    if(!(d.getId()==null) && d.getId().startsWith("P_S_")) {
                        if(!d.getStyleClass().contains("selectable")) d.getStyleClass().add("selectable");
                        d.setOnMouseClicked(e -> handleSelect((ImageView) d));
                    }
                }
            }
        }else if(type.equals("buy")){
            for(Node n : Slots.getChildren()) {
                if(((StackPane)n).getChildren().size()<3) {
                    if (!n.getStyleClass().contains("selectable")) n.getStyleClass().add("selectable");
                    n.setOnMouseClicked(e -> handleSelect(n));
                }
            }
        }
    }


    //Handle Methods
    public void handleConfirmClick(){
        if(isBuyAction && (selected.getId().split("_")[0].equals("0") || selected.getId().split("_")[0].equals("1") || selected.getId().split("_")[0].equals("2") || selected.getId().split("_")[0].equals("3"))){
            grayOut(true);
            graySlots(false,"buy");
            buyDevelopmentCard = new Pair<>(Colours.getColourFromColumn(selected.getId().split("_")[0]),Integer.parseInt(selected.getId().split("_")[1]));
            selected = null;
            confirmAction.setDisable(true);
        }

        else if(isBuyAction){
            notifyBuyDC(buyDevelopmentCard.getKey(),buyDevelopmentCard.getValue(),Integer.parseInt(selected.getId().split("_")[1]),buyDevelopmentAction);
            selected = null;
            grayOut(false);
        }

        else if(!isMoveAction && prevResBuyAction){
            confirmAction.setDisable(true);
            prevResBuyAction = false;
            notifyMoveResources(moveAction);
            moveAction.clear();
        }

        else if(!isMoveAction){
            String ID = selected.getId();
            String[] split = ID.split("_");

            if(split[0].equals("R") || split[0].equals("C")){
                prevResBuyAction = true;
                int i=0;
                for(Triplet<Resources,Integer,Boolean> T : LCMap.values()){
                    if(T.get_3() && T.get_2()==5) i++;
                }
                if(i<2){
                    confirmAction.setDisable(false);
                    unselect();
                    selected = null;
                    notifyBuyResources(split[0].equals("R"),Integer.parseInt(split[1]),new ArrayList<>());
                }
                else if(getWhiteMarbles(split[0].equals("R"),Integer.parseInt(split[1]))>0){
                    grayOut(true);
                    grayPlayedLeaderCard(false);
                    selectedRMLC.clear();
                    glowNextWhiteMarble(split[0].equals("R"),Integer.parseInt(split[1]),selectedRMLC.size());
                    for(Node n : LeaderCardsPlayed.getChildren()){
                        n.setEffect(new Glow());
                        n.setOnMouseClicked(e ->{
                            String[] S = n.getId().split("_");
                            selectedRMLC.add(Integer.parseInt(S[1]));
                            if(selectedRMLC.size() == getWhiteMarbles(split[0].equals("R"),Integer.parseInt(split[1]))){
                                for(Node LC : LeaderCardsPlayed.getChildren()){
                                    LC.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
                                }
                                grayPlayedLeaderCard(true);
                                grayOut(false);
                                grayOutActionDone();
                                unselect();
                                selected = null;
                                notifyBuyResources(split[0].equals("R"),Integer.parseInt(split[1]),selectedRMLC);

                            }else{
                                glowNextWhiteMarble(split[0].equals("R"),Integer.parseInt(split[1]),selectedRMLC.size());
                            }
                        });
                    }
                }
            }

            //Development buy action
            else if(split[0].equals("0") || split[0].equals("1") || split[0].equals("2") || split[0].equals("3")){
                isBuyAction = true;
                Colours color = Colours.getColourFromColumn(split[0]);
                int victoryPoints = Integer.parseInt(split[1]);
                grayOut(true);
                grayWarehouse(false,"buy");
                grayStrongBox(false);
                grayPlayedLeaderCard(false);
                selected.getStyleClass().add("selectable");
                selected.setOnMouseClicked(e -> {
                    grayOut(false);
                    isBuyAction = false;
                    buyDevelopmentAction.clear();
                    if(actionDone) grayOutActionDone();
                });
            }

            //Activate production
            else if(split[0].equals("SLOT")){

            }
        }

        //Resource market action
        else if(isMoveAction){
            prevResBuyAction = false;
            isMoveAction = false;
            notifyMoveResources(moveAction);
            moveAction.clear();
            confirmAction.setDisable(true);
            grayOut(false);
            if(actionDone) grayOutActionDone();
            grayEmptyWarehouse(true);
        }

    }

    public void handleEndTurnClick(){
        if(prevResBuyAction){
            notifyMoveResources(moveAction);
            prevResBuyAction = false;
            moveAction.clear();
        }
        confirmAction.setDisable(true);
        endTurn.setDisable(true);
        notifyEndTurn();
    }

    private void handleSelect(Node n){
        if(!(selected == null)) {
            unselect();
        }
        //select something different or something new
        if(selected == null || !selected.equals(n)) {
            selected =  n;
            if(selected.getId().startsWith("S_")){
                ColorAdjust CA = new ColorAdjust();
                CA.setBrightness(-10000);
                selected.setEffect(CA);
            }
            else {
                DropShadow DS = new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(212, 175, 55), 5.0, 5.0, 0, 0);
                n.setEffect(DS);
                if (!selected.getId().split("_")[1].startsWith("W")) confirmAction.setDisable(false);
            }
        }
        //unselect previous choice
        else if(selected.equals(n)){
            selected = null;
            confirmAction.setDisable(true);
        }

        if(selected!=null){
            if(selected.getId().split("_")[1].startsWith("W")){
                grayEmptyWarehouse(false);
            }
            if((selected.getParent()).equals(Hand)){
                grayHand(true);
            }
        }
    }

    private void handleMoveAction(Pane pane){
        isMoveAction = true;

        String r = Resources.getResourceFromID(selected.getId().split("_")[0]);
        Integer idFrom = Integer.parseInt(String.valueOf(selected.getId().split("_")[1].charAt(1)));
        Integer idTo = Integer.parseInt(pane.getId().split("_")[0].substring(1));

        moveAction.add(new Triplet<>(r,idFrom,idTo));

        Image image = ((ImageView)selected).getImage();
        String id = selected.getId().split("_")[0] + "_" + pane.getId().split("_")[0] + pane.getId().split("_")[1].charAt(0);


        ((Pane) selected.getParent()).setId("W" + selected.getId().split("_")[1].charAt(1) + "_" + selected.getId().split("_")[1].charAt(2) + "Empty");
        if(!selected.getParent().getId().equals(Hand.getId()))
            ((Pane) selected.getParent()).setOnMouseClicked(e -> { handleMoveAction((Pane) selected.getParent());});
        ((Pane) selected.getParent()).getChildren().remove(selected);

        pane.setOnMouseClicked(null);
        grayEmptyWarehouse(false);

        ImageView IV = new ImageView(image);
        IV.setId(id);
        IV.setFitWidth(25.0);
        IV.setPreserveRatio(true);
        pane.getChildren().add(IV);
        IV.setOnMouseClicked(e -> {
            if(selected!=null && selected.getId().split("_")[1].startsWith("W")) handleMoveAction((ImageView) pane.getChildren().get(0));
            else handleSelect((ImageView) pane.getChildren().get(0));
        });

        grayOut(true);
        grayHand(false);
        grayWarehouse(false,"move");
        confirmAction.setDisable(false);
        grayHand(false);

        selected = null;
    }

    //TODO: Scambi con la mano da fixare
    private void handleMoveAction(ImageView IV){
        isMoveAction = true;

        grayOut(true);
        grayHand(false);
        grayWarehouse(false,"move");
        confirmAction.setDisable(false);

        ImageView tmp = new ImageView( ((ImageView)selected).getImage());
        tmp.setId(selected.getId());
        tmp.setFitWidth(25.0);
        tmp.setPreserveRatio(true);

        String r = Resources.getResourceFromID(tmp.getId().split("_")[0]);
        Integer idFrom = Integer.parseInt(String.valueOf(tmp.getId().split("_")[1].charAt(1)));
        Integer idTo = Integer.parseInt(String.valueOf(IV.getId().split("_")[1].charAt(1)));

        moveAction.add(new Triplet<>(r,idFrom,idTo));

        r = Resources.getResourceFromID(IV.getId().split("_")[0]);
        moveAction.add(new Triplet<>(r,idTo,idFrom));

        String idSelected = tmp.getId().split("_")[0] + "_" + IV.getId().split("_")[1];
        String idIV = IV.getId().split("_")[0] + "_" + tmp.getId().split("_")[1];

        Pane pSel =  ((Pane) selected.getParent());
        Pane pIV = ((Pane) IV.getParent());

        pSel.getChildren().remove(selected);
        pIV.getChildren().remove(IV);

        tmp.setId(idSelected);
        IV.setId(idIV);

        pSel.getChildren().add(IV);
        pIV.getChildren().add(tmp);

        grayEmptyWarehouse(false);

        tmp.setOnMouseClicked(e -> {
            if(selected!=null && selected.getId().split("_")[1].startsWith("W")) handleMoveAction(tmp);
            else handleSelect(tmp);
        });
        IV.setOnMouseClicked(e -> {
            if(selected!=null && selected.getId().split("_")[1].startsWith("W")) handleMoveAction(IV);
            else handleSelect(IV);
        });

        grayHand(false);

        selected = null;
    }

    private void handleLeaderCardClick(ImageView backLCView){
        String path ="/images/Hanging Sign.png";

        for(Node node : LeaderCardsPlayed.getChildren()){
            ImageView n = (ImageView) node;
            n.getStyleClass().remove("selectable");
            n.setOnMouseClicked(null);
            n.setOnMouseEntered(null);
            n.setOnMouseExited(null);
        }

        grayOut(true);

        AnchorPane anchorPane = (AnchorPane) LeaderCardsPlayed.getParent();
        Pane confirmPane = new Pane();
        confirmPane.setId("confirmPane");
        confirmPane.setLayoutX(0.0);
        confirmPane.setLayoutY(0.0);
        anchorPane.getChildren().add(confirmPane);

        ImageView sign = new ImageView(new Image(GUI.class.getResource(path).toString()));
        sign.setFitWidth(434);
        sign.setPreserveRatio(true);
        confirmPane.getChildren().add(sign);

        Button confirmButton = new Button();
        confirmButton.getStyleClass().add("gameButton");
        confirmButton.getStyleClass().add("selectable");
        Button discardButton = new Button();
        discardButton.getStyleClass().add("gameButton");
        discardButton.getStyleClass().add("selectable");
        Button cancelButton = new Button();
        cancelButton.getStyleClass().add("gameButton");
        cancelButton.getStyleClass().add("selectable");

        confirmPane.getChildren().add(confirmButton);
        confirmPane.getChildren().add(discardButton);
        confirmPane.getChildren().add(cancelButton);

        confirmButton.setPrefWidth(200);
        confirmButton.setPrefHeight(50);
        discardButton.setPrefWidth(200);
        discardButton.setPrefHeight(50);
        cancelButton.setPrefWidth(200);
        cancelButton.setPrefHeight(50);

        confirmButton.setLayoutX(117);
        discardButton.setLayoutX(117);
        cancelButton.setLayoutX(117);
        confirmButton.setLayoutY(191);
        discardButton.setLayoutY(263.5);
        cancelButton.setLayoutY(336);

        confirmButton.setText("Gioca la carta");
        discardButton.setText("Scarta la carta");
        cancelButton.setText("Annulla selezione");

        TranslateTransition TTIn = new TranslateTransition(Duration.millis(1000),confirmPane);
        TTIn.setFromX(253);
        TTIn.setToX(253);
        TTIn.setFromY(-472);
        TTIn.setToY(-38);
        TTIn.play();

        TranslateTransition TTOut = new TranslateTransition(Duration.millis(1000),confirmPane);
        TTOut.setFromX(253);
        TTOut.setToX(253);
        TTOut.setFromY(-38);
        TTOut.setToY(-472);
        TTOut.setOnFinished(x -> anchorPane.getChildren().remove(confirmPane));

        confirmButton.setOnMouseClicked(e -> {
            notifyActivateLC(Integer.parseInt(backLCView.getId().split("_")[2]));
            TTOut.play();
            grayOut(false);
            if(actionDone) grayOutActionDone();
        });

        discardButton.setOnMouseClicked(e -> {
            notifyDiscardLC(Integer.parseInt(backLCView.getId().split("_")[2]));
            TTOut.play();

            int index = Integer.parseInt(backLCView.getId().split("_")[2]);
            LeaderCardsPlayed.getChildren().remove(backLCView);
            LCMap.remove(index);

            grayOut(false);
            if(actionDone) grayOutActionDone();
        });

        cancelButton.setOnMouseClicked(e -> {
            TTOut.play();

            grayOut(false);
            if(actionDone) grayOutActionDone();

            for(Node node : LeaderCardsPlayed.getChildren()){
                if(node.getId().startsWith("B")) {
                    ImageView n = (ImageView) node;
                    String target = "/images/LeaderCardBack.png";
                    n.setImage(new Image(GUI.class.getResource(target).toString()));
                    int finalJ = Integer.parseInt(n.getId().split("_")[2]);
                    n.setOnMouseEntered(x -> {
                        String p = "/images/leaderCards/"+LCMap.get(finalJ).get_1().getID()+LCMap.get(finalJ).get_2()+".png";
                        n.setImage(new Image(GUI.class.getResource(p).toString()));
                    });
                    n.setOnMouseExited(x ->{
                        String p = "/images/LeaderCardBack.png";
                        n.setImage(new Image(GUI.class.getResource(p).toString()));
                    });
                    n.setOnMouseClicked(x -> handleLeaderCardClick(n));
                }
            }
        });
    }

    //TODO: Selezione leader card
    private void handleBuySelect(Pane resource){
        ImageView IV = (ImageView) resource.getChildren().get(0);
        if(IV.getId().split("_")[1].startsWith("W")) {
            buyDevelopmentAction.add(new Pair<>(Resources.getResourceFromID(IV.getId().split("_")[0]), Integer.parseInt(String.valueOf(IV.getId().split("W")[1].charAt(0)))));
        }
        else if(IV.getId().split("_")[1].startsWith("SB")){}
        else if(IV.getId().split("_")[1].startsWith("LC")){}

        resource.getChildren().get(0).setOnMouseClicked(e -> handleBuyDeselect(resource));
        DropShadow DS = new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(212, 175, 55), 5.0, 5.0, 0, 0);
        resource.setEffect(DS);
    }

    private void handleBuyDeselect(Pane resource){
        ImageView IV = (ImageView) resource.getChildren().get(0);
        if(IV.getId().split("_")[1].startsWith("W")) {
            buyDevelopmentAction.remove(new Pair<>(Resources.getResourceFromID(IV.getId().split("_")[0]), Integer.parseInt(String.valueOf(IV.getId().split("W")[1].charAt(0)))));
        }
        else if(IV.getId().split("_")[1].startsWith("SB")){}
        else if(IV.getId().split("_")[1].startsWith("LC")){}

        resource.setEffect(null);
        resource.getChildren().get(0).setOnMouseClicked(e -> handleBuySelect(resource));
    }


    //Utils
    private int getWhiteMarbles(boolean row, int i){
        int n=0;
        if(row){
            for(int k=0;k<4;k++) n = n + ((grid[i-1][k])?1:0);
        }
        else{
            for(int k=0;k<3;k++) n = n + ((grid[k][i-1])?1:0);
        }
        return n;
    }

    private void glowNextWhiteMarble(boolean row, int i, int currentWM){
        int count = 0;
        int lastWhite = 0;
        i--;
        if(row){
            for(int j=0;j<4;j++) {
                if(grid[i][j]){
                    if(currentWM == count){
                        for (Node node : ResourceMarket.getChildren()) {
                            if (GridPane.getColumnIndex(node) == j && GridPane.getRowIndex(node) == i) {
                                node.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(212, 175, 55), 2.0, 5.0, 0, 0));
                                if(currentWM!=0){
                                    for (Node n : ResourceMarket.getChildren()) {
                                        if (GridPane.getColumnIndex(n) == lastWhite && GridPane.getRowIndex(n) == i) {
                                            n.setEffect(null);
                                        }
                                    }
                                }
                                return;
                            }
                        }
                    }
                    else {
                        lastWhite = j;
                        count++;
                    }
                }
            }
        }
        else{
            for(int j=0;j<3;j++) {
                if(grid[j][i]){
                    if(currentWM == count){
                        for (Node node : ResourceMarket.getChildren()) {
                            if (GridPane.getColumnIndex(node) == i && GridPane.getRowIndex(node) == j) {
                                node.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(212, 175, 55), 2.0, 5.0, 0, 0));
                                if(currentWM!=0){
                                    for (Node n : ResourceMarket.getChildren()) {
                                        if (GridPane.getColumnIndex(n) == i && GridPane.getRowIndex(n) == lastWhite) {
                                            n.setEffect(null);
                                        }
                                    }
                                }
                                return;
                            }
                        }
                    }
                    else {
                        lastWhite = j;
                        count++;
                    }
                }
            }
        }
    }

    private void unselect() {
        String[] split = selected.getId().split("_");
        if(split[0].equals("0") || split[0].equals("1") || split[0].equals("2") || split[0].equals("3") || selected.getId().startsWith("P_S")){
            selected.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
        }
        else selected.setEffect(null);
        confirmAction.setDisable(false);
    }

}