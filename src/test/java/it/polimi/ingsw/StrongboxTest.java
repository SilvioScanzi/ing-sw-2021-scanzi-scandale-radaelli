package it.polimi.ingsw;

import it.polimi.ingsw.commons.Resources;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class StrongboxTest {

    private Strongbox SB;

    @BeforeEach
    void setup(){
        SB = new Strongbox();
    }

    @Test
    @DisplayName("Ensure correct initialisation")
    void testEmpty(){
        for(Resources r : Resources.values()){
            assert(SB.getResource(r)==0);
        }
    }

    @Test
    @DisplayName("Ensure correct add")
    void testAdd(){
        for(Resources r : Resources.values()){
            SB.addResource(r,42);
        }
        for(Resources r : Resources.values()){
            assert(SB.getResource(r)==42);
        }
    }

    @Test
    @DisplayName("Ensure correct sub")
    void testCanSub(){
        for(Resources r : Resources.values()){
            SB.addResource(r,42);
        }
        for(Resources r : Resources.values()){
            try {
                SB.subResource(r,40);
            }catch(Exception e){e.printStackTrace();}
        }
        for(Resources r : Resources.values()){
            assert(SB.getResource(r)==2);
        }
    }

    @Test
    @DisplayName("Ensure incorrect sub throws an exception")
    void testCannotSub(){
        for(Resources r : Resources.values()){
            SB.addResource(r,42);
        }
        for(Resources r : Resources.values()){
            assertThrows(Exception.class,()->SB.subResource(r,50));
        }
        for(Resources r : Resources.values()){
            assert(SB.getResource(r)==42);
        }
    }
}
