package it.polimi.ingsw.model;
import it.polimi.ingsw.commons.Colours;
import it.polimi.ingsw.commons.Pair;
import it.polimi.ingsw.exceptions.EmptyException;
import it.polimi.ingsw.exceptions.InvalidPlacementException;

import java.util.*;

public class Slot {
    private final ArrayList<DevelopmentCard> developmentCards;

    public Slot() {
        developmentCards = new ArrayList<>();
    }

    /**
     * Method used to get all the cards in the slot
     * @return List of cards (identified with colour and level) contained in the slot
     */
    public ArrayList<Pair<Colours,Integer>> getList(){
        ArrayList<Pair<Colours,Integer>> tmp = new ArrayList<>();
        for(DevelopmentCard DC : developmentCards){
            tmp.add(new Pair<>(DC.getColour(), DC.getLevel()));
        }
        return tmp;
    }
    /**
     * Method used to get all the cards in the slot
     * @return List of cards (identified with colour and victory points) contained in the slot
     */
    public ArrayList<Pair<Colours,Integer>> getSlotListVP(){
        ArrayList<Pair<Colours,Integer>> tmp = new ArrayList<>();
        for(DevelopmentCard DC : developmentCards){
            tmp.add(new Pair<>(DC.getColour(), DC.getVictoryPoints()));
        }
        return tmp;
    }

    /**
     * Method used to get the development card on top of the stack
     * @return the Development card on top of the stack
     * @throws EmptyException if the slot is empty
     */
    public DevelopmentCard getFirstCard() throws EmptyException {
        if(developmentCards.size()==0) throw new EmptyException("Slot is empty");
        else return developmentCards.get(developmentCards.size()-1);
    }

    public ArrayList<DevelopmentCard> getDevelopmentCards() {
        return developmentCards;
    }

    /**
     * Method used to add a card to the slot
     * @param DC the card to add
     * @throws InvalidPlacementException if the card cannot be put in this slot (violation of rules on the slot)
     */
    public void addCard(DevelopmentCard DC) throws InvalidPlacementException {
        if(developmentCards.size()==0){
            if(DC.getLevel()==1)  developmentCards.add(DC);
            else throw new InvalidPlacementException("Cannot play the selected card on this slot");
        }
        else if(developmentCards.get(developmentCards.size()-1).getLevel() == DC.getLevel() - 1)  developmentCards.add(DC);
        else throw new InvalidPlacementException("Cannot play the selected card on this slot");
    }
}
