package it.polimi.ingsw;

import it.polimi.ingsw.commons.Colours;
import it.polimi.ingsw.commons.Pair;
import it.polimi.ingsw.commons.Resources;
import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.*;



public class LeaderCardPlayAndDiscardTest {

    private Game game;
    private Board playerBoard;

    //Initialising Game and Board
    @BeforeEach
    void setup(){
        game = new Game();
        String s = "player";
        ArrayList<String> st = new ArrayList<>();
        st.add(s);
        game.setup(st);
        playerBoard = game.getBoard(0);
    }

    @Test
    @DisplayName("Ensure Leader Card played with enough DC played")
    void testCanPlayLCardWithDC(){
        DevelopmentCard DCP1 = new DevelopmentCard(1, Colours.Purple,5,new HashMap<>(),new HashMap<>(),new HashMap<>(),0);
        DevelopmentCard DCP2 = new DevelopmentCard(2,Colours.Green,5,new HashMap<>(),new HashMap<>(),new HashMap<>(),0);
        DevelopmentCard DCY1 = new DevelopmentCard(1,Colours.Yellow,5,new HashMap<>(),new HashMap<>(),new HashMap<>(),0);
        try{
            playerBoard.getSlot(1).addCard(DCP1);
            playerBoard.getSlot(1).addCard(DCP2);
            playerBoard.getSlot(3).addCard(DCY1);
        }catch (Exception e){e.printStackTrace();}
        HashMap<Colours, Pair<Integer,Integer>> rc = new HashMap<Colours,Pair<Integer,Integer>>(){{put(Colours.Purple,new Pair<>(1,0)); put(Colours.Green,new Pair<>(1,2)); put(Colours.Yellow,new Pair<>(1,0));}};
        HashMap<Resources,Integer> rr = new HashMap<>();
        LeaderCard LC = new LeaderCard(5,rc,rr,"WhiteMarbleAbility",Resources.Shields,0);
        playerBoard.getLeaderCardsHand().clear();
        playerBoard.getLeaderCardsHand().put(0,LC);
        try {
            game.playLeaderCard(0, 1);
        }catch(Exception e){e.printStackTrace();}
        assert(playerBoard.getLeaderCardsPlayed().size()==1 && playerBoard.getLeaderCardsPlayed().get(0).getRequiredResources().equals(rr) &&
                playerBoard.getLeaderCardsPlayed().get(0).getRequiredColours().equals(rc) &&
                playerBoard.getLeaderCardsPlayed().get(0).getVictoryPoints()==5);
    }

    @Test
    @DisplayName("Ensure Leader Card played with enough Resources")
    void testCanPlayLCardWithResources(){
        HashMap<Colours,Pair<Integer,Integer>> rc = new HashMap<>();
        HashMap<Resources,Integer> rr = new HashMap<Resources,Integer>(){{put(Resources.Shields,5);}};
        try {
            playerBoard.getWarehouse().addDepot(3,Resources.Shields,3);
        }catch(Exception e) {e.printStackTrace();}
        playerBoard.getStrongbox().addResource(Resources.Shields,2);
        LeaderCard LC = new LeaderCard(3,rc,rr,"ExtraSlotAbility",Resources.Servants,2);
        playerBoard.getLeaderCardsHand().clear();
        playerBoard.getLeaderCardsHand().put(0,LC);
        try {
            game.playLeaderCard(0, 1);
        }catch(Exception e){e.printStackTrace();}
        assert(playerBoard.getLeaderCardsPlayed().size()==1 && playerBoard.getLeaderCardsPlayed().get(0).getRequiredResources().equals(rr) &&
                playerBoard.getLeaderCardsPlayed().get(0).getRequiredColours().equals(rc) &&
                playerBoard.getLeaderCardsPlayed().get(0).getVictoryPoints()==3);
    }

    @Test
    @DisplayName("Ensure Leader Card played with enough Resources and DC")
    void testCanPlayLCardWithDCAndResources(){
        DevelopmentCard DCP1 = new DevelopmentCard(1,Colours.Purple,5,new HashMap<>(),new HashMap<>(),new HashMap<>(),0);
        DevelopmentCard DCP2 = new DevelopmentCard(1,Colours.Purple,5,new HashMap<>(),new HashMap<>(),new HashMap<>(),0);
        DevelopmentCard DCY1 = new DevelopmentCard(1,Colours.Yellow,5,new HashMap<>(),new HashMap<>(),new HashMap<>(),0);
        try{
            playerBoard.getSlot(1).addCard(DCP1);
            playerBoard.getSlot(2).addCard(DCP2);
            playerBoard.getSlot(3).addCard(DCY1);
        }catch (Exception e){e.printStackTrace();}
        HashMap<Colours,Pair<Integer,Integer>> rc = new HashMap<Colours,Pair<Integer,Integer>>(){{put(Colours.Purple,new Pair<>(2,0)); put(Colours.Yellow,new Pair<>(1,0));}};
        HashMap<Resources,Integer> rr = new HashMap<Resources,Integer>(){{put(Resources.Shields,5);}};
        try {
            playerBoard.getWarehouse().addDepot(3,Resources.Shields,3);
        }catch(Exception e) {e.printStackTrace();}
        playerBoard.getStrongbox().addResource(Resources.Shields,2);
        LeaderCard LC = new LeaderCard(5,rc,rr,"DiscountAbility",Resources.Shields,-1);
        playerBoard.getLeaderCardsHand().clear();
        playerBoard.getLeaderCardsHand().put(0,LC);
        try {
            game.playLeaderCard(0, 1);
        }catch(Exception e){e.printStackTrace();}
        assert(playerBoard.getLeaderCardsPlayed().size()==1 && playerBoard.getLeaderCardsPlayed().get(0).getRequiredResources().equals(rr) &&
                playerBoard.getLeaderCardsPlayed().get(0).getRequiredColours().equals(rc) &&
                playerBoard.getLeaderCardsPlayed().get(0).getVictoryPoints()==5);
    }

    @Test
    @DisplayName("Ensure player cannot play a card without meeting its requirements")
    void testCannotPlayDC() {

        //Lack of resources
        HashMap<Colours, Pair<Integer, Integer>> rc = new HashMap<>();
        HashMap<Resources, Integer> rr = new HashMap<Resources, Integer>() {{
            put(Resources.Shields, 5);
        }};
        try {
            playerBoard.getWarehouse().addDepot(3, Resources.Shields, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        playerBoard.getStrongbox().addResource(Resources.Shields, 2);
        LeaderCard LC = new LeaderCard(3, rc, rr, "ExtraSlotAbility", Resources.Servants, 2);
        playerBoard.getLeaderCardsHand().clear();
        playerBoard.getLeaderCardsHand().put(0,LC);
        assertThrows(Exception.class, () -> game.playLeaderCard(0, 1));

        //Lack of DC
        DevelopmentCard DCP1 = new DevelopmentCard(1, Colours.Purple, 5, new HashMap<>(), new HashMap<>(), new HashMap<>(), 0);
        DevelopmentCard DCY1 = new DevelopmentCard(1, Colours.Yellow, 5, new HashMap<>(), new HashMap<>(), new HashMap<>(), 0);
        try {
            playerBoard.getSlot(1).addCard(DCP1);
            playerBoard.getSlot(3).addCard(DCY1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rc = new HashMap<Colours, Pair<Integer, Integer>>() {{
            put(Colours.Purple, new Pair<>(2, 0));
            put(Colours.Yellow, new Pair<>(1, 0));
        }};
        rr = new HashMap<>();
        LC = new LeaderCard(5, rc, rr, "WhiteMarbleAbility", Resources.Shields, 0);
        playerBoard.getLeaderCardsHand().clear();
        playerBoard.getLeaderCardsHand().put(0,LC);
        assertThrows(Exception.class, () -> game.playLeaderCard(0, 1));
    }

    @Test
    @DisplayName("Ensure correct behaviour of the discard LC action")
    void testCanDiscard(){
        try {
            game.discardLeaderCard(0, 1);
        }catch (Exception e){e.printStackTrace();}
        assert(playerBoard.getFaithTrack().getFaithMarker()==1);
        assert(playerBoard.getLeaderCardsHand().size()==3);
        assert(playerBoard.getLeaderCardsPlayed().size()==0);
    }

    @Test
    @DisplayName("Ensure wrong index results in exception")
    void testCannotDiscard(){
        assertThrows(Exception.class, () -> game.discardLeaderCard(0, 5));
        assertThrows(Exception.class, () -> game.discardLeaderCard(0, -1));
    }

    @Test
    @DisplayName("Ensure discard grants popeFavor")
    void testDiscardFavor(){
        for(int i=0;i<7;i++) {
            playerBoard.getFaithTrack().advanceTrack();
        }
        assert(playerBoard.getFaithTrack().getFaithMarker()==7);
        try {
            game.discardLeaderCard(0, 1);
        }catch (Exception e){e.printStackTrace();}
        assert(playerBoard.getFaithTrack().getFaithMarker()==8);
        assert(playerBoard.getLeaderCardsHand().size()==3);
        assert(playerBoard.getLeaderCardsPlayed().size()==0);
        assert(playerBoard.getFaithTrack().getPopeFavor()[0] && !playerBoard.getFaithTrack().getPopeFavor()[1] && !playerBoard.getFaithTrack().getPopeFavor()[2]);
    }

    @Test
    @DisplayName("Ensure correct discard of LC at the start of the game")
    void testStartDiscard(){
        HashMap<Colours,Pair<Integer,Integer>> rc = new HashMap<>();
        HashMap<Resources,Integer> rr = new HashMap<>();

        LeaderCard LC1 = new LeaderCard(1,rc,rr,"WhiteMarbleAbility",Resources.Shields,0);
        LeaderCard LC2 = new LeaderCard(2,rc,rr,"DiscountAbility",Resources.Stones,-1);
        LeaderCard LC3 = new LeaderCard(3,rc,rr,"ExtraSlotAbility",Resources.Servants,0);
        LeaderCard LC4 = new LeaderCard(4,rc,rr,"ProductionPowerAbility",Resources.Coins,-1);

        playerBoard.getLeaderCardsHand().clear();
        playerBoard.getLeaderCardsHand().put(0,LC1);
        playerBoard.getLeaderCardsHand().put(1,LC2);
        playerBoard.getLeaderCardsHand().put(2,LC3);
        playerBoard.getLeaderCardsHand().put(3,LC4);

        int[] selection = {1,2};
        try{
            game.discardSelectedLC(0,selection);
        }catch(Exception e) {e.printStackTrace();}

        assert(playerBoard.getLeaderCardsHand().get(0).getAbility().getResType().equals(Resources.Servants));
        assert(playerBoard.getLeaderCardsHand().get(0).getVictoryPoints()==3);
        assert(playerBoard.getLeaderCardsHand().get(1).getAbility().getResType().equals(Resources.Coins));
        assert(playerBoard.getLeaderCardsHand().get(1).getVictoryPoints()==4);
    }

    @Test
    @DisplayName("Ensure correct discard of LC at the start of the game gone wrong")
    void testStartDiscardIndexOutOfBound(){
        HashMap<Colours,Pair<Integer,Integer>> rc = new HashMap<>();
        HashMap<Resources,Integer> rr = new HashMap<>();

        LeaderCard LC1 = new LeaderCard(1,rc,rr,"WhiteMarbleAbility",Resources.Shields,0);
        LeaderCard LC2 = new LeaderCard(2,rc,rr,"DiscountAbility",Resources.Stones,-1);
        LeaderCard LC3 = new LeaderCard(3,rc,rr,"ExtraSlotAbility",Resources.Servants,0);
        LeaderCard LC4 = new LeaderCard(4,rc,rr,"ProductionPowerAbility",Resources.Coins,-1);

        playerBoard.getLeaderCardsHand().clear();
        playerBoard.getLeaderCardsHand().put(0,LC1);
        playerBoard.getLeaderCardsHand().put(1,LC2);
        playerBoard.getLeaderCardsHand().put(2,LC3);
        playerBoard.getLeaderCardsHand().put(3,LC4);

        int[] selection = {6,2};
        assertThrows(Exception.class, () -> game.discardSelectedLC(0,selection));
    }

    @Test
    @DisplayName("Ensure correct discard of LC at the start of the game gone wrong")
    void testStartDiscardTooManyDiscards(){
        HashMap<Colours,Pair<Integer,Integer>> rc = new HashMap<>();
        HashMap<Resources,Integer> rr = new HashMap<>();

        LeaderCard LC1 = new LeaderCard(1,rc,rr,"WhiteMarbleAbility",Resources.Shields,0);
        LeaderCard LC2 = new LeaderCard(2,rc,rr,"DiscountAbility",Resources.Stones,-1);
        LeaderCard LC3 = new LeaderCard(3,rc,rr,"ExtraSlotAbility",Resources.Servants,0);
        LeaderCard LC4 = new LeaderCard(4,rc,rr,"ProductionPowerAbility",Resources.Coins,-1);

        playerBoard.getLeaderCardsHand().clear();
        playerBoard.getLeaderCardsHand().put(0,LC1);
        playerBoard.getLeaderCardsHand().put(1,LC2);
        playerBoard.getLeaderCardsHand().put(2,LC3);
        playerBoard.getLeaderCardsHand().put(3,LC4);

        int[] selection = {69,42,119};
        assertThrows(Exception.class, () -> game.discardSelectedLC(0,selection));
    }
}

