package it.polimi.ingsw;

import it.polimi.ingsw.commons.Colours;
import it.polimi.ingsw.commons.Resources;
import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class CountVictoryPointsTest {

    private Game game;
    private Board playerBoard;

    //Initialising Game and Board
    @BeforeEach
    void setup(){
        game = new Game(0);
        String s = "player";
        ArrayList<String> st = new ArrayList<>();
        st.add(s);
        game.setup(st);
        playerBoard = game.getBoard(0);
    }

    @Test
    @DisplayName("Ensure victory points are counted correctly for DC, LC, Pope Favor, Faith track and extra resources")
    void testCountVP(){
        HashMap<Resources,Integer> requiredRes1 = new HashMap<>();
        HashMap<Resources,Integer> producedRes1 = new HashMap<>();
        requiredRes1.put(Resources.Shields,1);
        producedRes1.put(Resources.Coins,1);

        //Adding one DC which counts for 5 VP
        DevelopmentCard DCP1 = new DevelopmentCard(1, Colours.Purple,5,new HashMap<>(),requiredRes1,producedRes1,1);
        try{
            playerBoard.getSlot(1).addCard(DCP1);
        }catch(Exception e) {e.printStackTrace();}

        //Adding one LC which counts for 5 VP
        LeaderCard LC = new LeaderCard(5,new HashMap<>(),new HashMap<>(),"ExtraSlotAbility",Resources.Shields,2);
        playerBoard.getLeaderCardsHand().clear();
        playerBoard.getLeaderCardsHand().put(0,LC);
        try {
            game.playLeaderCard(0, 1);
        }catch(Exception e){e.printStackTrace();}

        //Advancing Faith Track until the 8th space which provides Pope favor Tile
        for(int i=0;i<8;i++){
            playerBoard.getFaithTrack().advanceTrack();
        }
        playerBoard.getFaithTrack().setPopeFavor(1);

        //Adding 11 resources in various places
        try {
            playerBoard.getWarehouse().addDepot(3, Resources.Stones, 3);
            playerBoard.getLeaderCardsPlayed().get(0).getAbility().doUpdateSlot(Resources.Shields,2);
        }catch (Exception e){e.printStackTrace();}
        playerBoard.getStrongbox().addResource(Resources.Servants,6);

        //Expected VP: 5 for DC, 5 for LC, 2 for Faith track, 2 for Pope favor, 2 for extra resources
        //For a total of 16 VP
        game.countVictoryPoints();
        assert(playerBoard.getVictoryPoints()==16);
    }
}
