package it.polimi.ingsw.view.GUI.controllers;

import it.polimi.ingsw.commons.*;
import it.polimi.ingsw.observers.ViewObservable;
import it.polimi.ingsw.view.GUI.GUI;
import it.polimi.ingsw.view.clientModel.ClientBoard;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

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
    private GridPane Slots;

    @FXML
    private Button confirmAction;
    @FXML
    private Button endTurn;

    //key is the index, value is: Resource of the card, Victory points and if played or not
    private final HashMap<Integer,Triplet<Resources,Integer,Boolean>> LCMap = new HashMap<>();
    private final boolean[][] grid = new boolean[3][4];
    private final ArrayList<Integer> selectedRMLC = new ArrayList<>();

    private ImageView selected = null;

    private ArrayList<Resources> Silvio = new ArrayList<>();

    private final ArrayList<Triplet<String,Integer,Integer>> moveAction = new ArrayList<>();


    @FXML
    public void initialize() { }

    public void addLCMap(ArrayList<Triplet<Resources,Integer,Integer>> L){
        for(int i=0;i<2;i++){
            LCMap.put((i+1),new Triplet<>(L.get(i).get_1(),L.get(i).get_2(),false));
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

    public void addDevelopment(HashMap<Pair<Colours, Integer>, Integer> DCM){
        CardMarket.getChildren().clear();

        for(Colours C : Colours.values()){
            for(int i=3;i>=1;i--){
                Pair<Colours,Integer> P = new Pair<>(C,i);
                String path = "/images/developmentCards/" + P.getKey().ColourToString() + DCM.get(new Pair<>(P)) +".png";
                ImageView DCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
                DCView.setFitWidth(140.0);
                DCView.setPreserveRatio(true);
                DCView.setId(P.getKey().ColourToColumn()+"_"+P.getValue().toString());
                DCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
                DCView.getStyleClass().add("selectable");
                DCView.setOnMouseClicked( e -> eventHandle(DCView));
                CardMarket.add(DCView,P.getKey().ColourToColumn(),3-P.getValue());
            }
        }
    }

    //TODO: all methods need heavy testing
    public void addBoard(ClientBoard board){
        addStrongBox(board.getStrongBox());
        addWarehouse(board.getWarehouse());
        addFaithTrack(board.getFaithMarker(), board.getPopeFavor());
        addLeaderCards(board.getLeaderCardsPlayed());
        populateHand(board.getHand());
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
    public void addLeaderCards(ArrayList<Triplet<Resources,Integer,Integer>> LCPlayed){
        int c = 0;
        boolean[] played = {false,false};
        for(Triplet<Resources,Integer,Integer> T : LCPlayed){
            String path = "/images/leaderCards/" + T.get_1().getID() + T.get_2() + ".png";
            ImageView LCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
            LCView.setFitWidth(140.0);
            LCView.setPreserveRatio(true);
            LCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
            LCView.setId("L_"+(c+1));
            if(T.get_1().equals(LCMap.get(1).get_1()) && T.get_2().equals(LCMap.get(1).get_2())){
                LeaderCardsPlayed.add(LCView,0,0);
                played[0]=true;
            }
            else{
                LeaderCardsPlayed.add(LCView,1,0);
                played[1]=true;
            }
            c++;
        }

        int j = 0;
        for(int i=c; i<2;i++){
            String path = "/images/LeaderCardBack.png";
            ImageView backLCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
            backLCView.setFitWidth(140.0);
            backLCView.setPreserveRatio(true);
            backLCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
            backLCView.setId("L_"+(c+1));
            if (played[j]) {
                j++;
            }
            LeaderCardsPlayed.add(backLCView,j,0);
            int finalJ = j;
            backLCView.setOnMouseEntered(e -> {
                String p = "/images/leaderCards/"+LCMap.get(finalJ +1).get_1().getID()+LCMap.get(finalJ + 1).get_2()+".png";
                backLCView.setImage(new Image(GUI.class.getResource(p).toString()));
            });
            backLCView.setOnMouseExited(e ->{
                String p = "/images/LeaderCardBack.png";
                backLCView.setImage(new Image(GUI.class.getResource(p).toString()));
            });
            j++;
        }
    }

    public void populateHand(ArrayList<Resources> hand){
        Silvio = new ArrayList<>(hand);
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

        //Resource hand
        for(Node n : Hand.getChildren()){
            n.getStyleClass().add("selectable");
            n.setOnMouseClicked(e -> {
                if(selected!=null && selected.getId().split("_")[1].startsWith("W")) moveActionHandle((ImageView) n);
                else eventHandle((ImageView) n); });
        }
    }

    public void grayOut(boolean gray){
        if(gray){
            //GrayOut
            setActionDone();
            for(Node n : LeaderCardsPlayed.getChildren()){
                n.getStyleClass().remove("selectable");
                n.setOnMouseClicked(null);
            }
        }
        else{
            //Clickable board
            confirmAction.setDisable(true);
            endTurn.setDisable(true);
            R_1.getStyleClass().add("bigselectable");
            R_2.getStyleClass().add("bigselectable");
            R_3.getStyleClass().add("bigselectable");
            C_1.getStyleClass().add("bigselectable");
            C_2.getStyleClass().add("bigselectable");
            C_3.getStyleClass().add("bigselectable");
            C_4.getStyleClass().add("bigselectable");
            for(Node n : CardMarket.getChildren()){
                n.getStyleClass().add("selectable");
                n.setOnMouseClicked(e -> { eventHandle((ImageView) n); });
            }
            R_1.setOnMouseClicked(e -> { eventHandle(R_1); });
            R_2.setOnMouseClicked(e -> { eventHandle(R_2); });
            R_3.setOnMouseClicked(e -> { eventHandle(R_3); });
            C_1.setOnMouseClicked(e -> { eventHandle(C_1); });
            C_2.setOnMouseClicked(e -> { eventHandle(C_2); });
            C_3.setOnMouseClicked(e -> { eventHandle(C_3); });
            C_4.setOnMouseClicked(e -> { eventHandle(C_4); });

            //Slots
            for(Node n : Slots.getChildren()) {
                n.getStyleClass().add("selectable");
                n.setOnMouseClicked(e -> { eventHandle((ImageView) n); });
            }

            //TODO: mettere che la moveaction si può fare anche con le carte leader di deposito
            //Warehouse
            if(W1_1.getChildren().size()>0){
                W1_1.getChildren().get(0).getStyleClass().add("selectable");
                W1_1.getChildren().get(0).setOnMouseClicked(e -> {
                    if(selected!=null && selected.getId().split("_")[1].startsWith("W")) moveActionHandle((ImageView) W1_1.getChildren().get(0));
                    else eventHandle((ImageView) W1_1.getChildren().get(0));
                });
            }
            if(W2_1.getChildren().size()>0){
                W2_1.getChildren().get(0).getStyleClass().add("selectable");
                W2_1.getChildren().get(0).setOnMouseClicked(e -> {
                    if(selected!=null && selected.getId().split("_")[1].startsWith("W")) moveActionHandle((ImageView) W2_1.getChildren().get(0));
                    else eventHandle((ImageView) W2_1.getChildren().get(0)); });
            }
            if(W2_2.getChildren().size()>0){
                W2_2.getChildren().get(0).getStyleClass().add("selectable");
                W2_2.getChildren().get(0).setOnMouseClicked(e -> {
                    if(selected!=null && selected.getId().split("_")[1].startsWith("W")) moveActionHandle((ImageView) W2_2.getChildren().get(0));
                    else eventHandle((ImageView) W2_2.getChildren().get(0)); });
            }
            if(W3_1.getChildren().size()>0){
                W3_1.getChildren().get(0).getStyleClass().add("selectable");
                W3_1.getChildren().get(0).setOnMouseClicked(e -> {
                    if(selected!=null && selected.getId().split("_")[1].startsWith("W")) moveActionHandle((ImageView) W3_1.getChildren().get(0));
                    else eventHandle((ImageView) W3_1.getChildren().get(0)); });
            }
            if(W3_2.getChildren().size()>0){
                W3_2.getChildren().get(0).getStyleClass().add("selectable");
                W3_2.getChildren().get(0).setOnMouseClicked(e -> {
                    if(selected!=null && selected.getId().split("_")[1].startsWith("W")) moveActionHandle((ImageView) W3_2.getChildren().get(0));
                    else eventHandle((ImageView) W3_2.getChildren().get(0)); });
            }
            if(W3_3.getChildren().size()>0){
                W3_3.getChildren().get(0).getStyleClass().add("selectable");
                W3_3.getChildren().get(0).setOnMouseClicked(e -> {
                    if(selected!=null && selected.getId().split("_")[1].startsWith("W")) moveActionHandle((ImageView) W3_3.getChildren().get(0));
                    else eventHandle((ImageView) W3_3.getChildren().get(0)); });
            }

            //Resource hand
            for(Node n : Hand.getChildren()){
                n.getStyleClass().add("selectable");
                n.setOnMouseClicked(e -> {
                    if(selected!=null && selected.getId().split("_")[1].startsWith("W")) moveActionHandle((ImageView) n);
                    else eventHandle((ImageView) n); });
            }
        }
    }

    public void setActionDone(){
        endTurn.setDisable(false);
        confirmAction.setDisable(true);

        //resource market
        R_1.getStyleClass().remove("bigselectable");
        R_1.setOnMouseClicked(null);
        R_2.getStyleClass().remove("bigselectable");
        R_2.setOnMouseClicked(null);
        R_3.getStyleClass().remove("bigselectable");
        R_3.setOnMouseClicked(null);
        C_1.getStyleClass().remove("bigselectable");
        C_1.setOnMouseClicked(null);
        C_2.getStyleClass().remove("bigselectable");
        C_2.setOnMouseClicked(null);
        C_3.getStyleClass().remove("bigselectable");
        C_3.setOnMouseClicked(null);
        C_4.getStyleClass().remove("bigselectable");
        C_4.setOnMouseClicked(null);

        //card market
        for(Node n : CardMarket.getChildren()){
            n.setOnMouseClicked(null);
            n.getStyleClass().remove("selectable");
        }

        //slots
        for(Node n : Slots.getChildren()) {
            n.getStyleClass().remove("selectable");
            n.setOnMouseClicked(null);
        }


        //Warehouse
        if(W1_1.getChildren().size()>0){
            W1_1.getChildren().get(0).getStyleClass().remove("selectable");
            W1_1.getChildren().get(0).setOnMouseClicked(null);
        }
        if(W2_1.getChildren().size()>0){
            W2_1.getChildren().get(0).getStyleClass().remove("selectable");
            W2_1.getChildren().get(0).setOnMouseClicked(null);
        }
        if(W2_2.getChildren().size()>0){
            W2_2.getChildren().get(0).getStyleClass().remove("selectable");
            W2_2.getChildren().get(0).setOnMouseClicked(null);
        }
        if(W3_1.getChildren().size()>0){
            W3_1.getChildren().get(0).getStyleClass().remove("selectable");
            W3_1.getChildren().get(0).setOnMouseClicked(null);
        }
        if(W3_2.getChildren().size()>0){
            W3_2.getChildren().get(0).getStyleClass().remove("selectable");
            W3_2.getChildren().get(0).setOnMouseClicked(null);
        }
        if(W3_3.getChildren().size()>0){
            W3_3.getChildren().get(0).getStyleClass().remove("selectable");
            W3_3.getChildren().get(0).setOnMouseClicked(null);
        }
    }

    public void handleConfirmClick(){
        String ID = selected.getId();
        String[] split = ID.split("_");
        //Resource market action
        if(split[0].equals("R") || split[0].equals("C")){
            int i=0;
            for(Triplet<Resources,Integer,Boolean> T : LCMap.values()){
                if(T.get_3() && T.get_2()==5) i++;
            }
            if(i<2){
                unselect();
                selected = null;
                notifyBuyResources(split[0].equals("R"),Integer.parseInt(split[1]),new ArrayList<>());
            }
            else if(getWhiteMarbles(split[0].equals("R"),Integer.parseInt(split[1]))>0){
                selectedRMLC.clear();
                glowNextWhiteMarble(split[0].equals("R"),Integer.parseInt(split[1]),selectedRMLC.size());
                for(Node n : LeaderCardsPlayed.getChildren()){
                    n.getStyleClass().add("selectable");
                    n.setEffect(new Glow());
                    n.setOnMouseClicked(e ->{
                        String[] S = n.getId().split("_");
                        selectedRMLC.add(Integer.parseInt(S[1]));
                        if(selectedRMLC.size() == getWhiteMarbles(split[0].equals("R"),Integer.parseInt(split[1]))){
                            for(Node LC : LeaderCardsPlayed.getChildren()){
                                LC.setOnMouseClicked(null);
                                LC.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
                            }
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

        }

        //Activate production
        else if(split[0].equals("SLOT")){

        }
    }

    public void handleEndTurnClick(){
        notifyEndTurn();
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
        if(split[0].equals("0") || split[0].equals("1") || split[0].equals("2") || split[0].equals("3")){
            selected.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
        }
        else selected.setEffect(null);
        confirmAction.setDisable(false);
    }

    private void deselectEmptyWarehouse(){
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
    }

    private void selectEmptyWarehouse(){
        if(W1_1.getChildren().size()==0) {
            W1_1.setId("W1_1Empty");
            W1_1.setOnMouseClicked(e -> {moveActionHandle(W1_1);});
        }
        if(W2_1.getChildren().size()==0) {
            W2_1.setId("W2_1Empty");
            W2_1.setOnMouseClicked(e -> {moveActionHandle(W2_1);});
        }
        if(W2_2.getChildren().size()==0) {
            W2_2.setId("W2_2Empty");
            W2_2.setOnMouseClicked(e -> {moveActionHandle(W2_2);});
        }
        if(W3_1.getChildren().size()==0) {
            W3_1.setId("W3_1Empty");
            W3_1.setOnMouseClicked(e -> {moveActionHandle(W3_1);});
        }
        if(W3_2.getChildren().size()==0) {
            W3_2.setId("W3_2Empty");
            W3_2.setOnMouseClicked(e -> {moveActionHandle(W3_2);});
        }
        if(W3_3.getChildren().size()==0) {
            W3_3.setId("W3_3Empty");
            W3_3.setOnMouseClicked(e -> {moveActionHandle(W3_3);});
        }
    }


    private void eventHandle(ImageView n){
        if(!(selected == null)) {
            unselect();
        }
        //select something different or something new
        if(selected == null || !selected.equals(n)) {
            selected = n;
            DropShadow DS = new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(212, 175, 55), 5.0, 5.0, 0, 0);
            n.setEffect(DS);
            if(!selected.getId().split("_")[1].startsWith("W")) confirmAction.setDisable(false);
        }
        //unselect previous choice
        else if(selected.equals(n)){
            selected = null;
            confirmAction.setDisable(true);
        }

        if(selected!=null){
            if(selected.getId().split("_")[1].startsWith("W")){
                selectEmptyWarehouse();
            }
        }
    }

    private void moveActionHandle(Pane pane){
        //grey out di tutto il resto (metodo a parte)

        String r = Resources.getResourceFromID(selected.getId().split("_")[0]);
        Integer idFrom = Integer.parseInt(String.valueOf(selected.getId().split("_")[1].charAt(1)));
        Integer idTo = Integer.parseInt(pane.getId().split("_")[0].substring(1));

        moveAction.add(new Triplet<>(r,idFrom,idTo));
        System.out.println(new Triplet<>(r,idFrom,idTo));

        Image image = selected.getImage();
        String id = selected.getId().split("_")[0] + "_" + pane.getId().split("_")[0] + pane.getId().split("_")[1].charAt(0);


        ((Pane) selected.getParent()).setId("W" + selected.getId().split("_")[1].charAt(1) + "_" + selected.getId().split("_")[1].charAt(2) + "Empty");
        ((Pane) selected.getParent()).setOnMouseClicked(e -> {moveActionHandle((Pane) selected.getParent());});
        //((Pane) selected.getParent()).getChildren().remove(selected);
        ((Pane) selected.getParent()).getChildren().clear();
        Silvio.remove(Resources.getResourceFromAbbr(Resources.getResourceFromID(selected.getId().split("_")[0])));
        populateHand(Silvio);

        pane.setOnMouseClicked(null);
        deselectEmptyWarehouse();

        ImageView IV = new ImageView(image);
        IV.setId(id);
        IV.setFitWidth(25.0);
        IV.setPreserveRatio(true);
        pane.getChildren().add(IV);
        IV.setOnMouseClicked(e -> {
            if(selected!=null && selected.getId().split("_")[1].startsWith("W")) moveActionHandle((ImageView) pane.getChildren().get(0));
            else eventHandle((ImageView) pane.getChildren().get(0));
        });

        selected = null;
    }

    private void moveActionHandle(ImageView IV){
        ImageView tmp = new ImageView(selected.getImage());
        tmp.setId(selected.getId());
        tmp.setFitWidth(25.0);
        tmp.setPreserveRatio(true);

        //grey out di tutto il resto

        String r = Resources.getResourceFromID(tmp.getId().split("_")[0]);
        Integer idFrom = Integer.parseInt(String.valueOf(tmp.getId().split("_")[1].charAt(1)));
        Integer idTo = Integer.parseInt(String.valueOf(IV.getId().split("_")[1].charAt(1)));

        moveAction.add(new Triplet<>(r,idFrom,idTo));
        System.out.println(new Triplet<>(r,idFrom,idTo));
        r = Resources.getResourceFromID(IV.getId().split("_")[0]);
        moveAction.add(new Triplet<>(r,idTo,idFrom));
        System.out.println(new Triplet<>(r,idFrom,idTo));

        String idSelected = tmp.getId().split("_")[0] + "_" + IV.getId().split("_")[1];
        String idIV = IV.getId().split("_")[0] + "_" + tmp.getId().split("_")[1];

        ((Pane) selected.getParent()).getChildren().add(IV);
        ((Pane) IV.getParent()).getChildren().add(tmp);
        tmp.setId(idSelected);
        IV.setId(idIV);

        ((Pane) selected.getParent()).getChildren().remove(selected);
        ((Pane) IV.getParent()).getChildren().remove(IV);

        deselectEmptyWarehouse();

        tmp.setOnMouseClicked(e -> {
            if(selected!=null && selected.getId().split("_")[1].startsWith("W")) moveActionHandle(tmp);
            else eventHandle(tmp);
        });
        IV.setOnMouseClicked(e -> {
            if(selected!=null && selected.getId().split("_")[1].startsWith("W")) moveActionHandle(IV);
            else eventHandle(IV);
        });

        selected = null;
    }
}