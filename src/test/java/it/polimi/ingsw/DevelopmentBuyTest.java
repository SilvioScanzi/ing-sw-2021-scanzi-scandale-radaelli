package it.polimi.ingsw;
import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DevelopmentBuyTest{

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
    @DisplayName("Ensure correct behaviour of development card buying action")
    void testCanBuyDCCard(){
        try {
            playerBoard.getWarehouse().addDepot(3, Resources.Servants, 3);
            playerBoard.getWarehouse().addDepot(2, Resources.Stones, 2);
        }
        catch(Exception e) {e.printStackTrace();}

        ArrayList<Pair<String,Integer>> choice = new ArrayList<>();
        choice.add(new Pair<>("SE",3));
        choice.add(new Pair<>("SE",3));
        choice.add(new Pair<>("PI",2));
        choice.add(new Pair<>("PI",2));

        try {
            game.getDevelopmentCard(Colours.Purple, 1, 0, 1, choice);
        }catch(Exception e) {e.printStackTrace();}

        assert(playerBoard.getSlot(1).getList().size()==1);
        assert(playerBoard.getSlot(2).getList().size()==0);
        assert(playerBoard.getSlot(3).getList().size()==0);

        try {
            assert (playerBoard.getSlot(1).getFirstCard().getColour().equals(Colours.Purple));
            assert (playerBoard.getSlot(1).getFirstCard().getvictoryPoints() == 4);
        }catch(Exception e) {e.printStackTrace();}
    }

    @Test
    @DisplayName("Ensure correct behaviour of development card buying action when resources are fully got from the strongbox")
    void testCanBuyDCCardFromSB() {
        playerBoard.getStrongbox().addResource(Resources.Coins, 99);
        playerBoard.getStrongbox().addResource(Resources.Stones, 99);
        playerBoard.getStrongbox().addResource(Resources.Shields, 99);
        playerBoard.getStrongbox().addResource(Resources.Servants, 99);
        //System.out.println(game.getDevelopmentcardmarket().toString());


        try{
            ArrayList<Pair<String,Integer>> choice = new ArrayList<Pair<String,Integer>>()
                {{add(new Pair<>("SE",6));add(new Pair<>("SE",6));add(new Pair<>("PI",6));add(new Pair<>("PI",6));}};
            game.getDevelopmentCard(Colours.Purple, 1, 0, 1,choice);
            choice = new ArrayList<Pair<String,Integer>>()
            {{add(new Pair<>("MO",6));add(new Pair<>("MO",6));add(new Pair<>("SC",6));add(new Pair<>("SC",6));}};
            game.getDevelopmentCard(Colours.Green, 1, 0, 2,choice);
            choice = new ArrayList<Pair<String,Integer>>()
            {{add(new Pair<>("MO",6));add(new Pair<>("MO",6));add(new Pair<>("SE",6));add(new Pair<>("SE",6));}};
            game.getDevelopmentCard(Colours.Blue, 1, 0, 3,choice);
            choice = new ArrayList<Pair<String,Integer>>()
            {{add(new Pair<>("SE",6));add(new Pair<>("PI",6));add(new Pair<>("SE",6));add(new Pair<>("PI",6));add(new Pair<>("SE",6));add(new Pair<>("PI",6));}};
            game.getDevelopmentCard(Colours.Yellow, 2, 0, 1,choice);
            choice = new ArrayList<Pair<String,Integer>>()
            {{add(new Pair<>("SE",6));add(new Pair<>("SC",6));add(new Pair<>("SE",6));add(new Pair<>("SC",6));add(new Pair<>("SE",6));add(new Pair<>("SC",6));}};
            game.getDevelopmentCard(Colours.Purple, 2, 0, 2,choice);
            choice = new ArrayList<Pair<String,Integer>>()
            {{add(new Pair<>("MO",6));add(new Pair<>("SC",6));add(new Pair<>("SC",6));add(new Pair<>("SC",6));add(new Pair<>("MO",6));add(new Pair<>("MO",6));}};
            game.getDevelopmentCard(Colours.Green, 2, 0, 3,choice);
            choice = new ArrayList<Pair<String,Integer>>()
            {{add(new Pair<>("MO",6));add(new Pair<>("MO",6));add(new Pair<>("PI",6));add(new Pair<>("PI",6));add(new Pair<>("MO",6));add(new Pair<>("MO",6));add(new Pair<>("PI",6));add(new Pair<>("PI",6));}};
            game.getDevelopmentCard(Colours.Blue, 3, 0, 1,choice);
            choice = new ArrayList<Pair<String,Integer>>()
            {{add(new Pair<>("SE",6));add(new Pair<>("SE",6));add(new Pair<>("PI",6));add(new Pair<>("PI",6));add(new Pair<>("SE",6));add(new Pair<>("SE",6));add(new Pair<>("PI",6));add(new Pair<>("PI",6));}};
            game.getDevelopmentCard(Colours.Yellow, 3, 0, 2,choice);
            choice = new ArrayList<Pair<String,Integer>>()
            {{add(new Pair<>("SE",6));add(new Pair<>("SE",6));add(new Pair<>("SC",6));add(new Pair<>("SC",6));add(new Pair<>("SE",6));add(new Pair<>("SE",6));add(new Pair<>("SC",6));add(new Pair<>("SC",6));}};
            game.getDevelopmentCard(Colours.Purple, 3, 0, 3,choice);
        }catch(Exception e) {e.printStackTrace();}
        //System.out.println(playerBoard.slottoString());
        assert(playerBoard.getSlot(1).getList().size()==3);
        assert(playerBoard.getSlot(2).getList().size()==3);
        assert(playerBoard.getSlot(3).getList().size()==3);
    }

    @Test
    @DisplayName("Ensure correct behaviour of development card buying action when resources are partially got from the strongbox and partially from werehouse")
    void testCanBuyDCCardFromSBAndWH(){
        try {
            playerBoard.getWarehouse().addDepot(1, Resources.Servants, 1);
            playerBoard.getWarehouse().addDepot(2, Resources.Stones, 1);
        }
        catch(Exception e) {e.printStackTrace();}
        playerBoard.getStrongbox().addResource(Resources.Servants,1);
        playerBoard.getStrongbox().addResource(Resources.Stones,1);
        ArrayList<Pair<String,Integer>> choice = new ArrayList<Pair<String,Integer>>()
        {{add(new Pair<>("SE",6));add(new Pair<>("SE",1));add(new Pair<>("PI",2));add(new Pair<>("PI",6));}};
        try{
            game.getDevelopmentCard(Colours.Purple, 1, 0, 1,choice);
        }catch(Exception e) {e.printStackTrace();}
        //System.out.println(playerBoard.slottoString());
        assert(playerBoard.getSlot(1).getList().size()==1);
        assert(playerBoard.getSlot(2).getList().size()==0);
        assert(playerBoard.getSlot(3).getList().size()==0);
    }

    @Test
    @DisplayName("Ensure correct behaviour of development card buying action when a lack of resources impedes the action")
    void testCannotBuyDCCardResources() {
        ArrayList<Pair<String,Integer>> choice = new ArrayList<Pair<String,Integer>>()
        {{add(new Pair<>("SE",6));add(new Pair<>("SE",6));add(new Pair<>("PI",6));add(new Pair<>("PI",6));}};
        assertThrows(IllegalArgumentException.class,()->{game.getDevelopmentCard(Colours.Purple, 1, 0, 1,choice);});
    }

    @ParameterizedTest
    @MethodSource("provideCombinationsOfColoursAndLevel")
    @DisplayName("Ensure correct behaviour of development card buying action when slot choice impedes the action")
    void testCannotBuyDCCardSlot(Colours c, int l, int s,ArrayList<Pair<String,Integer>>choice){
        playerBoard.getStrongbox().addResource(Resources.Coins,99);
        playerBoard.getStrongbox().addResource(Resources.Stones,99);
        playerBoard.getStrongbox().addResource(Resources.Shields,99);
        playerBoard.getStrongbox().addResource(Resources.Servants,99);
        try {
            game.getDevelopmentCard(Colours.Purple,1,0,1,new ArrayList<Pair<String,Integer>>(){{add(new Pair<>("SE",6));add(new Pair<>("SE",6));add(new Pair<>("PI",6));add(new Pair<>("PI",6));}});
        }catch(Exception e){e.printStackTrace();}
        assertThrows(Exception.class,()->{game.getDevelopmentCard(c, l, 0, s,choice);});
    }

    private static Stream<Arguments> provideCombinationsOfColoursAndLevel() {
        return Stream.of(
                Arguments.of(Colours.Green,3,1,new ArrayList<Pair<String,Integer>>()
                    {{add(new Pair<>("MO",6));add(new Pair<>("SC",6));add(new Pair<>("SC",6));add(new Pair<>("MO",6));add(new Pair<>("MO",6));add(new Pair<>("SC",6));add(new Pair<>("SC",6));add(new Pair<>("MO",6));}}),
                Arguments.of(Colours.Yellow,2,2,new ArrayList<Pair<String,Integer>>()
                    {{add(new Pair<>("PI",6));add(new Pair<>("SE",6));add(new Pair<>("PI",6));add(new Pair<>("SE",6));add(new Pair<>("SE",6));add(new Pair<>("PI",6));}}),
                Arguments.of(Colours.Purple,1,1,new ArrayList<Pair<String,Integer>>()
                    {{add(new Pair<>("SE",6));add(new Pair<>("PI",6));add(new Pair<>("PI",6));add(new Pair<>("SE",6));}}),
                Arguments.of(Colours.Green,3,2,new ArrayList<Pair<String,Integer>>()
                {{add(new Pair<>("MO",6));add(new Pair<>("SC",6));add(new Pair<>("SC",6));add(new Pair<>("MO",6));add(new Pair<>("MO",6));add(new Pair<>("SC",6));add(new Pair<>("SC",6));add(new Pair<>("MO",6));}})
        );
    }
}
