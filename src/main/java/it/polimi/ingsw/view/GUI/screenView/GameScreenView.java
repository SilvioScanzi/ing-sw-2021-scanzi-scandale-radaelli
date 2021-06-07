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
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;

//TODO: finestra per scegliere quali risorse prendere nelle produzioni a scelta
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
    private GridPane RES_P1;
    @FXML
    private GridPane RES_P2;
    @FXML
    private GridPane RES_P3;
    @FXML
    private GridPane RES_P4;
    @FXML
    private GridPane RES_P5;
    @FXML
    private GridPane RES_P6;

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

    @FXML
    private GridPane Slots;
    @FXML
    private ImageView P_B_6;
    @FXML
    private ImageView S_1_ARROW;
    @FXML
    private ImageView S_2_ARROW;
    @FXML
    private ImageView S_3_ARROW;

    @FXML
    private Button confirmAction;
    @FXML
    private Button endTurn;

    @FXML
    private GridPane players;


    //key is the index, value is: Resource of the card, Victory points and if played or not
    private final HashMap<Integer,Triplet<Resources,Integer,Boolean>> LCMap = new HashMap<>();
    private final boolean[][] grid = new boolean[3][4];
    private final ArrayList<Integer> selectedRMLC = new ArrayList<>();
    private ImageView selected = null;
    private ImageView selectedCard = null;
    private final ArrayList<Triplet<String,Integer,Integer>> moveAction = new ArrayList<>();
    private final ArrayList<Pair<String, Integer>> buyDevelopmentAction = new ArrayList<>();
    private final HashMap<Integer, ArrayList<Pair<String,Integer>>> productionAction = new HashMap<>();
    private Pair<Colours,Integer> buyDevelopmentCard;
    private boolean isMoveAction = false;
    private boolean isBuyAction = false;
    private boolean isProductionAction = false;
    private boolean actionDone = false;
    private boolean prevResBuyAction = false;
    private Color character;

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

    public void setCharacter(int position) {
        switch (position) {
            case 1 -> character = Color.rgb(212, 28, 36);
            case 2 -> character = Color.rgb(165, 38, 177);
            case 3 -> character = Color.rgb(29, 103, 173);
            case 4 -> character = Color.rgb(252, 112, 51);
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

    public void addPlayers(HashMap<String,ClientBoard> pl, String nickname){
        for(String S : pl.keySet()){
            if(!S.equals(nickname)){
                addPlayerBoard(pl.get(S));
            }
        }
    }

    public void addPlayerBoard(ClientBoard board) {
        boolean flag = false;
        Node b = null;
        int r = 0;
        for (Node n : players.getChildren()) {
            if (n.getId().split("_")[0].equals(board.getNickname())) {
                r = GridPane.getRowIndex(n);
                b = n;
                flag = true;
            }
        }
        if (flag) {
            players.getChildren().remove(b);
        } else {
            r = players.getChildren().size();
        }
        Pane p = new Pane();
        players.add(p, 0, r);
        p.setId(board.getNickname()+"_"+board.getPosition());
        p.setPrefWidth(200);
        p.setPrefHeight(157);
        //background
        String target = "/images/Scroll.png";
        ImageView background = new ImageView(new Image(GUI.class.getResource(target).toString()));
        background.setFitHeight(157);
        background.setFitWidth(200);
        p.getChildren().add(background);
        //nickname
        Text nick = new Text(board.getNickname() + " - " + board.getPosition());
        p.getChildren().add(nick);
        nick.setLayoutX(0);
        nick.setLayoutY(20);
        nick.setTextAlignment(TextAlignment.CENTER);
        nick.getStyleClass().add("nickname");
        nick.setWrappingWidth(200);
        //resources
        for (Resources R : Resources.values()) {
            Pane pane = new Pane();
            target = "/images/resources/" + R.getID() + ".png";
            ImageView Icon = new ImageView(new Image(GUI.class.getResource(target).toString()));
            pane.getChildren().add(Icon);
            Icon.setFitWidth(29);
            Icon.setPreserveRatio(true);
            Icon.setLayoutX(6);
            Icon.setLayoutY(7);
            int amount = board.getStrongBox().get(R);
            for (int j : board.getWarehouse().keySet()) {
                if (board.getWarehouse().get(j).getKey().equals(R)) {
                    amount = amount + board.getWarehouse().get(j).getValue();
                }
            }
            for (Triplet<Resources, Integer, Integer> T : board.getLeaderCardsPlayed()) {
                if (T.get_1().equals(R) && T.get_2() == 3) {
                    amount = amount + T.get_3();
                }
            }
            Text AmountText = new Text("" + amount);
            pane.getChildren().add(AmountText);
            AmountText.setLayoutX(47);
            AmountText.setLayoutY(25);
            AmountText.getStyleClass().add("smallnumber");
            p.getChildren().add(pane);
            switch(R){
                case Coins ->{
                    pane.setLayoutX(33);
                    pane.setLayoutY(18);
                }
                case Shields -> {
                    pane.setLayoutX(33);
                    pane.setLayoutY(52);
                }
                case Servants -> {
                    pane.setLayoutX(109);
                    pane.setLayoutY(18);
                }
                case Stones -> {
                    pane.setLayoutX(109);
                    pane.setLayoutY(52);
                }
            }
        }
        //development cards
        Pane DCpane = new Pane();
        target = "/images/DevelopmentCardBack.png";
        ImageView DCs = new ImageView(new Image(GUI.class.getResource(target).toString()));
        int DCnumber = board.getSlot_1().size() + board.getSlot_2().size() + board.getSlot_3().size();
        Text DCText = new Text(""+DCnumber);
        DCpane.getChildren().add(DCs);
        DCpane.getChildren().add(DCText);
        DCs.setFitWidth(32);
        DCs.setPreserveRatio(true);
        DCs.setLayoutY(-1);
        DCs.setLayoutX(2);
        DCText.setLayoutX(47);
        DCText.setLayoutY(25);
        DCText.getStyleClass().add("smallnumber");
        p.getChildren().add(DCpane);
        DCpane.setLayoutX(35);
        DCpane.setLayoutY(100);
        //leader cards
        Pane LCpane = new Pane();
        target = "/images/LeaderCardBack.png";
        ImageView LCs = new ImageView(new Image(GUI.class.getResource(target).toString()));
        int LCnumber = board.getLeaderCardsPlayed().size();
        Text LCText = new Text(""+LCnumber);
        LCpane.getChildren().add(LCs);
        LCpane.getChildren().add(LCText);
        LCs.setFitWidth(32);
        LCs.setPreserveRatio(true);
        LCs.setLayoutY(-1);
        LCs.setLayoutX(2);
        LCText.setLayoutX(47);
        LCText.setLayoutY(25);
        LCText.getStyleClass().add("smallnumber");
        p.getChildren().add(LCpane);
        LCpane.setLayoutX(108);
        LCpane.setLayoutY(100);
    }

    public void addSlots(ArrayList<Pair<Colours,Integer>> Slot_1,ArrayList<Pair<Colours,Integer>> Slot_2,ArrayList<Pair<Colours,Integer>> Slot_3){
        String path1 = "/images/developmentCards/B1.png";
        String path2 = "/images/developmentCards/G2.png";
        ImageView DCView = new ImageView(new Image(GUI.class.getResource(path1).toString()));
        DCView.setFitWidth(109.0);
        DCView.setPreserveRatio(true);
        DCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
        DCView.setTranslateY(15 -(25.0)*0);
        DCView.setTranslateX(-5);
        DCView.setId("P_S_1");
        StackPane SP = null;
        for (Node n : Slots.getChildren()) {
            if (GridPane.getColumnIndex(n) == 0) {
                SP = (StackPane) n;
            }
        }
        SP.getChildren().add(DCView);


        ImageView DCView1 = new ImageView(new Image(GUI.class.getResource(path2).toString()));
        DCView1.setFitWidth(109.0);
        DCView1.setPreserveRatio(true);
        DCView1.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
        DCView1.setTranslateY(15 -(25.0)*0);
        DCView1.setTranslateX(-5);
        DCView1.setId("P_S_2");
        StackPane SP1 = null;
        for (Node n : Slots.getChildren()) {
            if (GridPane.getColumnIndex(n) == 1) {
                SP1 = (StackPane) n;
            }
        }
        SP1.getChildren().add(DCView1);

        /*for(Node n : Slots.getChildren()){
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
        }*/
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

    public void addLeaderCards(ArrayList<Triplet<Resources,Integer,Integer>> LCPlayed, ArrayList<Triplet<Resources,Integer,Integer>> LCHand){
        LeaderCardsPlayed.getChildren().clear();

        for(Triplet<Resources,Integer,Integer> T : LCPlayed){
            String path = "/images/leaderCards/" + T.get_1().getID() + T.get_2() + ".png";
            ImageView LCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
            LCView.setFitWidth(140.0);
            LCView.setPreserveRatio(true);
            LCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));

            for(int i=1;i<3;i++){
                if(LCMap.containsKey(i) && LCMap.get(i).get_1().equals(T.get_1()) && LCMap.get(i).get_2().equals(T.get_2())) {
                    LCMap.get(i).set_3(true);
                    LCView.setId("F_L_" + (i));
                    LeaderCardsPlayed.add(LCView, i - 1, 0);
                    AnchorPane AP = new AnchorPane();
                    AP.setId("AP_"+i);
                    LeaderCardsPlayed.add(AP, i - 1, 0);
                    if (T.get_2() == 3) {
                        int j;
                        for (j = 0; j < T.get_3(); j++) {
                            Pane P = new Pane();
                            AP.getChildren().add(P);
                            if (j == 0) P.setLayoutX(25);
                            else P.setLayoutX(75);
                            P.setLayoutY(171);
                            P.setPrefHeight(34);
                            P.setPrefWidth(34);
                            P.setId("W_" + (i + 3) + "_" + (j + 1));
                            path = "/images/resources/" + T.get_1().getID() + ".png";
                            ImageView R = new ImageView(new Image(GUI.class.getResource(path).toString()));
                            R.setFitWidth(34);
                            R.setPreserveRatio(true);
                            R.setId(T.get_1().getID() + "_W" + ((i == 1) ? "4" : "5") + j);
                            P.getChildren().add(R);
                        }
                        for(int h=j;h<2;h++){
                            Pane P = new Pane();
                            AP.getChildren().add(P);
                            P.setLayoutY(171);
                            P.setPrefHeight(34);
                            P.setPrefWidth(34);
                            P.setId("W_" + (i + 3) + "_" + (j + 1));
                            if (h == 0) P.setLayoutX(25);
                            else P.setLayoutX(75);
                        }
                    }
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
                    //LeaderCardsPlayed.getChildren().removeIf(n -> GridPane.getColumnIndex(n) == (finalI -1));
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
                    grayNotPlayedLeaderCard(false,backLCView);
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
            IV.setFitWidth(47.0);
            IV.setPreserveRatio(true);
            Hand.add(IV,j,i);
        }
        grayHand(false);
    }

    public void addErrorMessage(String message){
        String path ="/images/Hanging Sign.png";

        grayOut(true);

        AnchorPane anchorPane = (AnchorPane) LeaderCardsPlayed.getParent();
        StackPane errorPane = new StackPane();
        errorPane.setId("errorPane");
        errorPane.setLayoutX(0.0);
        errorPane.setLayoutY(0.0);
        anchorPane.getChildren().add(errorPane);

        ImageView sign = new ImageView(new Image(GUI.class.getResource(path).toString()));
        sign.setFitWidth(434);
        sign.setPreserveRatio(true);
        errorPane.getChildren().add(sign);

        Text t = new Text(message);
        t.setWrappingWidth(300);
        t.setLayoutX(sign.getFitWidth()/2);
        t.setTranslateY(70);
        t.getStyleClass().add("textError");
        t.setTextAlignment(TextAlignment.CENTER);
        errorPane.getChildren().add(t);

        TranslateTransition TTIn = new TranslateTransition(Duration.millis(1000),errorPane);
        TTIn.setFromX(253);
        TTIn.setToX(253);
        TTIn.setFromY(-472);
        TTIn.setToY(-38);
        TTIn.play();
        TranslateTransition TTOut = new TranslateTransition(Duration.millis(1000),errorPane);
        TTIn.setOnFinished(x -> TTOut.play());
        TTOut.setFromX(253);
        TTOut.setToX(253);
        TTOut.setFromY(-38);
        TTOut.setToY(-472);
        TTOut.setDelay(Duration.millis(3000));
        TTOut.setOnFinished(x -> anchorPane.getChildren().remove(errorPane));
    }


    //Gray out methods
    public void grayOut(boolean gray){
        if(gray){
            grayOutActionDone();
            grayWarehouse(true,"");
            grayPlayedLeaderCard(true,"");
            for(int i = 1; i<=2;i++){
                ImageView IM = null;
                for(Node n : LeaderCardsPlayed.getChildren()){
                    if(GridPane.getColumnIndex(n) == i - 1 && !n.getId().startsWith("AP")) IM = (ImageView) n;
                }
                if(LCMap.containsKey(i) && !LCMap.get(i).get_3()) grayNotPlayedLeaderCard(false,IM);
            }
            grayBoards(true);
            grayEmptyWarehouse(true);
            grayHand(true);
            grayStrongBox(true,"");
            grayEmptyLeaderCard(true);
            graySlots(true,"");
        }
        else{
            //Clickable board
            grayWarehouse(false,"move");
            grayCardMarket(false);
            grayBoards(false);
            grayResourceMarket(false);
            grayPlayedLeaderCard(false,"move");
            for(int i = 1; i<=2;i++){
                ImageView IM = null;
                for(Node n : LeaderCardsPlayed.getChildren()){
                    if(GridPane.getColumnIndex(n) == i - 1 && !n.getId().startsWith("AP")) IM = (ImageView) n;
                }
                if(LCMap.containsKey(i) && !LCMap.get(i).get_3()) grayNotPlayedLeaderCard(false,IM);
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

    public void grayBoards(boolean gray){
        if(gray){
            for(Node n : players.getChildren()){
                n.getStyleClass().remove("selectable");
                n.setOnMouseClicked(null);
                n.setOnMouseEntered(null);
                n.setOnMouseExited(null);
            }
        }
        else{
            for(Node n : players.getChildren()){
                if(!n.getStyleClass().contains("selectable")) n.getStyleClass().add("selectable");
                n.setOnMouseEntered(e -> {
                    String s = n.getId().split("_")[1];
                    Color c = switch (s) {
                        case "1" -> Color.rgb(212, 28, 36);
                        case "2" -> Color.rgb(165, 38, 177);
                        case "3" -> Color.rgb(29, 103, 173);
                        case "4" -> Color.rgb(252, 112, 51);
                        default -> Color.WHITE;
                    };
                    n.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, c, 5.0, 5.0, 0, 0));
                });
                n.setOnMouseExited(e -> n.setEffect(null));
                n.setOnMouseClicked(e -> notifyPrintRequest("G "+ n.getId().split("_")[1]));
            }
        }
    }

    public void grayWarehouse(boolean gray, String type){
        if(gray){
            if (W1_1.getChildren().size() > 0) {
                W1_1.getChildren().get(0).getStyleClass().remove("selectable");
                W1_1.getChildren().get(0).setOnMouseClicked(null);
                if(!isBuyAction) W1_1.getChildren().get(0).setEffect(null);
            }
            if (W2_1.getChildren().size() > 0) {
                W2_1.getChildren().get(0).getStyleClass().remove("selectable");
                W2_1.getChildren().get(0).setOnMouseClicked(null);
                if(!isBuyAction) W2_1.getChildren().get(0).setEffect(null);
            }
            if (W2_2.getChildren().size() > 0) {
                W2_2.getChildren().get(0).getStyleClass().remove("selectable");
                W2_2.getChildren().get(0).setOnMouseClicked(null);
                if(!isBuyAction) W2_2.getChildren().get(0).setEffect(null);
            }
            if (W3_1.getChildren().size() > 0) {
                W3_1.getChildren().get(0).getStyleClass().remove("selectable");
                W3_1.getChildren().get(0).setOnMouseClicked(null);
                if(!isBuyAction) W3_1.getChildren().get(0).setEffect(null);
            }
            if (W3_2.getChildren().size() > 0) {
                W3_2.getChildren().get(0).getStyleClass().remove("selectable");
                W3_2.getChildren().get(0).setOnMouseClicked(null);
                if(!isBuyAction) W3_2.getChildren().get(0).setEffect(null);
            }
            if (W3_3.getChildren().size() > 0) {
                W3_3.getChildren().get(0).getStyleClass().remove("selectable");
                W3_3.getChildren().get(0).setOnMouseClicked(null);
                if(!isBuyAction) W3_3.getChildren().get(0).setEffect(null);
            }
        }
        else if (type.equals("move")){
            if (W1_1.getChildren().size() > 0) {
                if(!W1_1.getChildren().get(0).getStyleClass().contains("selectable")) W1_1.getChildren().get(0).getStyleClass().add("selectable");
                W1_1.getChildren().get(0).setOnMouseClicked(e -> {
                    if (selected != null && selected.getId().split("_")[1].startsWith("W") && !selected.equals(W1_1.getChildren().get(0)))
                        handleMoveAction((ImageView) W1_1.getChildren().get(0));
                    else handleSelect((ImageView) W1_1.getChildren().get(0));
                });
            }
            if (W2_1.getChildren().size() > 0) {
                if(!W2_1.getChildren().get(0).getStyleClass().contains("selectable")) W2_1.getChildren().get(0).getStyleClass().add("selectable");
                W2_1.getChildren().get(0).setOnMouseClicked(e -> {
                    if (selected != null && selected.getId().split("_")[1].startsWith("W") && !selected.equals(W2_1.getChildren().get(0)))
                        handleMoveAction((ImageView) W2_1.getChildren().get(0));
                    else handleSelect((ImageView) W2_1.getChildren().get(0));
                });
            }
            if (W2_2.getChildren().size() > 0) {
                if(!W2_2.getChildren().get(0).getStyleClass().contains("selectable")) W2_2.getChildren().get(0).getStyleClass().add("selectable");
                W2_2.getChildren().get(0).setOnMouseClicked(e -> {
                    if (selected != null && selected.getId().split("_")[1].startsWith("W") && !selected.equals(W2_2.getChildren().get(0)))
                        handleMoveAction((ImageView) W2_2.getChildren().get(0));
                    else handleSelect((ImageView) W2_2.getChildren().get(0));
                });
            }
            if (W3_1.getChildren().size() > 0) {
                if(!W3_1.getChildren().get(0).getStyleClass().contains("selectable")) W3_1.getChildren().get(0).getStyleClass().add("selectable");
                W3_1.getChildren().get(0).setOnMouseClicked(e -> {
                    if (selected != null && selected.getId().split("_")[1].startsWith("W") && !selected.equals(W3_1.getChildren().get(0)))
                        handleMoveAction((ImageView) W3_1.getChildren().get(0));
                    else handleSelect((ImageView) W3_1.getChildren().get(0));
                });
            }
            if (W3_2.getChildren().size() > 0) {
                if(!W3_2.getChildren().get(0).getStyleClass().contains("selectable")) W3_2.getChildren().get(0).getStyleClass().add("selectable");
                W3_2.getChildren().get(0).setOnMouseClicked(e -> {
                    if (selected != null && selected.getId().split("_")[1].startsWith("W") && !selected.equals(W3_2.getChildren().get(0)))
                        handleMoveAction((ImageView) W3_2.getChildren().get(0));
                    else handleSelect((ImageView) W3_2.getChildren().get(0));
                });
            }
            if (W3_3.getChildren().size() > 0) {
                if(!W3_3.getChildren().get(0).getStyleClass().contains("selectable")) W3_3.getChildren().get(0).getStyleClass().add("selectable");
                W3_3.getChildren().get(0).setOnMouseClicked(e -> {
                    if (selected != null && selected.getId().split("_")[1].startsWith("W") && !selected.equals(W3_3.getChildren().get(0)))
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
            if (W1_1.getChildren().size() > 0) {
                if(!W1_1.getChildren().get(0).getStyleClass().contains("selectable")) W1_1.getChildren().get(0).getStyleClass().add("selectable");
                W1_1.getChildren().get(0).setOnMouseClicked(e -> handleProductionSelect(W1_1));
            }
            if (W2_1.getChildren().size() > 0) {
                if(!W2_1.getChildren().get(0).getStyleClass().contains("selectable")) W2_1.getChildren().get(0).getStyleClass().add("selectable");
                W2_1.getChildren().get(0).setOnMouseClicked(e -> handleProductionSelect(W2_1));
            }
            if (W2_2.getChildren().size() > 0) {
                if(!W2_2.getChildren().get(0).getStyleClass().contains("selectable")) W2_2.getChildren().get(0).getStyleClass().add("selectable");
                W2_2.getChildren().get(0).setOnMouseClicked(e -> handleProductionSelect(W2_2));
            }
            if (W3_1.getChildren().size() > 0) {
                if(!W3_1.getChildren().get(0).getStyleClass().contains("selectable")) W3_1.getChildren().get(0).getStyleClass().add("selectable");
                W3_1.getChildren().get(0).setOnMouseClicked(e -> handleProductionSelect(W3_1));
            }
            if (W3_2.getChildren().size() > 0) {
                if(!W3_2.getChildren().get(0).getStyleClass().contains("selectable")) W3_2.getChildren().get(0).getStyleClass().add("selectable");
                W3_2.getChildren().get(0).setOnMouseClicked(e -> handleProductionSelect(W3_2));
            }
            if (W3_3.getChildren().size() > 0) {
                if(!W3_3.getChildren().get(0).getStyleClass().contains("selectable")) W3_3.getChildren().get(0).getStyleClass().add("selectable");
                W3_3.getChildren().get(0).setOnMouseClicked(e -> handleProductionSelect(W3_3));
            }
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

    public void grayNotPlayedLeaderCard(boolean gray, ImageView backLCView) {
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

    public void grayPlayedLeaderCard(boolean gray,String type) {
        if (gray) {
            for (Integer i : LCMap.keySet()) {
                if (LCMap.get(i).get_3()) {
                    for (Node m : LeaderCardsPlayed.getChildren()) {
                        if (m.getId().startsWith("AP") && GridPane.getColumnIndex(m) == (i - 1)) {
                            for (Node h : ((AnchorPane) m).getChildren()) {
                                for(Node n : ((Pane) h).getChildren()){
                                    n.getStyleClass().remove("selectable");
                                    n.setOnMouseClicked(null);
                                }
                            }
                        }
                    }
                }
            }
        } else if (type.equals("move")){
            for (Integer i : LCMap.keySet()) {
                if (LCMap.get(i).get_3()) {
                    for (Node m : LeaderCardsPlayed.getChildren()) {
                        if (m.getId().startsWith("AP") && GridPane.getColumnIndex(m) == (i - 1)) {
                            for (Node h : ((AnchorPane) m).getChildren()) {
                                for(Node n : ((Pane) h).getChildren()){
                                    if (!n.getStyleClass().contains("selectable")) n.getStyleClass().add("selectable");
                                    n.setOnMouseClicked(e -> {
                                        if (selected != null && selected.getId().split("_")[1].startsWith("W") && !selected.equals(n))
                                            handleMoveAction((ImageView) n);
                                        else handleSelect((ImageView) n);
                                    });
                                }
                            }
                        }
                    }
                }
            }
        } else if(type.equals("buy")){
            for (Integer i : LCMap.keySet()) {
                if (LCMap.get(i).get_3()) {
                    for (Node m : LeaderCardsPlayed.getChildren()) {
                        if (m.getId().startsWith("AP") && GridPane.getColumnIndex(m) == (i - 1)) {
                            for (Node h : ((AnchorPane) m).getChildren()) {
                                for(Node n : ((Pane) h).getChildren()){
                                    if (!n.getStyleClass().contains("selectable")) n.getStyleClass().add("selectable");
                                    n.setOnMouseClicked(e -> handleBuySelect((Pane) n.getParent()));
                                }
                            }
                        }
                    }
                }
            }
        } else if(type.equals("production")){
            for (Integer i : LCMap.keySet()) {
                if (LCMap.get(i).get_3()) {
                    for (Node m : LeaderCardsPlayed.getChildren()) {
                        if (m.getId().startsWith("AP") && GridPane.getColumnIndex(m) == (i - 1)) {
                            for (Node h : ((AnchorPane) m).getChildren()) {
                                for(Node n : ((Pane) h).getChildren()){
                                    if (!n.getStyleClass().contains("selectable")) n.getStyleClass().add("selectable");
                                    n.setOnMouseClicked(e -> handleProductionSelect((Pane) n.getParent()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void grayStrongBox(boolean gray,String type){
        if(gray){
            SB_CO_ICON.getStyleClass().remove("selectable");
            SB_CO_ICON.setOnMouseClicked(null);
            SB_SH_ICON.getStyleClass().remove("selectable");
            SB_SH_ICON.setOnMouseClicked(null);
            SB_ST_ICON.getStyleClass().remove("selectable");
            SB_ST_ICON.setOnMouseClicked(null);
            SB_SE_ICON.getStyleClass().remove("selectable");
            SB_SE_ICON.setOnMouseClicked(null);
        }else if(type.equals("buy")){
            if(!SB_CO_ICON.getStyleClass().contains("selectable")) SB_CO_ICON.getStyleClass().add("selectable");
            SB_CO_ICON.setOnMouseClicked(e -> handleBuySelect((Pane) SB_CO_ICON.getParent()));
            if(!SB_SH_ICON.getStyleClass().contains("selectable")) SB_SH_ICON.getStyleClass().add("selectable");
            SB_SH_ICON.setOnMouseClicked(e -> handleBuySelect((Pane) SB_SH_ICON.getParent()));
            if(!SB_ST_ICON.getStyleClass().contains("selectable")) SB_ST_ICON.getStyleClass().add("selectable");
            SB_ST_ICON.setOnMouseClicked(e -> handleBuySelect((Pane) SB_ST_ICON.getParent()));
            if(!SB_SE_ICON.getStyleClass().contains("selectable")) SB_SE_ICON.getStyleClass().add("selectable");
            SB_SE_ICON.setOnMouseClicked(e -> handleBuySelect((Pane) SB_SE_ICON.getParent()));
        }
        else if(type.equals("production")){
            if(!SB_CO_ICON.getStyleClass().contains("selectable")) SB_CO_ICON.getStyleClass().add("selectable");
            SB_CO_ICON.setOnMouseClicked(e -> handleProductionSelect((Pane) SB_CO_ICON.getParent()));
            if(!SB_SH_ICON.getStyleClass().contains("selectable")) SB_SH_ICON.getStyleClass().add("selectable");
            SB_SH_ICON.setOnMouseClicked(e -> handleProductionSelect((Pane) SB_SH_ICON.getParent()));
            if(!SB_ST_ICON.getStyleClass().contains("selectable")) SB_ST_ICON.getStyleClass().add("selectable");
            SB_ST_ICON.setOnMouseClicked(e -> handleProductionSelect((Pane) SB_ST_ICON.getParent()));
            if(!SB_SE_ICON.getStyleClass().contains("selectable")) SB_SE_ICON.getStyleClass().add("selectable");
            SB_SE_ICON.setOnMouseClicked(e -> handleProductionSelect((Pane) SB_SE_ICON.getParent()));
        }
    }

    public void grayProduction(boolean gray){
        if(gray){
            P_B_6.getStyleClass().remove("selectable");
            P_B_6.setOnMouseClicked(null);
        }else{
            if(!P_B_6.getStyleClass().contains("selectable")) P_B_6.getStyleClass().add("selectable");
            P_B_6.setOnMouseClicked(e -> {
                if(!isProductionAction) handleSelect(P_B_6);
                else handleProduction(P_B_6);
            });
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
            S_1_ARROW.getStyleClass().remove("bigselectable");
            S_1_ARROW.setOnMouseClicked(null);
            S_1_ARROW.setEffect(null);
            S_2_ARROW.getStyleClass().remove("bigselectable");
            S_2_ARROW.setOnMouseClicked(null);
            S_2_ARROW.setEffect(null);
            S_3_ARROW.getStyleClass().remove("bigselectable");
            S_3_ARROW.setOnMouseClicked(null);
            S_3_ARROW.setEffect(null);

            //grayOut of leader cards with production action
            for(Integer I : LCMap.keySet()){
                if(LCMap.get(I).get_3() && LCMap.get(I).get_2()==4){
                    for(Node n : LeaderCardsPlayed.getChildren()){
                        if(GridPane.getColumnIndex(n) == I-1 && !n.getId().startsWith("AP")){
                            n.getStyleClass().remove("selectable");
                            n.setOnMouseClicked(null);
                        }
                    }
                }
            }
        }else if(type.equals("production")){
            //grayIn of the slots
            for(Node n : Slots.getChildren()) {
                for(Node d : ((StackPane) n).getChildren()) {
                    if(!(d.getId()==null) && d.getId().startsWith("P_S_")) {
                        if(!d.getStyleClass().contains("selectable")) d.getStyleClass().add("selectable");
                        d.setOnMouseClicked(e -> {
                            if(!isProductionAction) handleSelect((ImageView) d);
                            else handleProduction((ImageView) d);
                        });
                    }
                }
            }

            //grayIn of the leader cards with production action
            for(Integer I : LCMap.keySet()){
                if(LCMap.get(I).get_3() && LCMap.get(I).get_2()==4){
                    for(Node n : LeaderCardsPlayed.getChildren()){
                        if(GridPane.getColumnIndex(n) == I-1 && !n.getId().startsWith("AP")){
                            if(!n.getStyleClass().contains("selectable")) n.getStyleClass().add("selectable");
                            n.setOnMouseClicked(e -> {
                                if(!isProductionAction) handleSelect((ImageView) n);
                                else handleProduction((ImageView) n);
                            });
                        }
                    }
                }
            }
        }else if(type.equals("buy")){
            for(Node n : Slots.getChildren()) {
                if(((StackPane)n).getChildren().size()<3) {
                    ImageView arrow;
                    switch(GridPane.getColumnIndex(n)){
                        case 0 -> arrow = S_1_ARROW;
                        case 1 -> arrow = S_2_ARROW;
                        case 2 -> arrow = S_3_ARROW;
                        default -> arrow = S_1_ARROW;
                    }
                    if (!n.getStyleClass().contains("bigselectable")) arrow.getStyleClass().add("bigselectable");
                    arrow.setOnMouseClicked(e -> handleSelect(arrow));
                }
            }
        }
    }

    public void grayEmptyLeaderCard(boolean gray){
        if (gray) {
            for (Integer i : LCMap.keySet()) {
                if (LCMap.get(i).get_3()) {
                    for (Node m : LeaderCardsPlayed.getChildren()) {
                        if (m.getId().startsWith("AP") && GridPane.getColumnIndex(m) == (i - 1)) {
                            for (Node n : ((AnchorPane) m).getChildren()) {
                                n.setOnMouseClicked(null);
                            }
                        }
                    }
                }
            }
        } else {
            for (Integer i : LCMap.keySet()) {
                if (LCMap.get(i).get_3()) {
                    for (Node m : LeaderCardsPlayed.getChildren()) {
                        if (m.getId().startsWith("AP") && GridPane.getColumnIndex(m) == (i - 1)) {
                            for (Node n : ((AnchorPane) m).getChildren()) {
                                if(((Pane)n).getChildren().size()==0)
                                    n.setOnMouseClicked(e -> handleMoveAction((Pane) n));
                            }
                        }
                    }
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
            selectedCard = selected;
            selected = null;
            confirmAction.setDisable(true);
        }

        else if(isBuyAction){
            isBuyAction = false;
            grayOut(false);
            notifyBuyDC(buyDevelopmentCard.getKey(),buyDevelopmentCard.getValue(),Integer.parseInt(selected.getId().split("_")[1]),buyDevelopmentAction);
            selectedCard.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
            selectedCard = null;
            selected = null;
            buyDevelopmentAction.clear();
            confirmAction.setDisable(true);
        }

        else if(!isMoveAction && prevResBuyAction){
            confirmAction.setDisable(true);
            prevResBuyAction = false;
            notifyMoveResources(moveAction);
            moveAction.clear();
        }

        else if(!isMoveAction && !isProductionAction){
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
                    grayPlayedLeaderCard(false,"move");
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
                                grayPlayedLeaderCard(true,"");
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
                grayOut(true);
                grayWarehouse(false,"buy");
                grayStrongBox(false,"buy");
                grayPlayedLeaderCard(false,"buy");
                selected.getStyleClass().add("selectable");
                selected.setOnMouseClicked(e -> {
                    isBuyAction = false;
                    grayOut(false);
                    selected.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
                    buyDevelopmentAction.clear();
                    confirmAction.setDisable(true);
                });
            }

            //Activate production
            else if(split[0].equals("P")){
                unselect();
                grayOut(true);
                isProductionAction = true;
                graySlots(false,"production");
            }
        }

        //Resource market action
        else if(isMoveAction){
            prevResBuyAction = false;
            isMoveAction = false;
            grayOut(false);
            notifyMoveResources(moveAction);
            moveAction.clear();
            confirmAction.setDisable(true);
        }

        else if(isProductionAction){
            isProductionAction = false;
            grayOut(false);
            confirmAction.setDisable(true);
            System.out.println(productionAction);
            notifyActivateProduction(productionAction);
            productionAction.clear();
            RES_P1.getChildren().clear();
            RES_P2.getChildren().clear();
            RES_P3.getChildren().clear();
            RES_P4.getChildren().clear();
            RES_P5.getChildren().clear();
            RES_P6.getChildren().clear();
            unselect();
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

    private void handleSelect(ImageView n){
        if(selected != null) {
            unselect();
        }
        //select something different or something new
        if(selected == null || !selected.equals(n)) {
            selected =  n;
            DropShadow DS = new DropShadow(BlurType.THREE_PASS_BOX, character, 5.0, 5.0, 0, 0);
            n.setEffect(DS);
            if (!selected.getId().split("_")[1].startsWith("W")) confirmAction.setDisable(false);
        }
        //unselect previous choice
        else if(selected.equals(n)){
            selected = null;
            if(!isMoveAction) confirmAction.setDisable(true);
        }

        if(selected!=null){
            if(selected.getId().split("_")[1].startsWith("W")){
                grayEmptyWarehouse(false);
                grayEmptyLeaderCard(false);
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

        Image image = selected.getImage();
        String id = selected.getId().split("_")[0] + "_" + pane.getId().split("_")[0] + pane.getId().split("_")[1].charAt(0);

        if((selected.getParent().getId() == null) || !selected.getParent().getId().equals(Hand.getId())) {
            (selected.getParent()).setId("W" + selected.getId().split("_")[1].charAt(1) + "_" + selected.getId().split("_")[1].charAt(2) + "Empty");
            (selected.getParent()).setOnMouseClicked(e -> {
                handleMoveAction((Pane) selected.getParent());
            });
        }

        ((Pane) selected.getParent()).getChildren().remove(selected);

        pane.setOnMouseClicked(null);

        ImageView IV = new ImageView(image);
        IV.setId(id);
        if(pane.getId().startsWith("W4") || pane.getId().startsWith("W5"))
            IV.setFitWidth(34.0);
        else
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
        grayPlayedLeaderCard(false,"move");
        refreshHand();
        confirmAction.setDisable(false);
        selected = null;
    }

    private void handleMoveAction(ImageView IV){
        isMoveAction = true;

        ImageView tmp = new ImageView(selected.getImage());
        tmp.setId(selected.getId());
        tmp.setPreserveRatio(true);

        if(selected.getParent().equals(Hand))
            IV.setFitWidth(47.0);
        else if(selected.getParent().getId()!=null)
            IV.setFitWidth(34.0);
        else
            IV.setFitWidth(25.0);

        if(IV.getParent().equals(Hand))
            tmp.setFitWidth(47.0);
        else if(IV.getParent().getId()!=null)
            tmp.setFitWidth(34.0);
        else
            tmp.setFitWidth(25.0);

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

        tmp.setOnMouseClicked(e -> {
            if(selected!=null && selected.getId().split("_")[1].startsWith("W")) handleMoveAction(tmp);
            else handleSelect(tmp);
        });
        IV.setOnMouseClicked(e -> {
            if(selected!=null && selected.getId().split("_")[1].startsWith("W")) handleMoveAction(IV);
            else handleSelect(IV);
        });

        grayOut(true);
        grayHand(false);
        grayWarehouse(false,"move");
        grayPlayedLeaderCard(false,"move");
        refreshHand();
        confirmAction.setDisable(false);
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

    private void handleBuySelect(Pane resource){
        ImageView IV = (ImageView) resource.getChildren().get(0);
        if(IV.getId().split("_")[1].startsWith("W")) {
            buyDevelopmentAction.add(new Pair<>(Resources.getResourceFromID(IV.getId().split("_")[0]), Integer.parseInt(String.valueOf(IV.getId().split("W")[1].charAt(0)))));
            IV.setOnMouseClicked(e -> handleBuyDeselect(resource,null));
            DropShadow DS = new DropShadow(BlurType.THREE_PASS_BOX, character, 5.0, 5.0, 0, 0);
            IV.setEffect(DS);
        }
        else if(IV.getId().split("_")[0].startsWith("SB")) {
            if (Hand.getChildren().size() < 10 && Integer.parseInt(((Text) resource.getChildren().get(1)).getText()) > 0) {
                buyDevelopmentAction.add(new Pair<>(Resources.getResourceFromID(IV.getId().split("_")[1]), 6));
                int HS = Hand.getChildren().size();
                ImageView R = new ImageView(IV.getImage());
                R.getStyleClass().add("selectable");
                R.setOnMouseClicked(e -> handleBuyDeselect(resource, R));
                R.setFitWidth(25.0);
                R.setPreserveRatio(true);
                R.setId("SB_" + IV.getId().split("_")[1] + "_HAND");
                if (HS < 5) Hand.add(R, 0, HS);
                else Hand.add(R, 1, HS % 5);
                ((Text) resource.getChildren().get(1)).setText("" + (Integer.parseInt(((Text) resource.getChildren().get(1)).getText()) - 1));
            }
            else{
                addErrorMessage("Le carte sviluppo non costano più di 7 risorse, sei già oltre il limite!");
            }
        }
    }

    private void handleBuyDeselect(Pane resource,ImageView HandRes){
        ImageView IV = (ImageView) resource.getChildren().get(0);
        if(IV.getId().split("_")[1].startsWith("W")) {
            buyDevelopmentAction.remove(new Pair<>(Resources.getResourceFromID(IV.getId().split("_")[0]), Integer.parseInt(String.valueOf(IV.getId().split("W")[1].charAt(0)))));
        }
        else if(IV.getId().split("_")[0].startsWith("SB")) {
            buyDevelopmentAction.remove(new Pair<>(Resources.getResourceFromID(IV.getId().split("_")[1]), 6));
            Hand.getChildren().remove(HandRes);
            refreshHand();
            ((Text) resource.getChildren().get(1)).setText("" + (Integer.parseInt(((Text) resource.getChildren().get(1)).getText()) + 1));
        }
        resource.setEffect(null);
        resource.getChildren().get(0).setOnMouseClicked(e -> handleBuySelect(resource));
    }

    private void handleProduction(ImageView IV){
        isMoveAction = false;
        isBuyAction = false;
        if(selected!=null && selected.getId().equals(IV.getId())){
            //handleProductionDeselect(IV,IV.getParent()); //TODO: popup che chiede se annullare l'azione totale o solo la selezione della carta
            selected = null;
            return;
        }
        handleSelect(IV);

        grayOut(true);
        graySlots(false,"production");
        grayWarehouse(false,"production");
        grayStrongBox(false,"production");
        grayPlayedLeaderCard(false,"production");
    }

    private void handleProductionSelect(Pane pane){
        ImageView IV = (ImageView) pane.getChildren().get(0);
        ImageView R;
        if(IV.getId().split("_")[1].startsWith("W")) {
            int index = 0;
            if(selected.getId().startsWith("F")) index = 3;
            if(!productionAction.containsKey(Integer.parseInt(selected.getId().split("_")[2])+index)) {
                ArrayList<Pair<String,Integer>> tmp1 = new ArrayList<>();
                Pair<String,Integer> tmp2 = new Pair<>(IV.getId().split("_")[0], Integer.parseInt(String.valueOf(IV.getId().split("_")[1].charAt(1))));
                tmp1.add(tmp2);
                productionAction.put(Integer.parseInt(selected.getId().split("_")[2]) + index,tmp1);
            }
            else{
                ArrayList<Pair<String,Integer>> tmp1 = productionAction.get(Integer.parseInt(selected.getId().split("_")[2])+index);
                Pair<String,Integer> tmp2 = new Pair<>(IV.getId().split("_")[0], Integer.parseInt(String.valueOf(IV.getId().split("_")[1].charAt(1))));
                tmp1.add(tmp2);
            }
            IV.setFitWidth(50.0);
            IV.setOnMouseClicked(e -> handleProductionDeselect(IV,IV.getParent()));

            R = IV;
            pane.getChildren().remove(IV);
        }
        else{
            int index = 0;
            if (selected.getId().startsWith("F")) index = 3;

            //TODO: check se effettivamente funziona attivare la produzione prendendo le risorse dallo strongbox
            if(!productionAction.containsKey(Integer.parseInt(selected.getId().split("_")[2])+index)) {
                ArrayList<Pair<String,Integer>> tmp1 = new ArrayList<>();
                Pair<String,Integer> tmp2 = new Pair<>(IV.getId().split("_")[0], Integer.parseInt(String.valueOf(IV.getId().split("_")[1].charAt(1))));
                tmp1.add(tmp2);
                productionAction.put(Integer.parseInt(selected.getId().split("_")[2]) + index,tmp1);
            }
            else{
                ArrayList<Pair<String,Integer>> tmp1 = productionAction.get(Integer.parseInt(selected.getId().split("_")[2])+index);
                Pair<String,Integer> tmp2 = new Pair<>(IV.getId().split("_")[0], Integer.parseInt(String.valueOf(IV.getId().split("_")[1].charAt(1))));
                tmp1.add(tmp2);
            }
            IV.setOnMouseClicked(e -> handleProductionDeselect(IV,IV.getParent()));

            R = new ImageView(IV.getImage());
            R.getStyleClass().add("selectable");
            R.setFitWidth(50.0);
            R.setPreserveRatio(true);
            R.setId(IV.getId());
            R.setOnMouseClicked(e -> handleProductionDeselect(R,R.getParent()));
            ((Text) pane.getChildren().get(1)).setText("" + (Integer.parseInt(((Text) pane.getChildren().get(1)).getText()) - 1));
        }

        switch(Integer.parseInt(selected.getId().split("_")[2])){
            case 1 -> {
                int size = RES_P1.getChildren().size();
                if(size == 0) RES_P1.add(R,0,0);
                else if(size == 1) RES_P1.add(R,1,0);
                else {
                    handleProductionDeselect((ImageView) RES_P1.getChildren().get(0),R.getParent());
                    ImageView tmp = (ImageView) RES_P1.getChildren().remove(0);
                    RES_P1.add(tmp,0,0);
                    RES_P1.add(R,1,0);
                }
            }
            case 2 -> {
                int size = RES_P2.getChildren().size();
                if (size == 0) RES_P2.add(R, 0, 0);
                else if (size == 1) RES_P2.add(R, 1, 0);
                else {
                    handleProductionDeselect((ImageView) RES_P2.getChildren().get(0),R.getParent());
                    ImageView tmp = (ImageView) RES_P2.getChildren().remove(0);
                    RES_P2.add(tmp,0,0);
                    RES_P2.add(R,1,0);
                }
            }
            case 3 -> {
                int size = RES_P3.getChildren().size();
                if (size == 0) RES_P3.add(R, 0, 0);
                else if (size == 1) RES_P3.add(R, 1, 0);
                else {
                    handleProductionDeselect((ImageView) RES_P3.getChildren().get(0),R.getParent());
                    ImageView tmp = (ImageView) RES_P3.getChildren().remove(0);
                    RES_P3.add(tmp,0,0);
                    RES_P3.add(R,1,0);
                }
            }
            case 4 -> {
                int size = RES_P4.getChildren().size();
                if (size == 0) RES_P4.add(R, 0, 0);
                else if (size == 1) RES_P4.add(R, 1, 0);
                else {
                    handleProductionDeselect((ImageView) RES_P4.getChildren().get(0),R.getParent());
                    ImageView tmp = (ImageView) RES_P4.getChildren().remove(0);
                    RES_P4.add(tmp,0,0);
                    RES_P4.add(R,1,0);
                }
            }
            case 5 -> {
                int size = RES_P5.getChildren().size();
                if (size == 0) RES_P5.add(R, 0, 0);
                else if (size == 1) RES_P5.add(R, 1, 0);
                else {
                    handleProductionDeselect((ImageView) RES_P5.getChildren().get(0),R.getParent());
                    ImageView tmp = (ImageView) RES_P5.getChildren().remove(0);
                    RES_P5.add(tmp,0,0);
                    RES_P5.add(R,1,0);
                }
            }
            case 6 -> {
                int size = RES_P6.getChildren().size();
                if (size == 0) RES_P6.add(R, 0, 0);
                else if (size == 1) RES_P6.add(R, 0, 1);
                else {
                    handleProductionDeselect((ImageView) RES_P6.getChildren().get(0),R.getParent());
                    ImageView tmp = (ImageView) RES_P6.getChildren().remove(0);
                    RES_P6.add(tmp,0,0);
                    RES_P6.add(R,0,1);
                }
            }
        }
    }

    private void handleProductionDeselect(ImageView IV,Node oldParent){
        Pane newParent = (Pane) IV.getParent();
        newParent.getChildren().remove(IV);
        if(IV.getId().split("_")[1].startsWith("W")) {
            ((Pane) oldParent).getChildren().add(IV);

            if(IV.getId().split("_")[1].substring(1).startsWith("4") || IV.getId().split("_")[1].substring(1).startsWith("5"))
                IV.setFitWidth(34);
            else IV.setFitWidth(25.0);

            IV.setOnMouseClicked(e -> handleProductionSelect((Pane) oldParent));

        }
        //strongbox
        else {
            ((Text) ((Pane) oldParent).getChildren().get(1)).setText("" + (Integer.parseInt(((Text) ((Pane) oldParent).getChildren().get(1)).getText()) + 1));
        }
    }

    //Utils
    private void refreshHand(){
        ArrayList<Node> tmp = new ArrayList<>(Hand.getChildren());
        Hand.getChildren().clear();
        int i = 0,j = 0;
        for(int size=0;size<tmp.size();size++){
            i = size%5;
            if(size>5) {
                j = 1;
            }
            Hand.add(tmp.get(size),j,i);
        }
    }

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
                                node.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, character, 2.0, 5.0, 0, 0));
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
                                node.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, character, 2.0, 5.0, 0, 0));
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