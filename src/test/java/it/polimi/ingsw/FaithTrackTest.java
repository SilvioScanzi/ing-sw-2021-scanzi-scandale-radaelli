package it.polimi.ingsw;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.*;

public class FaithTrackTest {

    private FaithTrack FT;

    @BeforeEach
    void setup(){
        FT = new FaithTrack();
    }

    @Test
    @DisplayName("Ensures the faith marker cannot exceed the max value")
    void testStopAtTheEnd (){
        for(int i=0;i<26;i++){
            FT.advanceTrack();
        }
        assert (FT.getFaithMarker() == 24);
    }

    @Test
    @DisplayName("Ensures correct initialisation")
    void checkInit(){
        assert(FT.getFaithMarker()==0);
        for (boolean PF : FT.getPopeFavor()){
            assert (!PF);
        }
    }
}