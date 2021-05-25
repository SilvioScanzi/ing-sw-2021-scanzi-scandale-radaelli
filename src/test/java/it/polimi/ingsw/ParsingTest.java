package it.polimi.ingsw;

import it.polimi.ingsw.commons.*;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ParsingTest {

    private DevelopmentCardMarket DCM;
    private LeaderCardDeck LCD;

    @Test
    @DisplayName("Ensure correct number Development Cards got from parsing")
    void testParsingNumberDC(){
        DCM = new DevelopmentCardMarket(0);
        DevelopmentCard DC;
        for(Colours c : Colours.values()){
            for(int i=1;i<=3;i++){
                for(int j=1;j<=4;j++){
                    DC = DCM.getFirstCard(c,i);
                    assert(DC.getColour().equals(c) && DC.getLevel()==i);
                }
            }
        }

        for(Colours c : Colours.values()){
            assertThrows(Exception.class,()-> DCM.getFirstCard(c,1));
            assertThrows(Exception.class,()-> DCM.getFirstCard(c,2));
            assertThrows(Exception.class,()-> DCM.getFirstCard(c,3));
        }
    }

    @ParameterizedTest
    @MethodSource("provideSourceOfDCCards")
    @DisplayName("Ensure correct parsing of DCCards")
    void testParsingDC(Colours c, int l, int vp, HashMap<Resources,Integer> cost, HashMap<Resources,Integer> r, HashMap<Resources,Integer> p, int faith){
        DCM = new DevelopmentCardMarket(0);
        DevelopmentCard DC;
        DC = DCM.getFirstCard(c,l);
        assert(DC.getCost().equals(cost) && DC.getVictoryPoints()==vp && DC.getRequiredResources().equals(r) && DC.getProducedResources().equals(p) && DC.getProducedFaith()==faith);
    }

    private static Stream<Arguments> provideSourceOfDCCards() {
        return Stream.of(
                Arguments.of(Colours.Green,1,4,new HashMap<Resources,Integer>(){{put(Resources.Shields,2);put(Resources.Coins,2);}},
                        new HashMap<Resources,Integer>(){{put(Resources.Stones,1);put(Resources.Servants,1);}},
                        new HashMap<Resources,Integer>(){{put(Resources.Coins,2);}},1),
                Arguments.of(Colours.Yellow,1,4,new HashMap<Resources,Integer>(){{put(Resources.Shields,2);put(Resources.Stones,2);}},
                        new HashMap<Resources,Integer>(){{put(Resources.Coins,1);put(Resources.Servants,1);}},
                        new HashMap<Resources,Integer>(){{put(Resources.Shields,2);}},1),
                Arguments.of(Colours.Purple,1,4,new HashMap<Resources,Integer>(){{put(Resources.Servants,2);put(Resources.Stones,2);}},
                        new HashMap<Resources,Integer>(){{put(Resources.Coins,1);put(Resources.Shields,1);}},
                        new HashMap<Resources,Integer>(){{put(Resources.Stones,2);}},1),
                Arguments.of(Colours.Blue,1,4,new HashMap<Resources,Integer>(){{put(Resources.Servants,2);put(Resources.Coins,2);}},
                        new HashMap<Resources,Integer>(){{put(Resources.Stones,1);put(Resources.Shields,1);}},
                        new HashMap<Resources,Integer>(){{put(Resources.Servants,2);}},1)
        );
    }

    @Test
    @DisplayName("Ensure correct number Leader Cards got from parsing")
    void testParsingNumberLC(){
        LCD = new LeaderCardDeck();
        for(int i=0;i<16;i++){
            LCD.getFirstCard();
        }
        assertThrows(Exception.class,()-> LCD.getFirstCard());
    }

    @Test
    @DisplayName("Ensure correct parsing of LCCards")
    void testParsingLC(){
        LCD = new LeaderCardDeck(0);
        LeaderCard LC;
        LC = LCD.getFirstCard();
        HashMap<Colours, Pair<Integer,Integer>> RC = new HashMap<>(){{put(Colours.Yellow,new Pair<>(2,0));put(Colours.Blue,new Pair<>(1,0));}};
        assert(LC.getVictoryPoints()==5 && LC.getRequiredColours().equals(RC) && LC.getRequiredResources().equals(new HashMap<>())
                && LC.getAbility().getResType().equals(Resources.Servants) && LC.getAbility().getCapacity()==0 && LC.getAbility().doConvert());
    }
}
