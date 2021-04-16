package it.polimi.ingsw;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.Colours;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class LorenzoTest {

    private Game game;

    @BeforeEach
    void setup(){
        game = new Game(0);
    }

    @Test
    @DisplayName("Ensure correct behaviour of all types of action token")
    void testActionToken(){

        for(int i=0;i<7;i++){
            game.activatedToken();
        }

        assert(game.getLorenzo().getBlackCross()==5);
        assert(game.getActionStack().getStack().size()==7);
        assert(game.getDevelopmentcardmarket().getFirstCard(Colours.Green,1).getvictoryPoints()==2);
        assert(game.getDevelopmentcardmarket().getFirstCard(Colours.Green,1).getvictoryPoints()==1);
        assertThrows(Exception.class,()->game.getDevelopmentcardmarket().getFirstCard(Colours.Green,1));
        assert(game.getDevelopmentcardmarket().getFirstCard(Colours.Yellow,1).getvictoryPoints()==2);
        assert(game.getDevelopmentcardmarket().getFirstCard(Colours.Yellow,1).getvictoryPoints()==1);
        assertThrows(Exception.class,()->game.getDevelopmentcardmarket().getFirstCard(Colours.Yellow,1));
        assert(game.getDevelopmentcardmarket().getFirstCard(Colours.Purple,1).getvictoryPoints()==2);
        assert(game.getDevelopmentcardmarket().getFirstCard(Colours.Purple,1).getvictoryPoints()==1);
        assertThrows(Exception.class,()->game.getDevelopmentcardmarket().getFirstCard(Colours.Purple,1));
        assert(game.getDevelopmentcardmarket().getFirstCard(Colours.Blue,1).getvictoryPoints()==2);
        assert(game.getDevelopmentcardmarket().getFirstCard(Colours.Blue,1).getvictoryPoints()==1);
        assertThrows(Exception.class,()->game.getDevelopmentcardmarket().getFirstCard(Colours.Blue,1));
        assert(!game.checkLorenzoWin());
    }

    @Test
    @DisplayName("Ensure remove cards in order")
    void testRemoveCards(){
        for(int i=0;i<3;i++){
            game.getDevelopmentcardmarket().deleteCards(Colours.Purple);
        }
        assertThrows(Exception.class,()->game.getDevelopmentcardmarket().getFirstCard(Colours.Purple,1));
        assert(game.getDevelopmentcardmarket().getFirstCard(Colours.Purple,2).getvictoryPoints()==6);
        assert(game.getDevelopmentcardmarket().getFirstCard(Colours.Purple,2).getvictoryPoints()==5);
        assertThrows(Exception.class,()->game.getDevelopmentcardmarket().getFirstCard(Colours.Purple,2));
        assert(game.getDevelopmentcardmarket().getFirstCard(Colours.Purple,3).getvictoryPoints()==12);
        assert(game.getDevelopmentcardmarket().getFirstCard(Colours.Purple,3).getvictoryPoints()==11);
        assert(game.getDevelopmentcardmarket().getFirstCard(Colours.Purple,3).getvictoryPoints()==10);
        assert(game.getDevelopmentcardmarket().getFirstCard(Colours.Purple,3).getvictoryPoints()==9);
        assertThrows(Exception.class,()->game.getDevelopmentcardmarket().getFirstCard(Colours.Purple,3));
    }

    @Test
    @DisplayName("Ensure Lorenzo wins thanks to blackCross")
    void testLorenzoWinBC(){
        for(int i=0;i<24;i++){
            game.getLorenzo().advanceBlackCross();
        }
        assert(game.checkLorenzoWin());
    }

    @Test
    @DisplayName("Ensure Lorenzo wins thanks to empty DC deck")
    void testLorenzoWinDC(){
        for(int i=0;i<6;i++){
            game.getDevelopmentcardmarket().deleteCards(Colours.Purple);
        }
        assert(game.checkLorenzoWin());
    }

}
