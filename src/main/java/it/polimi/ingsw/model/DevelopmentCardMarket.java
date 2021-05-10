package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyException;
import it.polimi.ingsw.observers.ModelObservable;
import it.polimi.ingsw.utils.DevelopmentCardParser;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

        DevelopmentCardParser DCP = new DevelopmentCardParser("src/xml_src/developmentCards.xml");
        try {
            ArrayList<DevelopmentCard> tmp = DCP.parseFromXML();
            //Getting the 48 cards from the arrayList tmp, inserting them into the cardMarket in a random order for every stack
            for (int i = 0; i < 48; i++) {
                int index;
                index = (int) (Math.random() * (48 - i));
                cardMarket.get(new Pair<>(tmp.get(index).getColour(), tmp.get(index).getLevel())).push(tmp.remove(index));
            }
        }
        catch(IOException | SAXException e){e.printStackTrace();}
    }

    //Only used for testing
    public DevelopmentCardMarket(int Arandom){
        //Instantiating one stack for every card deck (One for every combination of Color-Level)
        cardMarket = new HashMap<>();
        for (Colours c : Colours.values()) {
            for (int i = 1; i <= 3; i++) {
                cardMarket.put(new Pair<>(c, i), new Stack<>());
            }
        }

        DevelopmentCardParser DCP = new DevelopmentCardParser("src/xml_src/developmentCards.xml");
        try {
            ArrayList<DevelopmentCard> tmp = DCP.parseFromXML();
            //Getting the 48 cards from the arrayList tmp, inserting them into the cardMarket in a random order for every stack
            for (int i = 0; i < 48; i++) {
                cardMarket.get(new Pair<>(tmp.get(0).getColour(), tmp.get(0).getLevel())).push(tmp.remove(0));
            }
        }
        catch(IOException | SAXException e){e.printStackTrace();}
    }

    @Override
    public String toString(){
        String tmp = "Mercato delle carte:\n";
        for(int i=1;i<=3;i++){
            for(Colours c : Colours.values()){
                try {
                    tmp = tmp.concat(peekFirstCard(c, i).toString() + "\n\n");
                }
                catch(EmptyException e){tmp = tmp.concat("Le carte di livello "+i+" e colore "+c.toString()+" sono finite");}
            }
        }
        return tmp;
    }

    //Peek doesn't remove the card from the relative deck, it's used to check the cost of the card
    public DevelopmentCard peekFirstCard(Colours colour, int level) throws EmptyException, IndexOutOfBoundsException {
        if(level<1 || level>3) throw new IndexOutOfBoundsException();
        Pair<Colours,Integer> tmp1 = new Pair<>(colour,level);
        if(cardMarket.get(tmp1).empty()) throw new EmptyException("Non ci sono più carte in questa pila");
        return cardMarket.get(tmp1).peek();
    }

    //Get used when it's sure that the player can buy the card and thus remove it from the market
    public DevelopmentCard getFirstCard(Colours colour, int level){
        Pair<Colours,Integer> tmp1 = new Pair<>(colour,level);
        DevelopmentCard DC = cardMarket.get(tmp1).pop();
        return DC;
    }

    //Only used in single player
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

    public boolean lorenzoWin(){
        for(Colours c : Colours.values()){
            if(cardMarket.get(new Pair<>(c,3)).size()==0) return true;
        }
        return false;
    }
}
