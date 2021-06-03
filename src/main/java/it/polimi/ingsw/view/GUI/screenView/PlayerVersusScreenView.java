package it.polimi.ingsw.view.GUI.screenView;

import it.polimi.ingsw.view.GUI.GUI;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.HashMap;

public class PlayerVersusScreenView {
    @FXML
    private Text P1;
    @FXML
    private ImageView PL1;
    @FXML
    private Text P2;
    @FXML
    private ImageView PL2;
    @FXML
    private Text P3;
    @FXML
    private ImageView PL3;
    @FXML
    private Text P4;
    @FXML
    private ImageView PL4;
    @FXML
    private ImageView VS;


    public void setScreen(HashMap<String,Integer> players){
        AnchorPane AP = (AnchorPane) P1.getParent();
        HashMap<Integer,String> tmp = new HashMap<>();
        for(String s : players.keySet()){
            tmp.put(players.get(s), s);
        }
        switch (players.size()) {
            case 1 -> {
                P1.setText(tmp.get(0));
                P4.setText("Lorenzo Il Magnifico");
                P4.setTranslateX(-300);
                P4.setWrappingWidth(500);
                AP.getChildren().remove(P2);
                AP.getChildren().remove(PL2);
                AP.getChildren().remove(P3);
                AP.getChildren().remove(PL3);
                String target = "/images/lorenzo.png";
                PL4.setImage(new Image(GUI.class.getResource(target).toString()));
                PL4.setPreserveRatio(true);
                PL4.setFitWidth(446);
                PL4.setLayoutX(1600);
                PL4.setLayoutY(558);
                VS.setTranslateY(100);
                P4.setLayoutX(2000);
                TranslateTransition TT = new TranslateTransition(Duration.seconds(0.5),PL1);
                TT.setFromX(-500);
                TT.setToX(PL1.getLayoutX());
                TranslateTransition TT1 = new TranslateTransition(Duration.seconds(0.5),P1);
                TT1.setFromX(-500);
                TT1.setToX(P1.getLayoutX());
                TranslateTransition TL = new TranslateTransition(Duration.seconds(0.7),PL4);
                TL.setFromX(PL4.getLayoutX());
                TL.setToX(-500);
                TranslateTransition TL1 = new TranslateTransition(Duration.seconds(0.7),P4);
                TL1.setFromX(P4.getLayoutX());
                TL1.setToX(-900);
                TL.setDelay(Duration.millis(500));
                TL1.setDelay(Duration.millis(500));
                TT.setOnFinished(e -> {
                    TL1.play();
                    TL.play();
                });
                TT.play();
                TT1.play();

            }
            case 2 -> {
                P1.setText(tmp.get(0));
                P4.setText(tmp.get(1));
                AP.getChildren().remove(P2);
                AP.getChildren().remove(PL2);
                AP.getChildren().remove(PL3);
                AP.getChildren().remove(PL3);
            }
            case 3 -> {
                P1.setText(tmp.get(0));
                P2.setText(tmp.get(1));
                P4.setText(tmp.get(2));
                AP.getChildren().remove(PL3);
                AP.getChildren().remove(PL3);
            }
            default -> {
                {
                    P1.setText(tmp.get(0));
                    P2.setText(tmp.get(1));
                    P3.setText(tmp.get(2));
                    P4.setText(tmp.get(3));
                }
            }
        }
    }
}
