package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.LeaderCardParser;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.*;

public class LeaderCardDeck {
    private Stack<LeaderCard> deck;

    public LeaderCardDeck() {
        deck = new Stack<>();
        LeaderCardParser LCP = new LeaderCardParser();
        try {
            ArrayList<LeaderCard> tmp = LCP.parseFromXML();
            for (int i = 0; i < 16; i++) {
                deck.push(tmp.remove((int) (Math.random() * (16-i))));
            }
        }
        catch(IOException | SAXException e){e.printStackTrace();}
    }

    /**
     * Method used for testing, it initialize a leader card deck with a standard configuration
     * @param Arandom is just used to overload the method
     */
    public LeaderCardDeck(int Arandom){
        deck = new Stack<>();
        LeaderCardParser LCP = new LeaderCardParser();
        try {
            ArrayList<LeaderCard> tmp = LCP.parseFromXML();
            for (int i = 0; i < 16; i++) {
                deck.push(tmp.remove(0));
            }
        }
        catch(IOException | SAXException e){e.printStackTrace();}
    }

    public LeaderCard getFirstCard(){
        return deck.pop();
    }

    /**
     * Method used in the setup phase to give 4 leader cards to each player
     * @return an Hashmap with 4 leader card and the relative index
     */
    public HashMap<Integer,LeaderCard> getLeaderCards(){
        HashMap<Integer,LeaderCard> tmp = new HashMap<>();
        for(int i=0;i<4;i++){
            tmp.put(i,getFirstCard());
        }
        return tmp;
    }
}
