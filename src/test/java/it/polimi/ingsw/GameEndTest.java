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

public class GameEndTest {

        private Game game;
        private Board playerBoard;

        //Initialising Game and Board
        @BeforeEach
        void setup(){
            game = new Game(0);
            String s = "player";
            String s1 = "player1";
            ArrayList<String> st = new ArrayList<>();
            st.add(s);
            st.add(s1);
            game.setup(st);
            playerBoard = game.getBoard(0);
        }

        @Test
        @DisplayName("Ensure correct behaviour of game end method with 7 development cards")
        void testGameEndDC(){
            DevelopmentCard DCP1 = new DevelopmentCard(1, Colours.Purple,5,new HashMap<>(),new HashMap<>(),new HashMap<>(),1);
            DevelopmentCard DCP2 = new DevelopmentCard(2, Colours.Purple,5,new HashMap<>(),new HashMap<>(),new HashMap<>(),1);
            DevelopmentCard DCP3 = new DevelopmentCard(3, Colours.Purple,5,new HashMap<>(),new HashMap<>(),new HashMap<>(),1);
            try{
                playerBoard.getSlot(1).addCard(DCP1);
                playerBoard.getSlot(1).addCard(DCP2);
                playerBoard.getSlot(2).addCard(DCP1);
                playerBoard.getSlot(2).addCard(DCP2);
                playerBoard.getSlot(3).addCard(DCP1);
                playerBoard.getSlot(3).addCard(DCP2);
                playerBoard.getSlot(3).addCard(DCP3);
            }catch(Exception e) {e.printStackTrace();}
            assert(game.checkEndGame());
        }

    @Test
    @DisplayName("Ensure correct behaviour of game end method with FaithTrack")
    void testGameEndFT(){
        for(int i=0;i<25;i++){
            game.getBoard(1).getHand().add(Resources.Stones);
        }
        game.discardRemainingResources(1);
        assert(game.checkEndGame());
        assert(playerBoard.getFaithTrack().getPopeFavor()[0]);
        assert(playerBoard.getFaithTrack().getPopeFavor()[1]);
        assert(playerBoard.getFaithTrack().getPopeFavor()[2]);
    }

    @Test
    @DisplayName("Ensure correct behaviour of game")
    void testNotEnd(){
            assert(!game.checkEndGame());
    }
}
