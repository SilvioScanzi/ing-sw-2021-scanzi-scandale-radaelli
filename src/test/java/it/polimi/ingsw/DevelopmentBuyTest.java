package it.polimi.ingsw;
import it.polimi.ingsw.commons.Colours;
import it.polimi.ingsw.commons.Pair;
import it.polimi.ingsw.commons.Resources;
import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import java.util.*;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DevelopmentBuyTest{

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

    //Buy action without leader card influence

    @Test
    @DisplayName("Ensure correct behaviour of development card buying action, when resources are taken from warehouse")
    void testCanBuyDCard(){
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
            game.BuyDevelopmentCardAction(Colours.Purple, 1, 0, 1, choice);
        }catch(Exception e) {e.printStackTrace();}

        assert(playerBoard.getSlot(1).getList().size()==1);
        assert(playerBoard.getSlot(2).getList().size()==0);
        assert(playerBoard.getSlot(3).getList().size()==0);
        try {
            assert (playerBoard.getSlot(1).getFirstCard().getColour().equals(Colours.Purple));
            assert (playerBoard.getSlot(1).getFirstCard().getVictoryPoints() == 4);
        }catch(Exception e) {e.printStackTrace();}
    }

    @Test
    @DisplayName("Ensure correct behaviour of development card buying action, choosing the right slot for every card, stacking them in order")
    void testCanBuyDCardFromSB() {
        playerBoard.getStrongbox().addResource(Resources.Coins, 99);
        playerBoard.getStrongbox().addResource(Resources.Stones, 99);
        playerBoard.getStrongbox().addResource(Resources.Shields, 99);
        playerBoard.getStrongbox().addResource(Resources.Servants, 99);

        try{
            ArrayList<Pair<String,Integer>> choice = new ArrayList<Pair<String,Integer>>()
                {{add(new Pair<>("SE",6));add(new Pair<>("SE",6));add(new Pair<>("PI",6));add(new Pair<>("PI",6));}};
            game.BuyDevelopmentCardAction(Colours.Purple, 1, 0, 1,choice);
            playerBoard.setActionDone(false);
            choice = new ArrayList<Pair<String,Integer>>()
            {{add(new Pair<>("MO",6));add(new Pair<>("MO",6));add(new Pair<>("SC",6));add(new Pair<>("SC",6));}};
            game.BuyDevelopmentCardAction(Colours.Green, 1, 0, 2,choice);
            playerBoard.setActionDone(false);
            choice = new ArrayList<Pair<String,Integer>>()
            {{add(new Pair<>("MO",6));add(new Pair<>("MO",6));add(new Pair<>("SE",6));add(new Pair<>("SE",6));}};
            game.BuyDevelopmentCardAction(Colours.Blue, 1, 0, 3,choice);
            playerBoard.setActionDone(false);
            choice = new ArrayList<Pair<String,Integer>>()
            {{add(new Pair<>("SE",6));add(new Pair<>("PI",6));add(new Pair<>("SE",6));add(new Pair<>("PI",6));add(new Pair<>("SE",6));add(new Pair<>("PI",6));}};
            game.BuyDevelopmentCardAction(Colours.Yellow, 2, 0, 1,choice);
            playerBoard.setActionDone(false);
            choice = new ArrayList<Pair<String,Integer>>()
            {{add(new Pair<>("SE",6));add(new Pair<>("SC",6));add(new Pair<>("SE",6));add(new Pair<>("SC",6));add(new Pair<>("SE",6));add(new Pair<>("SC",6));}};
            game.BuyDevelopmentCardAction(Colours.Purple, 2, 0, 2,choice);
            playerBoard.setActionDone(false);
            choice = new ArrayList<Pair<String,Integer>>()
            {{add(new Pair<>("MO",6));add(new Pair<>("SC",6));add(new Pair<>("SC",6));add(new Pair<>("SC",6));add(new Pair<>("MO",6));add(new Pair<>("MO",6));}};
            game.BuyDevelopmentCardAction(Colours.Green, 2, 0, 3,choice);
            playerBoard.setActionDone(false);
            choice = new ArrayList<Pair<String,Integer>>()
            {{add(new Pair<>("MO",6));add(new Pair<>("MO",6));add(new Pair<>("PI",6));add(new Pair<>("PI",6));add(new Pair<>("MO",6));add(new Pair<>("MO",6));add(new Pair<>("PI",6));add(new Pair<>("PI",6));}};
            game.BuyDevelopmentCardAction(Colours.Blue, 3, 0, 1,choice);
            playerBoard.setActionDone(false);
            choice = new ArrayList<Pair<String,Integer>>()
            {{add(new Pair<>("SE",6));add(new Pair<>("SE",6));add(new Pair<>("PI",6));add(new Pair<>("PI",6));add(new Pair<>("SE",6));add(new Pair<>("SE",6));add(new Pair<>("PI",6));add(new Pair<>("PI",6));}};
            game.BuyDevelopmentCardAction(Colours.Yellow, 3, 0, 2,choice);
            playerBoard.setActionDone(false);
            choice = new ArrayList<Pair<String,Integer>>()
            {{add(new Pair<>("SE",6));add(new Pair<>("SE",6));add(new Pair<>("SC",6));add(new Pair<>("SC",6));add(new Pair<>("SE",6));add(new Pair<>("SE",6));add(new Pair<>("SC",6));add(new Pair<>("SC",6));}};
            game.BuyDevelopmentCardAction(Colours.Purple, 3, 0, 3,choice);
        }catch(Exception e) {e.printStackTrace();}

        assert(playerBoard.getSlot(1).getList().size()==3);
        assert(playerBoard.getSlot(2).getList().size()==3);
        assert(playerBoard.getSlot(3).getList().size()==3);
        try {
            assert (playerBoard.getSlot(1).getFirstCard().getColour().equals(Colours.Blue));
            assert (playerBoard.getSlot(2).getFirstCard().getColour().equals(Colours.Yellow));
            assert (playerBoard.getSlot(3).getFirstCard().getColour().equals(Colours.Purple));
            assert (playerBoard.getSlot(1).getFirstCard().getVictoryPoints() == 12);
            assert (playerBoard.getSlot(2).getFirstCard().getVictoryPoints() == 12);
            assert (playerBoard.getSlot(3).getFirstCard().getVictoryPoints() == 12);
        }catch(Exception e) {e.printStackTrace();}
    }

    @Test
    @DisplayName("Ensure correct behaviour of development card buying action when resources are partially got from the strongbox and partially from warehouse")
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
            game.BuyDevelopmentCardAction(Colours.Purple, 1, 0, 1,choice);
        }catch(Exception e) {e.printStackTrace();}

        assert(playerBoard.getSlot(1).getList().size()==1);
        assert(playerBoard.getSlot(2).getList().size()==0);
        assert(playerBoard.getSlot(3).getList().size()==0);
    }

    @Test
    @DisplayName("Ensure correct behaviour of development card buying action when a lack of resources impedes the action")
    void testCannotBuyDCCardResources() {
        ArrayList<Pair<String,Integer>> choice = new ArrayList<Pair<String,Integer>>()
        {{add(new Pair<>("SE",6));add(new Pair<>("SE",6));add(new Pair<>("PI",6));add(new Pair<>("PI",6));}};
        assertThrows(Exception.class,()-> game.BuyDevelopmentCardAction(Colours.Purple, 1, 0, 1,choice));
    }

    @Test
    @DisplayName("Ensure exception is thrown when DC stack is empty")
    void testCannotBuyDCEmpty(){
        ArrayList<Pair<String,Integer>> choice = new ArrayList<Pair<String,Integer>>()
        {{add(new Pair<>("SE",6));add(new Pair<>("SE",6));add(new Pair<>("PI",6));add(new Pair<>("PI",6));}};
        for(int i=0;i<4;i++) {
            game.getDevelopmentCardMarket().getFirstCard(Colours.Purple, 1);
        }
        assertThrows(Exception.class,()-> game.BuyDevelopmentCardAction(Colours.Purple, 1, 0, 1,choice));
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
            game.BuyDevelopmentCardAction(Colours.Purple,1,0,1,new ArrayList<Pair<String,Integer>>(){{add(new Pair<>("SE",6));add(new Pair<>("SE",6));add(new Pair<>("PI",6));add(new Pair<>("PI",6));}});
        }catch(Exception e){e.printStackTrace();}
        assertThrows(Exception.class,()-> game.BuyDevelopmentCardAction(c, l, 0, s,choice));
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

    //Leader Card Tests

    @Test
    @DisplayName("Ensure correct behaviour with LeaderCard Discount, userChoice accounts for the discount")
    void testCanBuyDCCardWithDiscount(){
        //Adding a new LCCard which provides a discount on servants
        LeaderCard LCDiscount = new LeaderCard(0,new HashMap<>(),new HashMap<>(),"DiscountAbility",Resources.Servants,-1);
        playerBoard.getLeaderCardsHand().clear();
        playerBoard.getLeaderCardsHand().put(0,LCDiscount);
        try {
            game.playLeaderCard(0, 1);
        }catch(Exception e){e.printStackTrace();}

        try {
            playerBoard.getWarehouse().addDepot(1, Resources.Servants, 1);
            playerBoard.getWarehouse().addDepot(2, Resources.Stones, 1);
        }
        catch(Exception e) {e.printStackTrace();}
        playerBoard.getStrongbox().addResource(Resources.Servants,1);
        playerBoard.getStrongbox().addResource(Resources.Stones,1);
        ArrayList<Pair<String,Integer>> choice = new ArrayList<Pair<String,Integer>>()
        {{add(new Pair<>("SE",6));add(new Pair<>("PI",2));add(new Pair<>("PI",6));}};
        try{
            game.BuyDevelopmentCardAction(Colours.Purple, 1, 0, 1,choice);
        }catch(Exception e) {e.printStackTrace();}

        try {
            assert (playerBoard.getSlot(1).getFirstCard().getColour().equals(Colours.Purple));
            assert (playerBoard.getSlot(1).getFirstCard().getVictoryPoints() == 4);
        }catch(Exception e) {e.printStackTrace();}

        assert(playerBoard.getSlot(1).getList().size()==1);
        assert(playerBoard.getSlot(2).getList().size()==0);
        assert(playerBoard.getSlot(3).getList().size()==0);

        assert(playerBoard.getWarehouse().getDepot(1).getKey().isPresent());
        assert(playerBoard.getWarehouse().getDepot(1).getKey().get().equals(Resources.Servants));
        assert(playerBoard.getWarehouse().getDepot(1).getValue()==1);
    }

    @Test
    @DisplayName("Ensure that when userChoice doesn't account for the discount, an exception is thrown")
    void testCannotBuyDCCardWithDiscount(){
        //Adding a new LCCard which provides a discount on servants
        LeaderCard LCDiscount = new LeaderCard(0,new HashMap<>(),new HashMap<>(),"DiscountAbility",Resources.Servants,-1);
        playerBoard.getLeaderCardsHand().clear();
        playerBoard.getLeaderCardsHand().put(0,LCDiscount);
        try {
            game.playLeaderCard(0, 1);
        }catch(Exception e){e.printStackTrace();}

        try {
            playerBoard.getWarehouse().addDepot(1, Resources.Servants, 1);
            playerBoard.getWarehouse().addDepot(2, Resources.Stones, 1);
        }
        catch(Exception e) {e.printStackTrace();}
        playerBoard.getStrongbox().addResource(Resources.Servants,1);
        playerBoard.getStrongbox().addResource(Resources.Stones,1);

        ArrayList<Pair<String,Integer>> choice = new ArrayList<Pair<String,Integer>>()
        {{add(new Pair<>("SE",1));add(new Pair<>("SE",6));add(new Pair<>("PI",2));add(new Pair<>("PI",6));}};

        assertThrows(Exception.class,()-> game.BuyDevelopmentCardAction(Colours.Purple, 1, 0, 1,choice));
    }

    @Test
    @DisplayName("Ensure correct behaviour with LeaderCard ExtraSlot")
    void testCanBuyFromExtraSlot(){
        //Adding a new LCCard which provides extra slots for stones
        LeaderCard LCSlot = new LeaderCard(0,new HashMap<>(),new HashMap<>(),"ExtraSlotAbility",Resources.Stones,2);
        playerBoard.getLeaderCardsHand().clear();
        playerBoard.getLeaderCardsHand().put(0,LCSlot);
        try {
            game.playLeaderCard(0, 1);
        }catch(Exception e){e.printStackTrace();}


        try {
            playerBoard.getWarehouse().addDepot(1, Resources.Servants, 1);
        }
        catch(Exception e) {e.printStackTrace();}
        playerBoard.getStrongbox().addResource(Resources.Servants,1);
        playerBoard.getLeaderCardsPlayed().get(0).getAbility().doUpdateSlot(Resources.Stones,2);

        ArrayList<Pair<String,Integer>> choice = new ArrayList<Pair<String,Integer>>()
        {{add(new Pair<>("SE",1));add(new Pair<>("SE",6));add(new Pair<>("PI",4));add(new Pair<>("PI",4));}};

        try{
            game.BuyDevelopmentCardAction(Colours.Purple, 1, 0, 1,choice);
        }catch(Exception e) {e.printStackTrace();}

        try {
            assert (playerBoard.getSlot(1).getFirstCard().getColour().equals(Colours.Purple));
            assert (playerBoard.getSlot(1).getFirstCard().getVictoryPoints() == 4);
        }catch(Exception e) {e.printStackTrace();}

        assert(playerBoard.getSlot(2).getList().size()==0);
        assert(playerBoard.getSlot(3).getList().size()==0);
        assert(playerBoard.getLeaderCardsPlayed().get(0).getAbility().getStashedResources()==0);
    }

    @Test
    @DisplayName("Ensure correct behaviour with LeaderCard ExtraSlot combined with LeaderCard Discount")
    void testCanBuyFromExtraSlotWithDiscount(){
        //Adding a new LCCard which provides a discount on servants and a new LCCard which provides extra slots for stones
        LeaderCard LCDiscount = new LeaderCard(0,new HashMap<>(),new HashMap<>(),"DiscountAbility",Resources.Servants,-1);
        LeaderCard LCSlot = new LeaderCard(0,new HashMap<>(),new HashMap<>(),"ExtraSlotAbility",Resources.Stones,2);
        playerBoard.getLeaderCardsHand().clear();
        playerBoard.getLeaderCardsHand().put(0,LCSlot);
        playerBoard.getLeaderCardsHand().put(1,LCDiscount);
        try {
            game.playLeaderCard(0, 1);
            game.playLeaderCard(0,2);
        }catch(Exception e){e.printStackTrace();}

        try {
            playerBoard.getWarehouse().addDepot(1, Resources.Servants, 1);
        }
        catch(Exception e) {e.printStackTrace();}
        playerBoard.getStrongbox().addResource(Resources.Servants,1);
        playerBoard.getLeaderCardsPlayed().get(0).getAbility().doUpdateSlot(Resources.Stones,2);

        ArrayList<Pair<String,Integer>> choice = new ArrayList<Pair<String,Integer>>()
        {{add(new Pair<>("SE",1));add(new Pair<>("PI",4));add(new Pair<>("PI",4));}};

        try{
            game.BuyDevelopmentCardAction(Colours.Purple, 1, 0, 1,choice);
        }catch(Exception e) {e.printStackTrace();}

        try {
            assert (playerBoard.getSlot(1).getFirstCard().getColour().equals(Colours.Purple));
            assert (playerBoard.getSlot(1).getFirstCard().getVictoryPoints() == 4);
        }catch(Exception e) {e.printStackTrace();}

        assert(playerBoard.getSlot(2).getList().size()==0);
        assert(playerBoard.getSlot(3).getList().size()==0);
        assert(playerBoard.getLeaderCardsPlayed().get(0).getAbility().getStashedResources()==0);
    }
}
