package it.polimi.ingsw;
import it.polimi.ingsw.commons.Resources;
import it.polimi.ingsw.commons.Triplet;
import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.*;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoveActionTest {

    private Game game;
    private Board playerBoard;

    @BeforeEach
    void setup(){
        game = new Game();
        String s = "player";
        ArrayList<String> st = new ArrayList<>();
        st.add(s);
        game.setup(st);
        playerBoard = game.getBoard(0);
    }

    //Move without leader cards

    @Test
    @DisplayName("Ensure correct behaviour of a simple move action between two depots")
    void testMoveResourceFromDepot1ToDepot2(){
        try {
            playerBoard.getWarehouse().addDepot(1, Resources.Servants, 1);
            playerBoard.getWarehouse().addDepot(3, Resources.Stones, 2);
        }
        catch(Exception e) {e.printStackTrace();}
        ArrayList<Triplet<String,Integer,Integer>> choice = new ArrayList<>();
        choice.add(new Triplet<>("SE",1,2));
        try {
            game.moveResources(0, choice);
        }catch(Exception e) { e.printStackTrace(); }

        assert(!playerBoard.getWarehouse().getDepot(1).getKey().isPresent());
        assert(playerBoard.getWarehouse().getDepot(2).getKey().isPresent());
        assert(playerBoard.getWarehouse().getDepot(3).getKey().isPresent());
        assert(playerBoard.getWarehouse().getDepot(2).getKey().get().equals(Resources.Servants) && playerBoard.getWarehouse().getDepot(2).getValue()==1);
    }

    @Test
    @DisplayName("Ensure correct behaviour of move action swapping entirely two depots")
    void testDepotSwap(){
        try {
            playerBoard.getWarehouse().addDepot(2, Resources.Servants, 2);
            playerBoard.getWarehouse().addDepot(3, Resources.Stones, 2);
        }
        catch(Exception e) {e.printStackTrace();}
        ArrayList<Triplet<String,Integer,Integer>> choice = new ArrayList<>();
        choice.add(new Triplet<>("SE",2,3));
        choice.add(new Triplet<>("PI",3,2));
        choice.add(new Triplet<>("SE",2,3));
        choice.add(new Triplet<>("PI",3,2));
        try{
            game.moveResources(0,choice);
        }catch(Exception e){e.printStackTrace();}
        
        assert(playerBoard.getWarehouse().getDepot(2).getKey().isPresent());
        assert(playerBoard.getWarehouse().getDepot(3).getKey().isPresent());
        assert(playerBoard.getWarehouse().getDepot(2).getKey().get().equals(Resources.Stones) && playerBoard.getWarehouse().getDepot(2).getValue()==2);
        assert(playerBoard.getWarehouse().getDepot(3).getKey().get().equals(Resources.Servants) && playerBoard.getWarehouse().getDepot(3).getValue()==2);
    }

    @Test
    @DisplayName("Ensure correct behaviour of move action performed after market buy (with resources from the hand)")
    void testMoveAfterMarket(){
        //Using the constructor with no random elements
        game = new Game(0);
        String s = "player";
        ArrayList<String> st = new ArrayList<>();
        st.add(s);
        game.setup(st);
        playerBoard = game.getBoard(0);
        //Getting the second row which contains one Grey marble, two yellow ones and a purple
        try {
            game.BuyMarketResourcesAction(0, true, 2, new ArrayList<>());
        }catch (Exception e){e.printStackTrace();}

        //Got in hand two Coins and one Servant
        ArrayList<Triplet<String,Integer,Integer>> choice = new ArrayList<>();
        choice.add(new Triplet<>("SE",0,3));
        choice.add(new Triplet<>("MO",0,2));
        choice.add(new Triplet<>("MO",0,2));
        choice.add(new Triplet<>("PI",0,1));
        try{
            game.moveResources(0,choice);
        }catch(Exception e){e.printStackTrace();}
        assert(playerBoard.getWarehouse().getDepot(1).getKey().isPresent());
        assert(playerBoard.getWarehouse().getDepot(2).getKey().isPresent());
        assert(playerBoard.getWarehouse().getDepot(3).getKey().isPresent());
        assert(playerBoard.getWarehouse().getDepot(1).getKey().get().equals(Resources.Stones) && playerBoard.getWarehouse().getDepot(1).getValue()==1);
        assert(playerBoard.getWarehouse().getDepot(2).getKey().get().equals(Resources.Coins) && playerBoard.getWarehouse().getDepot(2).getValue()==2);
        assert(playerBoard.getWarehouse().getDepot(3).getKey().get().equals(Resources.Servants) && playerBoard.getWarehouse().getDepot(3).getValue()==1);
    }

    @Test
    @DisplayName("Ensure correct behaviour of move action with consecutive swaps")
    void testCanConsecutiveSwapAndNullSwaps(){
        try {
            playerBoard.getWarehouse().addDepot(2, Resources.Servants, 1);
            playerBoard.getWarehouse().addDepot(3, Resources.Stones, 2);
        }
        catch(Exception e) {e.printStackTrace();}
        ArrayList<Triplet<String,Integer,Integer>> choice = new ArrayList<>();
        choice.add(new Triplet<>("SE",2,2));
        choice.add(new Triplet<>("SE",2,2));
        choice.add(new Triplet<>("SE",2,1));
        choice.add(new Triplet<>("SE",1,2));
        choice.add(new Triplet<>("SE",2,1));
        choice.add(new Triplet<>("PI",3,2));
        choice.add(new Triplet<>("PI",2,3));
        choice.add(new Triplet<>("PI",3,3));
        choice.add(new Triplet<>("PI",3,2));
        choice.add(new Triplet<>("PI",3,2));
        choice.add(new Triplet<>("SE",1,1));
        choice.add(new Triplet<>("SE",1,3));

        try{
            game.moveResources(0,choice);
        }catch(Exception e){e.printStackTrace();}

        assert(playerBoard.getWarehouse().getDepot(2).getKey().isPresent());
        assert(playerBoard.getWarehouse().getDepot(3).getKey().isPresent());
        assert(playerBoard.getWarehouse().getDepot(2).getKey().get().equals(Resources.Stones) && playerBoard.getWarehouse().getDepot(2).getValue()==2);
        assert(playerBoard.getWarehouse().getDepot(3).getKey().get().equals(Resources.Servants) && playerBoard.getWarehouse().getDepot(3).getValue()==1);
    }

    @Test
    @DisplayName("Ensure correct behaviour of move action with null swaps")
    void testNullSwaps(){
        try {
            playerBoard.getWarehouse().addDepot(2, Resources.Servants, 1);
            playerBoard.getWarehouse().addDepot(3, Resources.Stones, 2);
        }
        catch(Exception e) {e.printStackTrace();}
        ArrayList<Triplet<String,Integer,Integer>> choice = new ArrayList<>();
        choice.add(new Triplet<>("SE",2,1));
        choice.add(new Triplet<>("SE",1,2));
        choice.add(new Triplet<>("PI",3,2));
        choice.add(new Triplet<>("PI",3,2));
        choice.add(new Triplet<>("PI",2,3));
        choice.add(new Triplet<>("PI",2,3));

        try{
            game.moveResources(0,choice);
        }catch(Exception e){e.printStackTrace();}

        assert(playerBoard.getWarehouse().getDepot(2).getKey().isPresent());
        assert(playerBoard.getWarehouse().getDepot(3).getKey().isPresent());
        assert(playerBoard.getWarehouse().getDepot(2).getKey().get().equals(Resources.Servants) && playerBoard.getWarehouse().getDepot(2).getValue()==1);
        assert(playerBoard.getWarehouse().getDepot(3).getKey().get().equals(Resources.Stones) && playerBoard.getWarehouse().getDepot(3).getValue()==2);
    }

    @ParameterizedTest
    @MethodSource("provideSourceOfError")
    @DisplayName("Ensure incorrect move action throws an exception")
    void testCannotMoveForDifferentReasons(ArrayList<Triplet<String,Integer,Integer>> userChoice){
        try {
            playerBoard.getWarehouse().addDepot(1, Resources.Servants, 1);
            playerBoard.getWarehouse().addDepot(3, Resources.Stones, 2);
        }
        catch(Exception e) {e.printStackTrace();}
        assertThrows(Exception.class,()-> game.moveResources(0,userChoice));
    }

    private static Stream<Arguments> provideSourceOfError() {
        return Stream.of(
                Arguments.of(new ArrayList<Triplet<String,Integer,Integer>>(){{add(new Triplet<>("MO",1,2));}}),
                Arguments.of(new ArrayList<Triplet<String,Integer,Integer>>(){{add(new Triplet<>("PI",2,3));}}),
                Arguments.of(new ArrayList<Triplet<String,Integer,Integer>>(){{add(new Triplet<>("SE",1,3));}}),
                Arguments.of(new ArrayList<Triplet<String,Integer,Integer>>(){{add(new Triplet<>("PI",3,2));}})
        );
    }

    /*@Test
    @DisplayName("Ensure exception is thrown when resources are left in hand")
    void testResourcesLeftInHand(){
        game = new Game(0);
        String s = "player";
        ArrayList<String> st = new ArrayList<>();
        st.add(s);
        game.setup(st);
        playerBoard = game.getBoard(0);
        //Getting the second row which contains one Grey marble, two yellow ones and a purple
        try {
            game.BuyMarketResourcesAction(0, true, 2, new ArrayList<>());
        }catch (Exception e){e.printStackTrace();}

        //Got in hand two Coins and one Servant
        ArrayList<Triplet<String,Integer,Integer>> choice = new ArrayList<>();
        choice.add(new Triplet<>("SE",0,3));
        choice.add(new Triplet<>("MO",0,2));
        choice.add(new Triplet<>("MO",0,2));

        assertThrows(Exception.class,()-> game.moveResources(0,choice));
        assert(playerBoard.getHand().size()==1 && playerBoard.getHand().get(0).equals(Resources.Stones));
    }*/

    @Test
    @DisplayName("Ensure discard remaining resources is correct")
    void testDiscard(){
        game = new Game();
        String s = "player1";
        String p = "player2";
        ArrayList<String> st = new ArrayList<>();
        st.add(s);
        st.add(p);
        game.setup(st);
        playerBoard = game.getBoard(0);
        Board playerBoard2 = game.getBoard(1);
        playerBoard.getHand().add(Resources.Coins);
        playerBoard.getHand().add(Resources.Shields);
        playerBoard.getHand().add(Resources.Stones);
        playerBoard.getHand().add(Resources.Servants);
        game.discardRemainingResources(0);
        assert(playerBoard.getHand().size()==0);
        assert(playerBoard2.getFaithTrack().getFaithMarker() == 4);
    }

    //Leader Cards test

    @Test
    @DisplayName("Ensure correct behaviour of move action with LeaderCards which provide extra slot for resources")
    void testCanLeaderCardSlot(){
        //Adding a new LCCard which provides extra slots for stones
        LeaderCard LCSlot = new LeaderCard(0,new HashMap<>(),new HashMap<>(),"ExtraSlotAbility",Resources.Stones,2);
        playerBoard.getLeaderCardsHand().clear();
        playerBoard.getLeaderCardsHand().put(0,LCSlot);
        try {
            game.playLeaderCard(0, 1);
        }catch(Exception e){e.printStackTrace();}

        try {
            playerBoard.getWarehouse().addDepot(3, Resources.Stones, 2);
            playerBoard.getLeaderCardsPlayed().get(0).getAbility().doUpdateSlot(Resources.Stones,1);
        }
        catch(Exception e) {e.printStackTrace();}

        ArrayList<Triplet<String,Integer,Integer>> choice = new ArrayList<>();
        choice.add(new Triplet<>("PI",4,3));
        try{
            game.moveResources(0,choice);
        }catch(Exception e) {e.printStackTrace();}
        assert(playerBoard.getWarehouse().getDepot(3).getKey().get().equals(Resources.Stones) && playerBoard.getWarehouse().getDepot(3).getValue()==3);
        assert(playerBoard.getLeaderCardsPlayed().get(0).getAbility().getStashedResources()==0);

        choice.clear();
        choice.add(new Triplet<>("PI",3,4));
        choice.add(new Triplet<>("PI",3,4));
        choice.add(new Triplet<>("PI",3,1));
        try{
            game.moveResources(0,choice);
        }catch(Exception e) {e.printStackTrace();}

        assert(playerBoard.getWarehouse().getDepot(1).getKey().isPresent());
        assert(playerBoard.getWarehouse().getDepot(1).getKey().get().equals(Resources.Stones) && playerBoard.getWarehouse().getDepot(1).getValue()==1);
        assert(!playerBoard.getWarehouse().getDepot(3).getKey().isPresent() && playerBoard.getWarehouse().getDepot(3).getValue()==0);
        assert(playerBoard.getLeaderCardsPlayed().get(0).getAbility().getStashedResources()==2);
    }
}
