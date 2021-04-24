package it.polimi.ingsw.model;
import it.polimi.ingsw.exceptions.EmptyException;
import it.polimi.ingsw.exceptions.InvalidPlacementException;

import java.util.*;

public class Slot {
    private final ArrayList<DevelopmentCard> developmentcards;

    public Slot() {
        developmentcards = new ArrayList<>();
    }

    public ArrayList<Pair<Colours,Integer>> getList(){
        ArrayList<Pair<Colours,Integer>> tmp = new ArrayList<>();
        for(DevelopmentCard DC : developmentcards){
            tmp.add(new Pair<>(DC.getColour(), DC.getLevel()));
        }
        return tmp;
    }

    public DevelopmentCard getFirstCard() throws EmptyException {
        if(developmentcards.size()==0) throw new EmptyException("S");
        else return developmentcards.get(developmentcards.size()-1);
    }

    public ArrayList<DevelopmentCard> getDevelopmentCards() {
        return developmentcards;
    }

    public void addCard(DevelopmentCard DC) throws InvalidPlacementException {
        if(developmentcards.size()==0){
            if(DC.getLevel()==1)  developmentcards.add(DC);
            else throw new InvalidPlacementException("Cannot play the selected card on this slot");
        }
        else if(developmentcards.get(developmentcards.size()-1).getLevel() == DC.getLevel() - 1)  developmentcards.add(DC);
        else throw new InvalidPlacementException("Cannot play the selected card on this slot");
    }
}
