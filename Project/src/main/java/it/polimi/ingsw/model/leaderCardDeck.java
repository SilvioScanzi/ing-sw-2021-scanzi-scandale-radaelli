package it.polimi.ingsw.model;

import it.polimi.ingsw.Game;

import java.util.*;

public class leaderCardDeck {
    private Stack<LeaderCard> deck;

    public leaderCardDeck(){
        ArrayList<LeaderCard> tmp = new ArrayList<>();

        tmp.add(new LeaderCard(false,false,4,
                    new HashMap<Game.Colours,Pair<Integer,Integer>>() {{put(Game.Colours.Green,new Pair<>(2,1));}},
                    new HashMap<Game.Resources,Integer>(),
                        new Ability(Ability.AbilityType.DiscountAbility,Game.Resources.Coins))
        );

        //fare un xml per tutte le carte
        for(int i=0;i<16;i++){
            int index;
            index = (int) (Math.random() * (3-i));
            deck.push(tmp.remove(index));
       }
    }

    private LeaderCard getFirstCard() throws IllegalArgumentException{
        if(!(deck.isEmpty())) return deck.pop();
        else throw new IllegalArgumentException("No more leader cards");
    }

    public ArrayList<LeaderCard> getLeaderCards(){
        ArrayList<LeaderCard> tmp = new ArrayList<>();
        for(int i=0;i<4;i++){
            tmp.add(getFirstCard());
        }
        return tmp;
    }
}
