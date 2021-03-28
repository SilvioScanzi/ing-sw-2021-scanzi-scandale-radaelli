package it.polimi.ingsw;
import java.util.*;

public class Slot {
    private Stack<DevelopmentCard> developmentcards;

    public Slot() {
        developmentcards = new Stack<DevelopmentCard>();
    }

    public Optional<DevelopmentCard> getFirstCard(){
        if(developmentcards.empty())
            return Optional.empty();
        return Optional.of(developmentcards.peek());
    }

    public Stack<DevelopmentCard> getDevelopmentcards() {
        return developmentcards;
    }

    public void addCard(DevelopmentCard DC) throws InvalidPlacementException{
        if(developmentcards.empty()){
            if(DC.getLevel()==1)  developmentcards.push(DC);
            else throw new InvalidPlacementException("Not level 1 card");
        }
        else if(developmentcards.peek().getLevel() == DC.getLevel() - 1)  developmentcards.push(DC);
        else throw new InvalidPlacementException("Not correct card level");
    }
}
