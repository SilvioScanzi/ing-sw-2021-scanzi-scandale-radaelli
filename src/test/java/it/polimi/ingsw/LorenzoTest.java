package it.polimi.ingsw;

import it.polimi.ingsw.commons.Triplet;
import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.commons.Colours;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class LorenzoTest {

    private Game game;

    @BeforeEach
    void setup(){
        game = new Game(0);
        String s = "player";
        ArrayList<String> st = new ArrayList<>();
        st.add(s);
        game.setup(st);
    }

    @Test
    @DisplayName("Ensure correct behaviour of all types of action token")
    void testActionToken(){

        for(int i=0;i<7;i++){
            game.activatedToken();
        }

        assert(game.getLorenzo().getBlackCross()==5);
        assert(game.getActionStack().getStack().size()==7);
        assert(game.getDevelopmentCardMarket().getFirstCard(Colours.Green,1).getVictoryPoints()==2);
        assert(game.getDevelopmentCardMarket().getFirstCard(Colours.Green,1).getVictoryPoints()==1);
        assertThrows(Exception.class,()->game.getDevelopmentCardMarket().getFirstCard(Colours.Green,1));
        assert(game.getDevelopmentCardMarket().getFirstCard(Colours.Yellow,1).getVictoryPoints()==2);
        assert(game.getDevelopmentCardMarket().getFirstCard(Colours.Yellow,1).getVictoryPoints()==1);
        assertThrows(Exception.class,()->game.getDevelopmentCardMarket().getFirstCard(Colours.Yellow,1));
        assert(game.getDevelopmentCardMarket().getFirstCard(Colours.Purple,1).getVictoryPoints()==2);
        assert(game.getDevelopmentCardMarket().getFirstCard(Colours.Purple,1).getVictoryPoints()==1);
        assertThrows(Exception.class,()->game.getDevelopmentCardMarket().getFirstCard(Colours.Purple,1));
        assert(game.getDevelopmentCardMarket().getFirstCard(Colours.Blue,1).getVictoryPoints()==2);
        assert(game.getDevelopmentCardMarket().getFirstCard(Colours.Blue,1).getVictoryPoints()==1);
        assertThrows(Exception.class,()->game.getDevelopmentCardMarket().getFirstCard(Colours.Blue,1));
        assert(!game.checkLorenzoWin());
    }

    @Test
    @DisplayName("Ensure remove cards in order")
    void testRemoveCards(){
        for(int i=0;i<3;i++){
            game.getDevelopmentCardMarket().deleteCards(Colours.Purple);
        }
        assertThrows(Exception.class,()->game.getDevelopmentCardMarket().getFirstCard(Colours.Purple,1));
        assert(game.getDevelopmentCardMarket().getFirstCard(Colours.Purple,2).getVictoryPoints()==6);
        assert(game.getDevelopmentCardMarket().getFirstCard(Colours.Purple,2).getVictoryPoints()==5);
        assertThrows(Exception.class,()->game.getDevelopmentCardMarket().getFirstCard(Colours.Purple,2));
        assert(game.getDevelopmentCardMarket().getFirstCard(Colours.Purple,3).getVictoryPoints()==12);
        assert(game.getDevelopmentCardMarket().getFirstCard(Colours.Purple,3).getVictoryPoints()==11);
        assert(game.getDevelopmentCardMarket().getFirstCard(Colours.Purple,3).getVictoryPoints()==10);
        assert(game.getDevelopmentCardMarket().getFirstCard(Colours.Purple,3).getVictoryPoints()==9);
        assertThrows(Exception.class,()->game.getDevelopmentCardMarket().getFirstCard(Colours.Purple,3));
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
            game.getDevelopmentCardMarket().deleteCards(Colours.Purple);
        }
        assert(game.checkLorenzoWin());
    }

    @Test
    @DisplayName("Ensure Lorenzo advances when discarding resources")
    void testLorenzoAdvance(){
        try {
            game.BuyMarketResourcesAction(0, true, 1, new ArrayList<>());
        }catch(Exception e){e.printStackTrace();}
        try {
            game.moveResources(0, new ArrayList<>());
        }catch(Exception e){e.printStackTrace();}
        assert(game.getLorenzo().getBlackCross()==3);
    }
}
