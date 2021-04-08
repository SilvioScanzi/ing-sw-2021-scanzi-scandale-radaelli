package it.polimi.ingsw.model;
import it.polimi.ingsw.exceptions.EmptyDeckException;
import it.polimi.ingsw.exceptions.InvalidPlacementException;

import java.util.*;

public class Slot {
    private ArrayList<DevelopmentCard> developmentcards;
    private boolean activatedProduction;

    public Slot() {
        developmentcards = new ArrayList<>();
        activatedProduction = false;
    }

    public ArrayList<Pair<Colours,Integer>> getList(){
        ArrayList<Pair<Colours,Integer>> tmp = new ArrayList<>();
        for(DevelopmentCard DC : developmentcards){
            tmp.add(new Pair<>(DC.getColour(), DC.getLevel()));
        }
        return tmp;
    }

    public DevelopmentCard getFirstCard() throws EmptyDeckException{
        if(developmentcards.size()==0) throw new EmptyDeckException("S");
        else return developmentcards.get(developmentcards.size()-1);
    }

    public ArrayList<DevelopmentCard> getDevelopmentcards() {
        return developmentcards;
    }

    public void addCard(DevelopmentCard DC) throws InvalidPlacementException {
        if(developmentcards.size()==0){
            if(DC.getLevel()==1)  developmentcards.add(DC);
            else throw new InvalidPlacementException("Card level is incorrect",0);
        }
        else if(developmentcards.get(developmentcards.size()-1).getLevel() == DC.getLevel() - 1)  developmentcards.add(DC);
        else throw new InvalidPlacementException("Card level is incorrect",0);
    }
}
