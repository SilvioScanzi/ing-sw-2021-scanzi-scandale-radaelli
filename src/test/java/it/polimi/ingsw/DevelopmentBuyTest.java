package it.polimi.ingsw;
import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DevelopmentBuyTest {

    private Game game;
    Board playerBoard;

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
    @DisplayName("Ensure correct behaviour of development card buying action when lack of resources impedes the action")
    void testCannotBuyDCCardForLackOfResources(){
        try {
            playerBoard.getWarehouse().addDepot(3, Resources.Servants, 3);
            playerBoard.getWarehouse().addDepot(2, Resources.Stones, 2);
        }
        catch(Exception e) {e.printStackTrace();}

        try {
            game.getDevelopmentCard(Colours.Purple, 1, 0, 1);
        }catch(Exception e) {e.printStackTrace();}
        //System.out.println(playerBoard.slottoString());

        assertThrows(IllegalArgumentException.class,()->{game.getDevelopmentCard(Colours.Purple, 1, 0, 2);});
        //System.out.println(playerBoard.slottoString());
    }

    @Test
    @DisplayName("Ensure correct behaviour of development card buying action when resources are fully got from the strongbox")
    void testCanBuyDCCardFromSB(){
        playerBoard.getStrongbox().addResource(Resources.Coins,99);
        playerBoard.getStrongbox().addResource(Resources.Stones,99);
        playerBoard.getStrongbox().addResource(Resources.Shields,99);
        playerBoard.getStrongbox().addResource(Resources.Servants,99);
        try{
            game.getDevelopmentCard(Colours.Purple, 1, 0, 1);
            game.getDevelopmentCard(Colours.Green, 1, 0, 2);
            game.getDevelopmentCard(Colours.Blue, 1, 0, 3);
            game.getDevelopmentCard(Colours.Yellow, 2, 0, 1);
            game.getDevelopmentCard(Colours.Purple, 2, 0, 2);
            game.getDevelopmentCard(Colours.Green, 2, 0, 3);
            game.getDevelopmentCard(Colours.Blue, 3, 0, 1);
            game.getDevelopmentCard(Colours.Yellow, 3, 0, 2);
            game.getDevelopmentCard(Colours.Purple, 3, 0, 3);
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
        try{
            game.getDevelopmentCard(Colours.Purple, 1, 0, 1);
        }catch(Exception e) {e.printStackTrace();}
        //System.out.println(playerBoard.slottoString());
        assert(playerBoard.getSlot(1).getList().size()==1);
        assert(playerBoard.getSlot(2).getList().size()==0);
        assert(playerBoard.getSlot(3).getList().size()==0);
    }
}
