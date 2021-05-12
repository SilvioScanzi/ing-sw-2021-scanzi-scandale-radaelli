package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.exceptions.EmptyException;
import it.polimi.ingsw.commons.Colours;
import it.polimi.ingsw.model.DevelopmentCard;
import it.polimi.ingsw.model.DevelopmentCardMarket;
import it.polimi.ingsw.commons.Pair;

import java.io.Serializable;
import java.util.HashMap;

public class DCMarketMessage extends Message implements Serializable {
    private final HashMap<Pair<Colours,Integer>, Integer> market;

    public DCMarketMessage(DevelopmentCardMarket DM){
        market = new HashMap<>();
        for(Colours C : Colours.values()){
            for(int L=1; L<=3;L++){
                DevelopmentCard DC;
                try{
                    DC = DM.peekFirstCard(C,L);
                    market.put(new Pair<>(C,L),DC.getVictoryPoints());
                }catch(EmptyException e){}
            }
        }
    }

    public HashMap<Pair<Colours,Integer>, Integer> getMarket() {
        return market;
    }
}
