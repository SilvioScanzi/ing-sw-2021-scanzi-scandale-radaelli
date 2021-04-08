package it.polimi.ingsw;
import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;

public class DevelopmentBuyTest {
    public static void main(String[] args) {
        Game game = new Game(0);
        String s = "player";
        ArrayList<String> st = new ArrayList<>();
        st.add(s);
        game.setup(st);
        Board playerBoard = game.getBoard(0);
        try {
            playerBoard.getWarehouse().addDepot(3, Resources.Servants, 3);
            playerBoard.getWarehouse().addDepot(2,Resources.Stones,2);
        }
        catch(Exception e) {e.printStackTrace();}
        System.out.println(game.getDevelopmentcardmarket().toString());
        System.out.println(playerBoard.getWarehouse().toString());
        try {
            game.getDevelopmentCard(Colours.Purple, 1, 0, 1);
        }
        catch(Exception e) {e.printStackTrace();}
        System.out.println(playerBoard.slottoString());
        try {
            game.getDevelopmentCard(Colours.Purple, 1, 0, 1);
        }
        catch(Exception e) {e.printStackTrace();}
        finally {
            System.out.println(playerBoard.slottoString());
        }
    }
}
