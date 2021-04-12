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
    void MoveResourceFromDepot1ToDepot2(){
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

    @ParameterizedTest
    @MethodSource("provideSourceOfError")
    @DisplayName("Ensure correct behaviour of move action between two depots")
    void CannotMoveForDifferentReasons(ArrayList<Triplet<String,Integer,Integer>> userChoice){
        try {
            playerBoard.getWarehouse().addDepot(1, Resources.Servants, 1);
            playerBoard.getWarehouse().addDepot(3, Resources.Stones, 2);
        }
        catch(Exception e) {e.printStackTrace();}
        //System.out.println(playerBoard.getWarehouse().toString());
        assertThrows(Exception.class,()->{game.moveAction(0,userChoice);});
        //System.out.println(playerBoard.getWarehouse().toString());
    }

    private static Stream<Arguments> provideSourceOfError() {
        return Stream.of(
                Arguments.of(new ArrayList<Triplet<String,Integer,Integer>>(){{add(new Triplet<>("MO",1,2));}}),
                Arguments.of(new ArrayList<Triplet<String,Integer,Integer>>(){{add(new Triplet<>("PI",2,3));}}),
                Arguments.of(new ArrayList<Triplet<String,Integer,Integer>>(){{add(new Triplet<>("SE",1,3));}})
        );
    }
}
