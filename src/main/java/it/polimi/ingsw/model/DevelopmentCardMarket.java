package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Colours;
import it.polimi.ingsw.commons.Pair;
import it.polimi.ingsw.exceptions.EmptyException;
import it.polimi.ingsw.commons.DevelopmentCardParser;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;


public class DevelopmentCardMarket {
    private final HashMap<Pair<Colours,Integer>,Stack<DevelopmentCard>> cardMarket; //attributes: colour, level, cards

    public DevelopmentCardMarket() {
        //Instantiating one stack for every card deck (One for every combination of Color-Level)
        cardMarket = new HashMap<>();
        for (Colours c : Colours.values()) {
            for (int i = 1; i <= 3; i++) {
                cardMarket.put(new Pair<>(c, i), new Stack<>());
            }
        }

        DevelopmentCardParser DCP = new DevelopmentCardParser();

        ArrayList<DevelopmentCard> tmp = DCP.parseFromXML();
        //Getting the 48 cards from the arrayList tmp, inserting them into the cardMarket in a random order for every stack
        for (int i = 0; i < 48; i++) {
            int index;
            index = (int) (Math.random() * (48 - i));
            cardMarket.get(new Pair<>(tmp.get(index).getColour(), tmp.get(index).getLevel())).push(tmp.remove(index));
        }

    }

    /**
     * Method used for testing, it initialize a card market with a standard configuration
     * @param Arandom is just used to overload the method
     */
    public DevelopmentCardMarket(int Arandom) {
        //Instantiating one stack for every card deck (One for every combination of Color-Level)
        cardMarket = new HashMap<>();
        for (Colours c : Colours.values()) {
            for (int i = 1; i <= 3; i++) {
                cardMarket.put(new Pair<>(c, i), new Stack<>());
            }
        }

        DevelopmentCardParser DCP = new DevelopmentCardParser();

        ArrayList<DevelopmentCard> tmp = DCP.parseFromXML();
        //Getting the 48 cards from the arrayList tmp, inserting them into the cardMarket in a random order for every stack
        for (int i = 0; i < 48; i++) {
            cardMarket.get(new Pair<>(tmp.get(0).getColour(), tmp.get(0).getLevel())).push(tmp.remove(0));
        }

    }

    public int cardsInStack(Colours colour, int level){
        return cardMarket.get(new Pair<>(colour,level)).size();
    }

    /**
     * Method used to check the cost of the requested card without removing it from the stack
     * @param colour colour of the card to check
     * @param level level of the card to check
     * @return DevelopmentCard with that colour and level
     * @throws EmptyException if the stack of cards identified with colour and level requested is empty
     * @throws IndexOutOfBoundsException if the level chosen isn't between 1 and 3
     */
    public DevelopmentCard peekFirstCard(Colours colour, int level) throws EmptyException, IndexOutOfBoundsException {
        if(level<1 || level>3) throw new IndexOutOfBoundsException();
        Pair<Colours,Integer> tmp1 = new Pair<>(colour,level);
        if(cardMarket.get(tmp1).empty()) throw new EmptyException("There aren't any cards in this stack");
        return cardMarket.get(tmp1).peek();
    }

    /**
     * Method used to remove the card from the requested stack. Used only when it is certain that a player can buy that card
     * @param colour colour of the card to requested
     * @param level level of the card to requested
     * @return DevelopmentCard with that colour and level
     */
    public DevelopmentCard getFirstCard(Colours colour, int level){
        Pair<Colours,Integer> tmp1 = new Pair<>(colour,level);
        DevelopmentCard DC = cardMarket.get(tmp1).pop();
        return DC;
    }

    //Only used in single player

    /**
     * Method used in single player, when an action token is drawn. Deletes the cards of the given colour,
     * going from the lowest level to the highest in order
     * @param colour colour of the cards to remove
     */
    public void deleteCards(Colours colour){
        int count = 2;  //cards to be deleted
        boolean Empty;
        for(int i=0; i<3 && count>0; i++){
            Empty = false;
            while(!Empty && count>0) {
                if (cardMarket.get(new Pair<>(colour, i + 1)).size() > 0) {
                    cardMarket.get(new Pair<>(colour, i + 1)).pop();
                    count--;
                } else Empty = true;
            }
        }
    }

    /**
     * Method used to check if lorenzo has won by removing all the cards of one colour
     * @return true if he wins, false otherwise
     */
    public boolean lorenzoWin(){
        for(Colours c : Colours.values()){
            if(cardMarket.get(new Pair<>(c,3)).size()==0) return true;
        }
        return false;
    }
}
