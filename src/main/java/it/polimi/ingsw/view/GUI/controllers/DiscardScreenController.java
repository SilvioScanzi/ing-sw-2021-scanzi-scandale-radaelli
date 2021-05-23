package it.polimi.ingsw.view.GUI.controllers;

import it.polimi.ingsw.commons.*;
import it.polimi.ingsw.observers.ViewObservable;

import it.polimi.ingsw.view.GUI.GUI;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.HashMap;


public class DiscardScreenController extends ViewObservable {

    @FXML
    private GridPane developmentCards;

    @FXML
    private GridPane marbles;

    @FXML
    private GridPane userChoice;    //2 screens: leader cards and resources

    private int[] indexes;
    private ArrayList<String> chosenResources = new ArrayList<>();
    private int one_twoResourceSetup;
    private boolean state; //using same screen for 2 states: true for discardLC, false for finishSetup

    private ArrayList<ImageView> leader = new ArrayList<>();
    private ArrayList<Resources> resources = new ArrayList<>();


    @FXML
    public void initialize() {
        state = true;
        indexes = new int[2];
        indexes[0] = -1;
        indexes[1] = -1;
    }

    public void addMarbles(Marbles[][] market){
        for(int i=0;i<3;i++) {
            for(int j=0;j<4;j++) {
                String path = "/images/marbles/" + market[i][j].getID()+".png";
                ImageView marbleView = new ImageView(new Image(GUI.class.getResource(path).toString()));
                marbleView.setFitHeight(94.0);
                marbleView.setPreserveRatio(true);
                marbles.add(marbleView,j,i);
            }
        }
    }

    public void addDevelopment(HashMap<Pair<Colours, Integer>, Integer> DCM){
        for(Colours C : Colours.values()){
            for(int i=3;i>=1;i--){
                Pair<Colours,Integer> P = new Pair<>(C,i);
                String path = "/images/developmentCards/" + P.getKey().ColourToString() + DCM.get(new Pair<>(P)) +".png";
                ImageView DCView = new ImageView(new Image(GUI.class.getResource(path).toString()));
                DCView.setFitHeight(274.0);
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
            LCView.setFitHeight(274.0);
            LCView.setPreserveRatio(true);
            LCView.getStyleClass().add("unselected");
            LCView.setId(""+(i+1));
            EventHandler<MouseEvent> eventHandler = e -> {
                if (indexes[0] != Integer.parseInt(LCView.getId()) && indexes[1] != Integer.parseInt(LCView.getId())) {
                    LCView.getStyleClass().remove("unselected");
                    LCView.getStyleClass().add("selected");
                    if (indexes[0] == -1) {
                        indexes[0] = Integer.parseInt(LCView.getId());
                    } else if (indexes[1] == -1) {
                        indexes[1] = Integer.parseInt(LCView.getId());
                    } else {
                        leader.get(indexes[0] - 1).getStyleClass().remove("selected");
                        leader.get(indexes[0] - 1).getStyleClass().add("unselected");
                        int tmp;
                        tmp = indexes[1];
                        indexes[1] = Integer.parseInt(LCView.getId());
                        indexes[0] = tmp;
                    }
                }
            };
            LCView.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);

            switch(i){
                case 0-> userChoice.add(LCView,0,0);
                case 1-> userChoice.add(LCView,1,0);
                case 2-> userChoice.add(LCView,0,1);
                case 3-> userChoice.add(LCView,1,1);
            }
        }
    }

    public void addResources(int n){
        userChoice.getChildren().clear();
        one_twoResourceSetup = n;
        int i=0;
        for(Resources R : Resources.values()){
            String path = "/images/resources/" + R.getID() + ".png";
            ImageView resourceView = new ImageView(new Image(GUI.class.getResource(path).toString()));
            resources.add(R);
            resourceView.setId(""+(i));
            EventHandler<MouseEvent> eventHandler = e -> {
                chosenResources.add(resources.get(Integer.parseInt(resourceView.getId())).abbreviation());
            };
            resourceView.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);
            switch(i){
                case 0-> userChoice.add(resourceView,0,0);
                case 1-> userChoice.add(resourceView,1,0);
                case 2-> userChoice.add(resourceView,0,1);
                case 3-> userChoice.add(resourceView,1,1);
            }
            i++;
        }
    }

    public void selectCards(){
        if(state){
            if(indexes[0]!=-1 && indexes[1]!=-1) notifySetupDiscardLC(indexes);
            //TODO: else messaggio di errore
        }
        else{
            if(one_twoResourceSetup == chosenResources.size()){
                System.out.println("Risorse scelte: " + chosenResources);
                notifyFinishSetup(chosenResources);
            }
            //TODO: else messaggio di errore
        }
    }
}