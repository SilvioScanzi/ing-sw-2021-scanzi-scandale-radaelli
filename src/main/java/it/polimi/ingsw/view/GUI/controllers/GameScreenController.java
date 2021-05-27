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
import javafx.scene.effect.Lighting;
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
    private ImageView SLOT_1;
    @FXML
    private ImageView SLOT_2;
    @FXML
    private ImageView SLOT_3;

    @FXML
    private Button confirmAction;
    @FXML
    private Button endTurn;

    //key is the index, value is: Resource of the card, Victory points and if played or not
    private final HashMap<Integer,Triplet<Resources,Integer,Boolean>> LCMap = new HashMap<>();
    private final boolean[][] grid = new boolean[3][4];
    private final ArrayList<Integer> selectedRMLC = new ArrayList<>();

    private Pair<Colours,Integer> selectedDC;
    private String selectedMarket;
    private ImageView selected = null;

    @FXML
    public void initialize() {
        confirmAction.setDisable(true);
        R_1.setOnMouseClicked(e -> {
            handleMarketClick(R_1);
        });
        R_2.setOnMouseClicked(e -> {
            handleMarketClick(R_2);
        });
        R_3.setOnMouseClicked(e -> {
            handleMarketClick(R_3);
        });
        C_1.setOnMouseClicked(e -> {
            handleMarketClick(C_1);
        });
        C_2.setOnMouseClicked(e -> {
            handleMarketClick(C_2);
        });
        C_3.setOnMouseClicked(e -> {
            handleMarketClick(C_3);
        });
        C_4.setOnMouseClicked(e -> {
            handleMarketClick(C_4);
        });
    }

    private void handleMarketClick(ImageView click){
        if(!(selected == null)) {
            if(selected.getId().split("_").length==2){
                selected.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
            }
            else selected.setEffect(null);
            selected.getStyleClass().add("selectable");
            confirmAction.setDisable(false);
        }
        if(selected == null || !selected.equals(click)){
            selected = click;
            DropShadow DS = new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(212, 175, 55), 5.0, 5.0, 0, 0);
            click.setEffect(DS);
            String[] s = click.getId().split("_");
            selectedMarket = s[0] + " " + s[1];
            confirmAction.setDisable(false);
        }
        else if(selected.equals(click)){
            selected = null;
            selectedMarket = null;
        }
    }

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
                DCView.setOnMouseClicked(e -> {
                    if(!(selected == null)) {
                        selected.getStyleClass().add("selectable");
                        if(selected.getId().split("_").length==2){
                            selected.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
                        }
                        else selected.setEffect(null);
                    }
                    if(selected == null || !selected.equals(DCView)){
                        selected = DCView;
                        DropShadow DS = new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(212, 175, 55), 5.0, 5.0, 0, 0);
                        DCView.setEffect(DS);
                        //DCView.getStyleClass().remove("selectable");
                        String[] s = DCView.getId().split("_");
                        selectedDC = new Pair<>(Colours.getColourFromColumn(s[0]),Integer.parseInt(s[1]));
                    }
                    else if(selected.equals(DCView)){
                        selected = null;
                        selectedDC = null;
                    }
                });
                CardMarket.add(DCView,P.getKey().ColourToColumn(),3-P.getValue());
            }
        }
    }

    //TODO: all methods need heavy testing
    //TODO: check if the before-all clear() are necessary
    public void addBoard(ClientBoard board){
        addStrongBox(board.getStrongBox());
        addWarehouse(board.getWarehouse());
        addFaithTrack(board.getFaithMarker(), board.getPopeFavor());
        addLeaderCards(board.getLeaderCardsPlayed());
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

    public void addHand(ArrayList<Resources> hand){}

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

    public void grayOut(boolean gray){
        if(gray){
            //GrayOut
        }
        else{
            //Clickable board
        }
    }

    public void actionDone(){
        endTurn.setDisable(false);
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
                notifyBuyResources(split[0].equals("R"),Integer.parseInt(split[1]),new ArrayList<>());
            }
            else if(getWhiteMarbles(split[0].equals("R"),Integer.parseInt(split[1]))>0){
                selectedRMLC.clear();
                glowNextWhiteMarble(split[0].equals("R"),Integer.parseInt(split[1]),selectedRMLC.size());
                for(Node n : LeaderCardsPlayed.getChildren()){
                    n.setEffect(new Lighting());
                    n.setOnMouseClicked(e ->{
                        String[] S = n.getId().split("_");
                        selectedRMLC.add(Integer.parseInt(S[1]));
                        if(selectedRMLC.size() == getWhiteMarbles(split[0].equals("R"),Integer.parseInt(split[1]))){
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
        else if(split[0].equals("P")){

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
        if(row){
            for(int j=0;j<4;j++) {
                if(grid[i][j]){
                    if(currentWM == count){
                        for (Node node : ResourceMarket.getChildren()) {
                            if (GridPane.getColumnIndex(node) == i && GridPane.getRowIndex(node) == j) {
                                node.setEffect(new Glow());
                            }
                        }
                    }
                    else {
                        lastWhite = j;
                        count++;
                    }
                }
            }
            if(currentWM!=0){
                for (Node node : ResourceMarket.getChildren()) {
                    if (GridPane.getColumnIndex(node) == i && GridPane.getRowIndex(node) == lastWhite) {
                        node.setEffect(null);
                    }
                }
            }
        }
        else{
            for(int j=0;j<3;j++) {
                if(grid[j][i]){
                    if(currentWM == count){
                        for (Node node : ResourceMarket.getChildren()) {
                            if (GridPane.getColumnIndex(node) == j && GridPane.getRowIndex(node) == i) {
                                node.setEffect(new Glow());
                            }
                        }
                    }
                    else {
                        lastWhite = j;
                        count++;
                    }
                }
            }
            if(currentWM!=0){
                for (Node node : ResourceMarket.getChildren()) {
                    if (GridPane.getColumnIndex(node) == lastWhite && GridPane.getRowIndex(node) == i) {
                        node.setEffect(null);
                    }
                }
            }
        }
    }
}