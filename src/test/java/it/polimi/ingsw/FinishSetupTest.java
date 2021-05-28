package it.polimi.ingsw;

import it.polimi.ingsw.commons.Colours;
import it.polimi.ingsw.commons.Resources;
import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.DevelopmentCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class FinishSetupTest {

    private Game game;
    private Board playerBoard;

    //Initialising Game and Board
    @BeforeEach
    void setup(){
        game = new Game(0);
        String s = "player";
        String s1 = "player1";
        String s2 = "player2";
        String s3 = "player3";
        ArrayList<String> st = new ArrayList<>();
        st.add(s);
        st.add(s1);
        st.add(s2);
        st.add(s3);
        game.setup(st);
        playerBoard = game.getBoard(0);
    }

    @Test
    @DisplayName("Ensure correct behaviour of setup")
    void testGenericFinishSetup(){
        ArrayList<String> choice = new ArrayList<>();
        choice.add("PI");
        game.finishingSetup(1,choice);
        game.finishingSetup(2,choice);
        choice.add("MO");
        game.finishingSetup(3,choice);
        assert(game.getBoard(1).getWarehouse().getDepot(1).getKey().isPresent());
        assert(game.getBoard(1).getWarehouse().getDepot(1).getKey().get().equals(Resources.Stones) && game.getBoard(1).getWarehouse().getDepot(1).getValue()==1);
        assert(game.getBoard(2).getWarehouse().getDepot(1).getKey().isPresent());
        assert(game.getBoard(2).getWarehouse().getDepot(1).getKey().get().equals(Resources.Stones) && game.getBoard(2).getWarehouse().getDepot(1).getValue()==1);
        assert(game.getBoard(2).getFaithTrack().getFaithMarker()==1);
        assert(game.getBoard(3).getWarehouse().getDepot(1).getKey().isPresent());
        assert(game.getBoard(3).getWarehouse().getDepot(1).getKey().get().equals(Resources.Stones) && game.getBoard(3).getWarehouse().getDepot(1).getValue()==1);
        assert(game.getBoard(3).getWarehouse().getDepot(2).getKey().isPresent());
        assert(game.getBoard(3).getWarehouse().getDepot(2).getKey().get().equals(Resources.Coins) && game.getBoard(3).getWarehouse().getDepot(2).getValue()==1);
        assert(game.getBoard(2).getFaithTrack().getFaithMarker()==1);
    }

    @Test
    @DisplayName("Ensure correct behaviour of setup when choosing the same resource multiple times")
    void testDoubleFinishSetup(){
        ArrayList<String> choice = new ArrayList<>();
        choice.add("PI");
        choice.add("PI");
        game.finishingSetup(3,choice);
        assert(game.getBoard(3).getWarehouse().getDepot(2).getKey().isPresent());
        assert(game.getBoard(3).getWarehouse().getDepot(2).getKey().get().equals(Resources.Stones) && game.getBoard(3).getWarehouse().getDepot(2).getValue()==2);
        assert(game.getBoard(3).getFaithTrack().getFaithMarker()==1);
    }
}
