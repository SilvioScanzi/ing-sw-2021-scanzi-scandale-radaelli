package it.polimi.ingsw;

import it.polimi.ingsw.commons.Colours;
import it.polimi.ingsw.commons.Pair;
import it.polimi.ingsw.commons.Resources;
import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ActivateProductionTest {

    private Game game;
    private Board playerBoard;

    //Initialising Game and Board, playing DC cards for production
    @BeforeEach
    void setup(){
        game = new Game();
        String s = "player";
        ArrayList<String> st = new ArrayList<>();
        st.add(s);
        game.setup(st);
        playerBoard = game.getBoard(0);
        HashMap<Resources,Integer> requiredRes1 = new HashMap<>();
        HashMap<Resources,Integer> producedRes1 = new HashMap<>();
        requiredRes1.put(Resources.Shields,1);
        producedRes1.put(Resources.Coins,1);
        DevelopmentCard DCP1 = new DevelopmentCard(1, Colours.Purple,5,new HashMap<>(),requiredRes1,producedRes1,1);
        HashMap<Resources,Integer> requiredRes2 = new HashMap<>();
        HashMap<Resources,Integer> producedRes2 = new HashMap<>();
        requiredRes2.put(Resources.Shields,1);
        producedRes2.put(Resources.Servants,2);
        DevelopmentCard DCG1 = new DevelopmentCard(1,Colours.Green,5,new HashMap<>(),requiredRes2,producedRes2,2);
        HashMap<Resources,Integer> requiredRes3 = new HashMap<>();
        HashMap<Resources,Integer> producedRes3 = new HashMap<>();
        requiredRes3.put(Resources.Shields,1);
        producedRes3.put(Resources.Stones,3);
        DevelopmentCard DCY1 = new DevelopmentCard(1,Colours.Yellow,5,new HashMap<>(),requiredRes3,producedRes3,3);
        try{
            playerBoard.getSlot(1).addCard(DCP1);
            playerBoard.getSlot(2).addCard(DCG1);
            playerBoard.getSlot(3).addCard(DCY1);
        }catch(Exception e){e.printStackTrace();}
        try{
            playerBoard.getWarehouse().addDepot(1,Resources.Servants,1);
            playerBoard.getWarehouse().addDepot(2,Resources.Stones,1);
            playerBoard.getWarehouse().addDepot(3,Resources.Shields,3);
        }catch(Exception e){e.printStackTrace();}
    }

    @Test
    @DisplayName("Ensure correct activation of all productions in the board")
    void testCanActivateProduction(){
        HashMap<Integer,ArrayList<Pair<String,Integer>>> userChoice = new HashMap<>();
        userChoice.put(1,new ArrayList<Pair<String,Integer>>(){{add(new Pair<>("SC",3));}});
        userChoice.put(2,new ArrayList<Pair<String,Integer>>(){{add(new Pair<>("SC",3));}});
        userChoice.put(3,new ArrayList<Pair<String,Integer>>(){{add(new Pair<>("SC",3));}});
        userChoice.put(6,new ArrayList<Pair<String,Integer>>(){{add(new Pair<>("SE",1));add(new Pair<>("PI",2));add(new Pair<>("MO",-1));}});

        try {
            game.activateProductionAction(0, userChoice);
        }catch(Exception e){e.printStackTrace();}

        assert(playerBoard.getStrongbox().getResource(Resources.Coins)==2);
        assert(playerBoard.getStrongbox().getResource(Resources.Servants)==2);
        assert(playerBoard.getStrongbox().getResource(Resources.Stones)==3);
        assert(playerBoard.getFaithTrack().getFaithMarker()==6);
    }

    @Test
    @DisplayName("Ensure lack of resources impedes the action")
    void testCannotActivateProductionBecauseOfResources(){
        HashMap<Integer,ArrayList<Pair<String,Integer>>> userChoice = new HashMap<>();
        userChoice.put(1,new ArrayList<Pair<String,Integer>>(){{add(new Pair<>("SC",3));}});
        userChoice.put(2,new ArrayList<Pair<String,Integer>>(){{add(new Pair<>("SC",3));}});
        userChoice.put(3,new ArrayList<Pair<String,Integer>>(){{add(new Pair<>("SC",3));}});
        userChoice.put(6,new ArrayList<Pair<String,Integer>>(){{add(new Pair<>("SE",1));add(new Pair<>("PI",2));add(new Pair<>("MO",-1));}});
        try{
            playerBoard.getWarehouse().subDepot(1,1);
        }catch(Exception e){e.printStackTrace();}

        assertThrows(Exception.class,()-> game.activateProductionAction(0, userChoice));
    }

    @Test
    @DisplayName("Ensure wrong request impedes the action")
    void testCannotActivateProductionBecauseOfRequest(){
        HashMap<Integer,ArrayList<Pair<String,Integer>>> userChoice = new HashMap<>();
        userChoice.put(6,new ArrayList<Pair<String,Integer>>(){{add(new Pair<>("SE",1));add(new Pair<>("MO",-1));}});
        assertThrows(Exception.class,()-> game.activateProductionAction(0, userChoice));

        userChoice.clear();
        userChoice.put(6,new ArrayList<Pair<String,Integer>>(){{add(new Pair<>("SE",1));add(new Pair<>("MO",2));add(new Pair<>("MO",2));add(new Pair<>("MO",-1));}});
        assertThrows(Exception.class,()-> game.activateProductionAction(0, userChoice));

        userChoice.clear();
        userChoice.put(15,new ArrayList<Pair<String,Integer>>(){{add(new Pair<>("SE",1));add(new Pair<>("MO",2));add(new Pair<>("MO",2));add(new Pair<>("MO",-1));}});
        assertThrows(Exception.class,()-> game.activateProductionAction(0, userChoice));
    }

    @Test
    @DisplayName("Ensure Correct activation with resources got from LC")
    void testCanActivateWithLC(){
        LeaderCard LCSlot = new LeaderCard(0,new HashMap<>(),new HashMap<>(),"ExtraSlotAbility",Resources.Shields,2);
        playerBoard.getLeaderCardsHand().clear();
        playerBoard.getLeaderCardsHand().put(0,LCSlot);
        try {
            game.playLeaderCard(0, 1);
        }catch(Exception e){e.printStackTrace();}
        playerBoard.getLeaderCardsPlayed().get(0).getAbility().doUpdateSlot(Resources.Shields,2);

        HashMap<Integer,ArrayList<Pair<String,Integer>>> userChoice = new HashMap<>();
        userChoice.put(1,new ArrayList<Pair<String,Integer>>(){{add(new Pair<>("SC",4));}});
        userChoice.put(6,new ArrayList<Pair<String,Integer>>(){{add(new Pair<>("SE",1));add(new Pair<>("SC",4));add(new Pair<>("MO",-1));}});

        try {
            game.activateProductionAction(0, userChoice);
        }catch(Exception e){e.printStackTrace();}

        assert(playerBoard.getStrongbox().getResource(Resources.Coins)==2);
    }

    @Test
    @DisplayName("Ensure correct activation of LC")
    void testCanActivateLCProduction(){
        LeaderCard LCProduction = new LeaderCard(0,new HashMap<>(),new HashMap<>(),"ProductionPowerAbility",Resources.Servants,0);
        playerBoard.getLeaderCardsHand().clear();
        playerBoard.getLeaderCardsHand().put(0,LCProduction);
        try {
            game.playLeaderCard(0, 1);
        }catch(Exception e){e.printStackTrace();}
        HashMap<Integer,ArrayList<Pair<String,Integer>>> userChoice = new HashMap<>();
        userChoice.put(4,new ArrayList<Pair<String,Integer>>(){{add(new Pair<>("SE",1));add(new Pair<>("MO",-1));}});

        try {
            game.activateProductionAction(0, userChoice);
        }catch(Exception e){e.printStackTrace();}

        assert(playerBoard.getStrongbox().getResource(Resources.Coins)==1);
        assert(playerBoard.getFaithTrack().getFaithMarker()==1);
    }

    @Test
    @DisplayName("Ensure exception is thrown when trying to produce with a LC that has a different ability")
    void testWrongLC(){
        LeaderCard LCSlot = new LeaderCard(0,new HashMap<>(),new HashMap<>(),"ExtraSlotAbility",Resources.Servants,0);
        playerBoard.getLeaderCardsHand().clear();
        playerBoard.getLeaderCardsHand().put(0,LCSlot);
        try {
            game.playLeaderCard(0, 1);
        }catch(Exception e){e.printStackTrace();}
        HashMap<Integer,ArrayList<Pair<String,Integer>>> userChoice = new HashMap<>();
        userChoice.put(4,new ArrayList<Pair<String,Integer>>(){{add(new Pair<>("SE",1));add(new Pair<>("MO",-1));}});

        assertThrows(Exception.class,()-> game.activateProductionAction(0, userChoice));
    }
}
