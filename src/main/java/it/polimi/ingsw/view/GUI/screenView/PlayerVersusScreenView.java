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

    /**
     * Method used to generate an animation in the screen.
     * @param players Hashmap with players and their respective position
     */
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
                String target = "/images/Lorenzo.png";
                PL4.setImage(new Image(GUI.class.getResource(target).toString()));
                PL4.setPreserveRatio(true);
                PL4.setFitWidth(500);
                PL4.setLayoutX(1600);
                PL4.setLayoutY(558);
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
                PL4.setImage(PL2.getImage());
                AP.getChildren().remove(P2);
                AP.getChildren().remove(PL2);
                AP.getChildren().remove(PL3);
                AP.getChildren().remove(P3);

                TranslateTransition TT = new TranslateTransition(Duration.seconds(0.5),PL1);
                TT.setFromX(-500);
                TT.setToX(0);
                TranslateTransition TT1 = new TranslateTransition(Duration.seconds(0.5),P1);
                TT1.setFromX(-500);
                TT1.setToX(0);

                PL4.setLayoutX(PL4.getLayoutX()+500);
                P4.setLayoutX(P4.getLayoutX()+500);

                TranslateTransition TL = new TranslateTransition(Duration.seconds(0.5),PL4);
                TL.setToX(-500);
                TranslateTransition TL1 = new TranslateTransition(Duration.seconds(0.5),P4);
                TL1.setToX(-500);
                TL.setDelay(Duration.millis(500));
                TL1.setDelay(Duration.millis(500));
                TT.setOnFinished(e -> {
                    TL1.play();
                    TL.play();
                });
                TT.play();
                TT1.play();
            }
            case 3 -> {
                P1.setText(tmp.get(0));
                P2.setText(tmp.get(1));
                P3.setText(tmp.get(2));
                PL2.setLayoutX(630);
                P2.setLayoutX(690);
                PL3.setLayoutX(1237);
                P3.setLayoutX(1332);
                AP.getChildren().remove(P4);
                AP.getChildren().remove(PL4);
                TranslateTransition TT = new TranslateTransition(Duration.seconds(0.5),PL1);
                TT.setFromY(500);
                TT.setToY(0);
                TranslateTransition TT1 = new TranslateTransition(Duration.seconds(0.5),P1);
                TT1.setFromY(500);
                TT1.setToY(0);
                TranslateTransition TM = new TranslateTransition(Duration.seconds(0.5),PL2);
                TM.setToY(-500);
                TranslateTransition TM1 = new TranslateTransition(Duration.seconds(0.5),P2);
                TM1.setToY(-520);
                TranslateTransition TL = new TranslateTransition(Duration.seconds(0.5),PL3);
                TL.setToY(-500);
                TranslateTransition TL1 = new TranslateTransition(Duration.seconds(0.5),P3);
                TL1.setToY(-520);

                PL2.setLayoutY(PL2.getLayoutY()+500);
                P2.setLayoutY(P2.getLayoutY()+520);
                PL3.setLayoutY(PL3.getLayoutY()+500);
                P3.setLayoutY(P3.getLayoutY()+520);

                TM.setDelay(Duration.millis(500));
                TM1.setDelay(Duration.millis(500));
                TL.setDelay(Duration.millis(1000));
                TL1.setDelay(Duration.millis(1000));
                TT.play();
                TT1.play();
                TM.play();
                TM1.play();
                TL.play();
                TL1.play();
            }
            default -> {
                P1.setText(tmp.get(0));
                P2.setText(tmp.get(1));
                P3.setText(tmp.get(2));
                P4.setText(tmp.get(3));

                TranslateTransition TT = new TranslateTransition(Duration.seconds(0.5),PL1);
                TT.setFromY(500);
                TT.setToY(0);
                TranslateTransition TT1 = new TranslateTransition(Duration.seconds(0.5),P1);
                TT1.setFromY(500);
                TT1.setToY(0);
                TranslateTransition TM = new TranslateTransition(Duration.seconds(0.5),PL2);
                TM.setToY(-500);
                TranslateTransition TM1 = new TranslateTransition(Duration.seconds(0.5),P2);
                TM1.setToY(-520);
                TranslateTransition TL = new TranslateTransition(Duration.seconds(0.5),PL3);
                TL.setToY(-500);
                TranslateTransition TL1 = new TranslateTransition(Duration.seconds(0.5),P3);
                TL1.setToY(-520);
                TranslateTransition TR = new TranslateTransition(Duration.seconds(0.5),PL4);
                TR.setToY(-500);
                TranslateTransition TR1 = new TranslateTransition(Duration.seconds(0.5),P4);
                TR1.setToY(-520);

                PL2.setLayoutY(PL2.getLayoutY()+500);
                P2.setLayoutY(P2.getLayoutY()+520);
                PL3.setLayoutY(PL3.getLayoutY()+500);
                P3.setLayoutY(P3.getLayoutY()+520);
                PL4.setLayoutY(PL4.getLayoutY()+500);
                P4.setLayoutY(P4.getLayoutY()+520);

                TM.setDelay(Duration.millis(500));
                TM1.setDelay(Duration.millis(500));
                TL.setDelay(Duration.millis(1000));
                TL1.setDelay(Duration.millis(1000));
                TR.setDelay(Duration.millis(1500));
                TR1.setDelay(Duration.millis(1500));
                TT.play();
                TT1.play();
                TM.play();
                TM1.play();
                TL.play();
                TL1.play();
                TR.play();
                TR1.play();
            }
        }
    }
}
