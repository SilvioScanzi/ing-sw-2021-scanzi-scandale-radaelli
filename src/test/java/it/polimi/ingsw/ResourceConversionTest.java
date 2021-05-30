package it.polimi.ingsw;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.LeaderCard;
import it.polimi.ingsw.commons.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ResourceConversionTest {
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

    //This is the state of the market:
    //RED BLU BLU GRE
    //GRE YEL YEL PUR
    //WHI WHI WHI WHI

    @Test
    @DisplayName("Ensure correct conversion of marbles without white marbles")
    void testConversionNoWhite(){
        try {
            game.BuyMarketResourcesAction(0, true, 2, new ArrayList<>());
        }catch (Exception e) {e.printStackTrace();}
        assert(playerBoard.getHand().size()==4);
        assert(playerBoard.getHand().get(0).equals(Resources.Stones));
        assert(playerBoard.getHand().get(1).equals(Resources.Coins));
        assert(playerBoard.getHand().get(2).equals(Resources.Coins));
        assert(playerBoard.getHand().get(3).equals(Resources.Servants));
    }

    @Test
    @DisplayName("Ensure correct conversion of red marble")
    void testConversionRed(){
        try {
            game.BuyMarketResourcesAction(0, true, 1, new ArrayList<>());
        }catch (Exception e) {e.printStackTrace();}
        assert(playerBoard.getHand().size()==3);
        assert(playerBoard.getHand().get(0).equals(Resources.Shields));
        assert(playerBoard.getHand().get(1).equals(Resources.Shields));
        assert(playerBoard.getHand().get(2).equals(Resources.Stones));
        assert(playerBoard.getFaithTrack().getFaithMarker()==1);
    }

    @Test
    @DisplayName("Ensure correct conversion of White marble when player doesn't have any Leader Card that interfere")
    void testConversionWhiteNull(){
        try {
            game.BuyMarketResourcesAction(0, true, 3, new ArrayList<>());
        }catch (Exception e) {e.printStackTrace();}
        assert(playerBoard.getHand().size()==0);
    }

    @Test
    @DisplayName("Ensure correct conversion of White marble when player has one Leader Card that interfere")
    void testCanConversionWhiteWithLC(){
        LeaderCard LC = new LeaderCard(0,new HashMap<>(),new HashMap<>(),"WhiteMarbleAbility",Resources.Servants,0);
        playerBoard.getLeaderCardsHand().clear();
        playerBoard.getLeaderCardsHand().put(0,LC);
        try {
            game.playLeaderCard(0, 1);
        }catch(Exception e){e.printStackTrace();}

        try{
            game.BuyMarketResourcesAction(0,true,3,new ArrayList<>());
        }catch (Exception e){e.printStackTrace();}

        assert(playerBoard.getHand().size()==4);
        assert(playerBoard.getHand().get(0).equals(Resources.Servants));
        assert(playerBoard.getHand().get(1).equals(Resources.Servants));
        assert(playerBoard.getHand().get(2).equals(Resources.Servants));
        assert(playerBoard.getHand().get(3).equals(Resources.Servants));
    }

    @Test
    @DisplayName("Ensure correct conversion of White marble when player has two Leader Card that interfere")
    void testCanConversionWhiteWithTwoLC(){
        LeaderCard LCServants = new LeaderCard(0,new HashMap<>(),new HashMap<>(),"WhiteMarbleAbility",Resources.Servants,0);
        LeaderCard LCCoins = new LeaderCard(0,new HashMap<>(),new HashMap<>(),"WhiteMarbleAbility",Resources.Coins,0);
        playerBoard.getLeaderCardsHand().clear();
        playerBoard.getLeaderCardsHand().put(0,LCServants);
        playerBoard.getLeaderCardsHand().put(1,LCCoins);
        try {
            game.playLeaderCard(0, 1);
            game.playLeaderCard(0, 2);
        }catch(Exception e){e.printStackTrace();}
        ArrayList<Integer> requestedWMConversion = new ArrayList<>();
        requestedWMConversion.add(1);
        requestedWMConversion.add(2);
        requestedWMConversion.add(1);
        requestedWMConversion.add(2);
        try {
            game.BuyMarketResourcesAction(0, true, 3, requestedWMConversion);
        }catch (Exception e){e.printStackTrace();}
        assert(playerBoard.getHand().size()==4);
        assert(playerBoard.getHand().get(0).equals(Resources.Servants));
        assert(playerBoard.getHand().get(1).equals(Resources.Coins));
        assert(playerBoard.getHand().get(2).equals(Resources.Servants));
        assert(playerBoard.getHand().get(3).equals(Resources.Coins));
    }

    @Test
    @DisplayName("Ensure an exception is thrown when player has two Leader Card with conversion ability and doesn't specify which to use")
    void testCannotConversionWhiteWithTwoLC(){
        LeaderCard LCServants = new LeaderCard(0,new HashMap<>(),new HashMap<>(),"WhiteMarbleAbility",Resources.Servants,0);
        LeaderCard LCCoins = new LeaderCard(0,new HashMap<>(),new HashMap<>(),"WhiteMarbleAbility",Resources.Coins,0);
        playerBoard.getLeaderCardsHand().clear();
        playerBoard.getLeaderCardsHand().put(0,LCServants);
        playerBoard.getLeaderCardsHand().put(1,LCCoins);
        try {
            game.playLeaderCard(0, 1);
            game.playLeaderCard(0, 2);
        }catch(Exception e){e.printStackTrace();}
        ArrayList<Integer> requestedWMConversion = new ArrayList<>();
        requestedWMConversion.add(1);
        requestedWMConversion.add(1);
        requestedWMConversion.add(2);
        assertThrows(Exception.class,()-> game.BuyMarketResourcesAction(0,true,3,requestedWMConversion));
    }

    @Test
    @DisplayName("Ensure an exception is thrown when player has two Leader Card with conversion ability and specify invalid index")
    void testCannotConversionWhiteIndexOutOfBound(){
        LeaderCard LCServants = new LeaderCard(0,new HashMap<>(),new HashMap<>(),"WhiteMarbleAbility",Resources.Servants,0);
        LeaderCard LCCoins = new LeaderCard(0,new HashMap<>(),new HashMap<>(),"WhiteMarbleAbility",Resources.Coins,0);
        playerBoard.getLeaderCardsHand().clear();
        playerBoard.getLeaderCardsHand().put(0,LCServants);
        playerBoard.getLeaderCardsHand().put(1,LCCoins);
        try {
            game.playLeaderCard(0, 1);
            game.playLeaderCard(0, 2);
        }catch(Exception e){e.printStackTrace();}
        ArrayList<Integer> requestedWMConversion = new ArrayList<>();
        requestedWMConversion.add(1);
        requestedWMConversion.add(10);
        requestedWMConversion.add(2);
        requestedWMConversion.add(2);
        assertThrows(Exception.class,()-> game.BuyMarketResourcesAction(0,true,3,requestedWMConversion));
    }
}
