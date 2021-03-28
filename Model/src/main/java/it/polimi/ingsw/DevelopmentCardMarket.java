package it.polimi.ingsw;
import java.util.*;

public class DevelopmentCardMarket {
    private Map<Pair<Game.Colours,Integer>,Stack<DevelopmentCard>> cardMarket;

    public DevelopmentCardMarket(){
        ArrayList<DevelopmentCard> tmp = new ArrayList<>();
        Pair<Game.Colours,Integer> tmp1 = new Pair<>(Game.Colours.Green,1);

        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }

        tmp1 = new Pair<> (Game.Colours.Green,2);
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }

        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }

        tmp1 = new Pair<> (Game.Colours.Green,2);
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }

        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }

        tmp1 = new Pair<> (Game.Colours.Green,2);
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }

        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }

        tmp1 = new Pair<> (Game.Colours.Green,2);
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }

        tmp1 = new Pair<> (Game.Colours.Green,2);
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }

        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }

        tmp1 = new Pair<> (Game.Colours.Green,2);
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }
    }

    public DevelopmentCard getFirstCard(Game.Colours colour, int level) throws IllegalArgumentException{
        Pair<Game.Colours,Integer> tmp1 = new Pair<>(colour,level);
        if(cardMarket.get(tmp1).empty()) throw new IllegalArgumentException("There's no card on the stack");
        return cardMarket.get(tmp1).peek();
    }
}
