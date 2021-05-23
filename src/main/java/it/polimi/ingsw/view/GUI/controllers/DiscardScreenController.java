package it.polimi.ingsw.view.GUI.controllers;

import it.polimi.ingsw.commons.Marbles;
import it.polimi.ingsw.commons.Resources;
import it.polimi.ingsw.commons.Triplet;
import it.polimi.ingsw.observers.ViewObservable;

import it.polimi.ingsw.view.GUI.GUI;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;


public class DiscardScreenController extends ViewObservable {

    @FXML
    private GridPane developmentCards;

    @FXML
    private GridPane marbles;

    @FXML
    private GridPane leaderCards;

    private int[] indexes;

    public ArrayList<ImageView> leader = new ArrayList<>();

    @FXML
    public void initialize() {
        indexes = new int[2];
        indexes[0] = -1;
        indexes[1] = -1;
    }

    public void addMarbles(Marbles[][] market, Marbles remaining){
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

    public void addDevelopment(){

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
                case 0-> leaderCards.add(LCView,0,0);
                case 1-> leaderCards.add(LCView,1,0);
                case 2-> leaderCards.add(LCView,0,1);
                case 3-> leaderCards.add(LCView,1,1);
            }
        }
    }

    public void selectCards(){
        if(indexes[0]!=-1 && indexes[1]!=-1) {
            notifySetupDiscardLC(indexes);
        }
    }
}