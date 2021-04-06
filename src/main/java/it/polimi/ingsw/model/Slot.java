package it.polimi.ingsw.model;
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

    public Optional<DevelopmentCard> getFirstCard(){
        if(developmentcards.size()==0) return Optional.empty();
        else return Optional.ofNullable(developmentcards.get(developmentcards.size()-1));
    }

    public ArrayList<DevelopmentCard> getDevelopmentcards() {
        return developmentcards;
    }

    public void addCard(DevelopmentCard DC) throws InvalidPlacementException {
        if(developmentcards.size()==0){
            if(DC.getLevel()==1)  developmentcards.add(DC);
            else throw new InvalidPlacementException("Not level 1 card");
        }
        else if(developmentcards.get(developmentcards.size()-1).getLevel() == DC.getLevel() - 1)  developmentcards.add(DC);
        else throw new InvalidPlacementException("Not correct card level");
    }
}
