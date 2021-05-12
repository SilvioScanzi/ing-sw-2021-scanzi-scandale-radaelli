package it.polimi.ingsw;

import it.polimi.ingsw.commons.Pair;
import it.polimi.ingsw.commons.Resources;
import it.polimi.ingsw.commons.Triplet;
import it.polimi.ingsw.model.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class WarehouseTest {

    private Warehouse WH;

    @BeforeEach
    void setup(){
        WH = new Warehouse();
    }

    @Test
    @DisplayName("Ensure correct behaviour when adding resources into warehouse")
    void testCanDeposit(){
        try{
            WH.addDepot(1, Resources.Stones,1);
            WH.addDepot(2,Resources.Shields,2);
            WH.addDepot(3,Resources.Servants,3);
        }catch(Exception e) {e.printStackTrace();}
        assert(WH.getDepot(1).getKey().isPresent());
        assert(WH.getDepot(1).getKey().get().equals(Resources.Stones) && WH.getDepot(1).getValue()==1);
        assert(WH.getDepot(2).getKey().isPresent());
        assert(WH.getDepot(2).getKey().get().equals(Resources.Shields) && WH.getDepot(2).getValue()==2);
        assert(WH.getDepot(3).getKey().isPresent());
        assert(WH.getDepot(3).getKey().get().equals(Resources.Servants) && WH.getDepot(3).getValue()==3);
    }

    @Test
    @DisplayName("Ensure correct behaviour of getDepot when Depot number is incorrect")
    void testIncorrectGet(){
        assertThrows(Exception.class,()->WH.getDepot(42));
        assertThrows(Exception.class,()->WH.getDepot(-33));
    }

    @ParameterizedTest
    @MethodSource("provideSourceOfErrorAdd")
    @DisplayName("Ensure incorrect add throws an exception")
    void CannotAddForDifferentReasons(ArrayList<Triplet<Integer,Resources,Integer>> PreliminaryMoves, Triplet<Integer,Resources,Integer> error){
        for(Triplet<Integer,Resources,Integer> T : PreliminaryMoves){
            try{
                WH.addDepot(T.get_1(),T.get_2(),T.get_3());
            }catch(Exception e){e.printStackTrace();}
        }
        assertThrows(Exception.class,()->WH.addDepot(error.get_1(),error.get_2(),error.get_3()));
    }

    private static Stream<Arguments> provideSourceOfErrorAdd() {
        return Stream.of(
                //Depot choice out of bounds
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(), new Triplet<>(5,Resources.Servants,1)),
                //Depot 1 errors
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(), new Triplet<>(1,Resources.Servants,-1)),
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(), new Triplet<>(1,Resources.Servants,2)),
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(){{add(new Triplet<>(1,Resources.Shields,1));}},
                        new Triplet<>(1,Resources.Servants,1)),
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(){{add(new Triplet<>(2,Resources.Servants,1));}},
                        new Triplet<>(1,Resources.Servants,1)),
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(){{add(new Triplet<>(2,Resources.Servants,1));}},
                        new Triplet<>(1,Resources.Servants,1)),
                //Depot 2 errors
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(), new Triplet<>(2,Resources.Servants,-1)),
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(), new Triplet<>(2,Resources.Servants,3)),
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(){{add(new Triplet<>(3,Resources.Servants,1));}},
                        new Triplet<>(2,Resources.Servants,1)),
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(){{add(new Triplet<>(1,Resources.Servants,1));}},
                        new Triplet<>(2,Resources.Servants,1)),
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(){{add(new Triplet<>(2,Resources.Servants,1));}},
                        new Triplet<>(2,Resources.Servants,2)),
                //Depot 3 errors
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(), new Triplet<>(3,Resources.Servants,-1)),
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(), new Triplet<>(3,Resources.Servants,4)),
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(){{add(new Triplet<>(1,Resources.Servants,1));}},
                        new Triplet<>(3,Resources.Servants,1)),
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(){{add(new Triplet<>(2,Resources.Servants,1));}},
                        new Triplet<>(3,Resources.Servants,1)),
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(){{add(new Triplet<>(3,Resources.Servants,2));}},
                        new Triplet<>(3,Resources.Servants,2))
        );
    }

    @Test
    @DisplayName("Ensure correct behaviour when withdrawing resources into warehouse")
    void testCanWithdraw(){
        try{
            WH.addDepot(1, Resources.Stones,1);
            WH.addDepot(2,Resources.Shields,2);
            WH.addDepot(3,Resources.Servants,3);
        }catch(Exception e) {e.printStackTrace();}
        assert(WH.getDepot(1).getKey().isPresent());
        assert(WH.getDepot(1).getKey().get().equals(Resources.Stones) && WH.getDepot(1).getValue()==1);
        assert(WH.getDepot(2).getKey().isPresent());
        assert(WH.getDepot(2).getKey().get().equals(Resources.Shields) && WH.getDepot(2).getValue()==2);
        assert(WH.getDepot(3).getKey().isPresent());
        assert(WH.getDepot(3).getKey().get().equals(Resources.Servants) && WH.getDepot(3).getValue()==3);
        try{
            WH.subDepot(1,1);
            WH.subDepot(2,2);
            WH.subDepot(3,2);
        }catch(Exception e) {e.printStackTrace();}
        assert(!WH.getDepot(1).getKey().isPresent() && WH.getDepot(1).getValue()==0);
        assert(!WH.getDepot(2).getKey().isPresent() && WH.getDepot(2).getValue()==0);
        assert(WH.getDepot(3).getKey().isPresent());
        assert(WH.getDepot(3).getKey().get().equals(Resources.Servants) && WH.getDepot(3).getValue()==1);
    }

    @ParameterizedTest
    @MethodSource("provideSourceOfErrorSub")
    @DisplayName("Ensure incorrect sub throws an exception")
    void CannotSubForDifferentReasons(ArrayList<Triplet<Integer,Resources,Integer>> PreliminaryMoves, Pair<Integer,Integer> error){
        for(Triplet<Integer,Resources,Integer> T : PreliminaryMoves){
            try{
                WH.addDepot(T.get_1(),T.get_2(),T.get_3());
            }catch(Exception e){e.printStackTrace();}
        }
        assertThrows(Exception.class,()->WH.subDepot(error.getKey(),error.getValue()));
    }

    private static Stream<Arguments> provideSourceOfErrorSub() {
        return Stream.of(
                //Depot choice out of bounds
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(), new Pair<>(5,1)),
                //Depot 1 errors
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(), new Pair<>(1,-1)),
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(), new Pair<>(1,2)),
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(), new Pair<>(1,1)),
                //Depot 2 errors
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(), new Pair<>(2,-1)),
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(), new Pair<>(2,3)),
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(), new Pair<>(2,1)),
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(){{add(new Triplet<>(2,Resources.Servants,1));}}, new Pair<>(2,2)),
                //Depot 3 errors
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(), new Pair<>(3,-1)),
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(), new Pair<>(3,4)),
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(), new Pair<>(3,1)),
                Arguments.of(new ArrayList<Triplet<Integer,Resources,Integer>>(){{add(new Triplet<>(3,Resources.Servants,1));}}, new Pair<>(3,2))
        );
    }

    @Test
    @DisplayName("Ensure correct behaviour of check on resources")
    void testCheckResources(){
        try{
            WH.addDepot(1, Resources.Stones,1);
            WH.addDepot(2,Resources.Shields,2);
        }catch(Exception e) {e.printStackTrace();}

        assert(WH.checkResourcePresent(1,Resources.Stones));
        assert(WH.checkResourcePresent(2,Resources.Shields));
        assert(!WH.checkResourcePresent(1,Resources.Servants));
        assert(!WH.checkResourcePresent(3,Resources.Stones));
    }
}
