package it.polimi.ingsw.view.GUI.screenView;

import it.polimi.ingsw.observers.ViewObservable;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class LeaderBoardScreenView extends ViewObservable {
    @FXML
    private Text P1;
    @FXML
    private Text P2;
    @FXML
    private Text P3;
    @FXML
    private Text P4;
    @FXML
    private ImageView P1_I;
    @FXML
    private ImageView P2_I;
    @FXML
    private ImageView P3_I;
    @FXML
    private ImageView P4_I;

    public void setLeaderBoard(HashMap<String,Integer> vp, Boolean lorenzo) {
        Pane p = (Pane) P1.getParent();
        int n = vp.size();
        ArrayList<Integer> players = new ArrayList<>();
        for (String s : vp.keySet()) {
            players.add(vp.get(s));
        }
        Collections.sort(players);
        for (int i = 0; i < n; i++) {
            String nickname = "";
            for (String s : vp.keySet()) {
                if (vp.get(s).equals(players.get(i)))
                    nickname = s;
            }
            switch (i) {
                case 0 -> {
                    P1.setText(nickname + " - Punti vittoria: " + vp.get(nickname));
                }
                case 1 -> {
                    P2.setText(nickname + " - Punti vittoria: " + vp.get(nickname));
                }
                case 2 -> {
                    P3.setText(nickname + " - Punti vittoria: " + vp.get(nickname));
                }
                case 3 -> {
                    P4.setText(nickname + " - Punti vittoria: " + vp.get(nickname));
                }
            }
        }

        if(n==1){
            if(!lorenzo) {
                P2.setText("Lorenzo il magnifico");
            }
            else{
                P2.setText(P1.getText());
                P1.setText("Lorenzo il magnifico");
            }
            p.getChildren().remove(P3);
            p.getChildren().remove(P3_I);
            p.getChildren().remove(P4);
            p.getChildren().remove(P4_I);
        }
        else if(n==2){
            p.getChildren().remove(P3);
            p.getChildren().remove(P3_I);
            p.getChildren().remove(P4);
            p.getChildren().remove(P4_I);
        }
        else if(n==3){
            p.getChildren().remove(P4);
            p.getChildren().remove(P4_I);
        }
    }


    public void submitOnClickHandlerConfirm() {
        notifyAnotherGame();
    }

    public void submitOnClickHandlerCancel() {
        notifyDemolish();
    }
}
