package it.polimi.ingsw.model;
import it.polimi.ingsw.exceptions.InvalidPlacementException;

import java.util.*;

public class Slot {
    //Non va bene come Stack! bisogna poter "leggere" le carte sottostanti per i leader.
    private ArrayList<DevelopmentCard> developmentcards;

    public Slot() {
        developmentcards = new ArrayList<>();
    }

    public Optional<DevelopmentCard> getFirstCard(){
        if(developmentcards.size()==0) return Optional.empty();
        else return Optional.of(developmentcards.get(developmentcards.size()-1));
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
