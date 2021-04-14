package it.polimi.ingsw;
import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.model.*;
import kotlin.Triple;
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
        game = new Game(0);
        String s = "player";
        ArrayList<String> st = new ArrayList<>();
        st.add(s);
        game.setup(st);
        playerBoard = game.getBoard(0);
    }

    @Test
    @DisplayName("Ensure correct behaviour of move action between two depots")
    void testMoveResourceFromDepot1ToDepot2(){
        try {
            playerBoard.getWarehouse().addDepot(1, Resources.Servants, 1);
            playerBoard.getWarehouse().addDepot(3, Resources.Stones, 2);
        }
        catch(Exception e) {e.printStackTrace();}
        ArrayList<Triplet<String,Integer,Integer>> choice = new ArrayList<>();
        choice.add(new Triplet<>("SE",1,2));
        try {
            game.moveAction(0, choice);
        }catch(Exception e) { e.printStackTrace(); }

        assert(!playerBoard.getWarehouse().getDepot(1).getKey().isPresent());
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
        choice.add(new Triplet<>("SE",2,3));
        choice.add(new Triplet<>("PI",3,2));
        choice.add(new Triplet<>("PI",3,2));
        try{
            game.moveAction(0,choice);
        }catch(Exception e){e.printStackTrace();}
        assert(playerBoard.getWarehouse().getDepot(2).getKey().get().equals(Resources.Stones) && playerBoard.getWarehouse().getDepot(2).getValue()==2);
        assert(playerBoard.getWarehouse().getDepot(3).getKey().get().equals(Resources.Servants) && playerBoard.getWarehouse().getDepot(3).getValue()==2);
    }

    @Test
    @DisplayName("Ensure correct behaviour of move action performed after market buy (with resources from the hand)")
    void testMoveAfterMarket(){
        //Getting the third row which contains one white marble, two yellow ones and a purple
        try {
            game.getMarketResources(0, true, 3, new ArrayList<>());
        }catch (Exception e){e.printStackTrace();}

        //Got in hand two Coins and one Servant
        ArrayList<Triplet<String,Integer,Integer>> choice = new ArrayList<>();
        choice.add(new Triplet<>("SE",0,3));
        choice.add(new Triplet<>("MO",0,2));
        choice.add(new Triplet<>("MO",0,2));
        try{
            game.moveAction(0,choice);
        }catch(Exception e){e.printStackTrace();}
        assert(playerBoard.getWarehouse().getDepot(2).getKey().get().equals(Resources.Coins) && playerBoard.getWarehouse().getDepot(2).getValue()==2);
        assert(playerBoard.getWarehouse().getDepot(3).getKey().get().equals(Resources.Servants) && playerBoard.getWarehouse().getDepot(3).getValue()==1);
    }

    @Test
    @DisplayName("Ensure correct behaviour of move action with multiple consecutive swaps")
    void testCanConsecutiveSwap(){
        try {
            playerBoard.getWarehouse().addDepot(2, Resources.Servants, 1);
            playerBoard.getWarehouse().addDepot(3, Resources.Stones, 2);
        }
        catch(Exception e) {e.printStackTrace();}
        ArrayList<Triplet<String,Integer,Integer>> choice = new ArrayList<>();
        choice.add(new Triplet<>("SE",2,1));
        choice.add(new Triplet<>("PI",3,2));
        choice.add(new Triplet<>("SE",1,2));
        choice.add(new Triplet<>("PI",3,2));
        choice.add(new Triplet<>("PI",2,3));
        choice.add(new Triplet<>("SE",2,1));
        choice.add(new Triplet<>("PI",2,3));

        try{
            game.moveAction(0,choice);
        }catch(Exception e){e.printStackTrace();}
        assert(playerBoard.getWarehouse().getDepot(3).getKey().get().equals(Resources.Stones) && playerBoard.getWarehouse().getDepot(3).getValue()==2);
        assert(playerBoard.getWarehouse().getDepot(1).getKey().get().equals(Resources.Servants) && playerBoard.getWarehouse().getDepot(1).getValue()==1);
    }

    @Test
    @DisplayName("Ensure correct behaviour of move action with consecutive swaps and null swaps")
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
            game.moveAction(0,choice);
        }catch(Exception e){e.printStackTrace();}
        assert(playerBoard.getWarehouse().getDepot(2).getKey().get().equals(Resources.Stones) && playerBoard.getWarehouse().getDepot(2).getValue()==2);
        assert(playerBoard.getWarehouse().getDepot(3).getKey().get().equals(Resources.Servants) && playerBoard.getWarehouse().getDepot(3).getValue()==1);
    }

    @Test
    @DisplayName("Ensure correct behaviour of move action with LeaderCards")
    void testCanLeaderCardSlot(){
        game = new Game(0);

        game.LCTestsetup();
        playerBoard = game.getBoard(0);

        //Getting rid of the WhiteMarbleAbility LC and ProductionPowerAbility LC (to have 2 LC active and without interference on cost)
        playerBoard.getLeadercardsplayed().remove(0);
        playerBoard.getLeadercardsplayed().remove(0);

        //Got played LC Slot in first position (Stones)
        try {
            playerBoard.getWarehouse().addDepot(3, Resources.Stones, 2);
            playerBoard.getLeadercardsplayed().get(0).getAbility().doUpdateSlot(Resources.Stones,1);
        }
        catch(Exception e) {e.printStackTrace();}

        ArrayList<Triplet<String,Integer,Integer>> choice = new ArrayList<>();
        choice.add(new Triplet<>("PI",4,3));
        try{
            game.moveAction(0,choice);
        }catch(Exception e) {e.printStackTrace();}
        assert(playerBoard.getWarehouse().getDepot(3).getKey().get().equals(Resources.Stones) && playerBoard.getWarehouse().getDepot(3).getValue()==3);
        assert(playerBoard.getLeadercardsplayed().get(0).getAbility().getStashedResources()==0);

        choice.clear();
        choice.add(new Triplet<>("PI",3,4));
        choice.add(new Triplet<>("PI",3,4));
        choice.add(new Triplet<>("PI",3,1));
        try{
            game.moveAction(0,choice);
        }catch(Exception e) {e.printStackTrace();}
        assert(playerBoard.getWarehouse().getDepot(1).getKey().get().equals(Resources.Stones) && playerBoard.getWarehouse().getDepot(1).getValue()==1);
        assert(!playerBoard.getWarehouse().getDepot(3).getKey().isPresent() && playerBoard.getWarehouse().getDepot(3).getValue()==0);
        assert(playerBoard.getLeadercardsplayed().get(0).getAbility().getStashedResources()==2);
    }



    @ParameterizedTest
    @MethodSource("provideSourceOfError")
    @DisplayName("Ensure correct behaviour of move action between two depots gone wrong")
    void CannotMoveForDifferentReasons(ArrayList<Triplet<String,Integer,Integer>> userChoice){
        try {
            playerBoard.getWarehouse().addDepot(1, Resources.Servants, 1);
            playerBoard.getWarehouse().addDepot(3, Resources.Stones, 2);
        }
        catch(Exception e) {e.printStackTrace();}
        assertThrows(Exception.class,()->{game.moveAction(0,userChoice);});
    }

    private static Stream<Arguments> provideSourceOfError() {
        return Stream.of(
                Arguments.of(new ArrayList<Triplet<String,Integer,Integer>>(){{add(new Triplet<>("MO",1,2));}}),
                Arguments.of(new ArrayList<Triplet<String,Integer,Integer>>(){{add(new Triplet<>("PI",2,3));}}),
                Arguments.of(new ArrayList<Triplet<String,Integer,Integer>>(){{add(new Triplet<>("SE",1,3));}})
        );
    }
}
