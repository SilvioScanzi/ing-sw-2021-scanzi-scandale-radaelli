package it.polimi.ingsw;
import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.*;

import java.util.*;


public class FaithTrackTest {

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
    @DisplayName("Make sure the faith marker cannot exceed the max value")
    void testStopAtTheEnd (){
        for(int i=0;i<26;i++){
            playerBoard.getFaithtrack().advanceTrack();
            System.out.println(playerBoard.getFaithtrack().getFaithMarker());
        }
        assert (playerBoard.getFaithtrack().getFaithMarker() == 24);
    }

    @Test
    @DisplayName("Ensures correct initialisation")
    void checkInit(){
        assert(playerBoard.getFaithtrack().getFaithMarker()==0);
        for (boolean PF : playerBoard.getFaithtrack().getPopeFavor()){
            assert (!PF);
        }
    }
}