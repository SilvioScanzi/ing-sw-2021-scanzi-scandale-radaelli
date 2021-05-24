package it.polimi.ingsw.view.GUI.controllers;

import it.polimi.ingsw.commons.Colours;
import it.polimi.ingsw.commons.Marbles;
import it.polimi.ingsw.commons.Pair;
import it.polimi.ingsw.observers.ViewObservable;
import it.polimi.ingsw.view.GUI.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.HashMap;


public class GameScreenController extends ViewObservable {

    @FXML
    private GridPane ResourceMarket;

    @FXML
    private GridPane CardMarket;

    @FXML
    public void initialize() {}

    public void addMarbles(Marbles[][] market){
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
}