package it.polimi.ingsw.view.GUI.screenView;

import it.polimi.ingsw.commons.*;
import it.polimi.ingsw.observers.ViewObservable;

import it.polimi.ingsw.view.GUI.GUI;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;


public class SetupScreenView extends ViewObservable {

    @FXML
    private GridPane developmentCards;

    @FXML
    private GridPane marbles;

    @FXML
    private GridPane userChoice;    //2 screens: leader cards and resources

    @FXML
    private Button button;

    @FXML
    private ImageView rm;

    @FXML
    private Text message;

    private int[] indexes;
    private final ArrayList<String> chosenResources = new ArrayList<>();
    private int one_twoResourceSetup;
    private int choiceNumber = 1;
    private boolean state = true; //using same screen for 2 states: true for discardLC, false for finishSetup

    private final ArrayList<ImageView> leader = new ArrayList<>();
    private final ArrayList<ImageView> resources = new ArrayList<>();


    @FXML
    public void initialize() {
        button.setDisable(true);
        indexes = new int[2];
        indexes[0] = -1;
        indexes[1] = -1;
    }

    public void addMarbles(Marbles[][] market, Marbles remainingMarble){
        for(int i=0;i<3;i++) {
            for(int j=0;j<4;j++) {
                String path = "/images/marbles/" + market[i][j].getID()+".png";
                ImageView marbleView = new ImageView(new Image(GUI.class.getResource(path).toString()));
                marbleView.setFitHeight(56.0);
                marbleView.setPreserveRatio(true);
                marbles.add(marbleView,j,i);
            }
        }
        String path = "/images/marbles/" + remainingMarble.getID()+".png";
        rm.setImage(new Image(GUI.class.getResource(path).toString()));
    }

    public void addDevelopment(HashMap<Pair<Colours, Integer>, Pair<Integer,Integer>> DCM){
        for(Colours C : Colours.values()){
            for(int i=3;i>=1;i--){
                Pair<Colours,Integer> P = new Pair<>(C,i);
                String path = "/images/developmentCards/" + P.getKey().ColourToString() + DCM.get(P).getKey() +".png";
                ImageView DCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
                DCView.getStyleClass().add("physical");
                DCView.setFitWidth(175.0);
                DCView.setPreserveRatio(true);
                developmentCards.add(DCView,P.getKey().ColourToColumn(),3-P.getValue());
            }
        }
    }

    public void addLeader(ArrayList<Triplet<Resources, Integer, Integer>> LC){
        for(int i=0;i<4;i++){
            Triplet<Resources, Integer, Integer> T = LC.get(i);
            String path = "/images/leaderCards/" + T.get_1().getID()+T.get_2()+".png";
            ImageView LCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
            leader.add(LCView);
            LCView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
            LCView.setFitWidth(175.0);
            LCView.setPreserveRatio(true);
            LCView.setId(""+(i+1));
            EventHandler<MouseEvent> eventHandler = e -> {
                eventHandle(LCView);
            };
            LCView.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);
            LCView.getStyleClass().add("selectable");
            userChoice.add(LCView,i,0);
        }
    }

    public void addResources(int n){
        state = false;
        userChoice.getChildren().clear();
        one_twoResourceSetup = n;
        int i=0;
        for(Resources R : Resources.values()){
            String path = "/images/resources/" + R.getID() + ".png";
            ImageView resourceView = new ImageView(new Image(GUI.class.getResource(path).toString()));
            resources.add(resourceView);
            resourceView.setId(""+(i));
            resourceView.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
            EventHandler<MouseEvent> eventHandler = e -> {
                eventHandle(resourceView);
            };
            resourceView.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);
            resourceView.getStyleClass().add("selectable");
            userChoice.add(resourceView,i,0);
            i++;
        }
    }


    public void selectCards(){
        if(state){
            notifySetupDiscardLC(indexes);
        }
        else{
            if(one_twoResourceSetup == chosenResources.size()){
                notifyFinishSetup(chosenResources);
            }
            else if(one_twoResourceSetup == 2 && chosenResources.size()==1){
                resources.get(indexes[0]).setEffect(null);
                choiceNumber++;
                setMessage("Scegli la seconda risorsa da ottenere");
            }
        }
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            selectCards();
        }
        else if(keyEvent.getCode().equals(KeyCode.DIGIT1) || keyEvent.getCode().equals(KeyCode.NUMPAD1)){
            if(state) {
                eventHandle(leader.get(0));
            }
            else{
                eventHandle(resources.get(0));
            }
        }
        else if(keyEvent.getCode().equals(KeyCode.DIGIT2) || keyEvent.getCode().equals(KeyCode.NUMPAD2)){
            if(state) {
                eventHandle(leader.get(1));
            }
            else{
                eventHandle(resources.get(1));
            }
        }
        else if(keyEvent.getCode().equals(KeyCode.DIGIT3) || keyEvent.getCode().equals(KeyCode.NUMPAD3)){
            if(state) {
                eventHandle(leader.get(2));
            }
            else{
                eventHandle(resources.get(2));
            }
        }
        else if(keyEvent.getCode().equals(KeyCode.DIGIT4) || keyEvent.getCode().equals(KeyCode.NUMPAD4)){
            if(state) {
                eventHandle(leader.get(3));
            }
            else{
                eventHandle(resources.get(3));
            }
        }
    }

    public void setMessage(String S){
        message.setText(S);
    }

    private void eventHandle(ImageView img){
        if(state){
            if (indexes[0] != Integer.parseInt(img.getId()) && indexes[1] != Integer.parseInt(img.getId())) {
                ColorAdjust colorAdjust = new ColorAdjust();
                colorAdjust.setBrightness(0.5);
                img.setEffect(colorAdjust);
                img.getStyleClass().remove("selectable");
                if (indexes[0] == -1) {
                    indexes[0] = Integer.parseInt(img.getId());
                } else if (indexes[1] == -1) {
                    indexes[1] = Integer.parseInt(img.getId());
                    img.setEffect(colorAdjust);
                    button.setDisable(false);
                } else {
                    leader.get(indexes[0] - 1).setEffect(null);
                    leader.get(indexes[0] - 1).getStyleClass().add("selectable");
                    leader.get(indexes[0] - 1).setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
                    int tmp;
                    tmp = indexes[1];
                    indexes[1] = Integer.parseInt(img.getId());
                    indexes[0] = tmp;
                }
            }
        }
        else{
            button.setDisable(false);
            img.getStyleClass().remove("selectable");
            if(choiceNumber == 1) {
                if (indexes[0] != -1) {
                    resources.get(indexes[0]).setEffect(null);
                    resources.get(indexes[0]).getStyleClass().add("selectable");
                    resources.get(indexes[0]).setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
                    chosenResources.clear();
                }
                indexes[0] = Integer.parseInt(img.getId());
            }
            else{
                if (indexes[1] != -1) {
                    resources.get(indexes[1]).setEffect(null);
                    resources.get(indexes[1]).getStyleClass().add("selectable");
                    resources.get(indexes[1]).setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.8), 5, 0, -5, 5));
                    chosenResources.remove(1);
                }
                indexes[1] = Integer.parseInt(img.getId());
            }
            String abbr;
            switch(img.getId()){
                case "0"-> abbr = "MO";
                case "1"-> abbr = "PI";
                case "2"-> abbr = "SE";
                case "3"-> abbr = "SC";
                default -> abbr = "";
            }
            chosenResources.add(abbr);
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(0.5);
            img.setEffect(colorAdjust);
        }
    }
}