package it.polimi.ingsw;
import java.util.*;

public class Slot {
    private Stack<DevelopmentCard> developmentcards;

    public Optional<DevelopmentCard> getFirstCard(){
        if(developmentcards.empty())
            return Optional.empty();
        return Optional.of(developmentcards.peek());
    }

    public Stack<DevelopmentCard> getDevelopmentcards() {
        return developmentcards;
    }

    public void addCard(DevelopmentCard DC){
        //da implementare
    }
}
