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

    //Only used for testing
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

    public HashMap<Integer,LeaderCard> getLeaderCards(){
        HashMap<Integer,LeaderCard> tmp = new HashMap<>();
        for(int i=0;i<4;i++){
            tmp.put(i,getFirstCard());
        }
        return tmp;
    }
}
