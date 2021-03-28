package it.polimi.ingsw;
import java.util.*;

public class Strongbox {
    private Map<Game.Resources,Integer> storage;

    public Strongbox() {
        storage.put(Game.Resources.Coins,0);
        storage.put(Game.Resources.Stones,0);
        storage.put(Game.Resources.Servants,0);
        storage.put(Game.Resources.Shields,0);
    }

    public int getResource(Game.Resources r){
        return storage.get(r);
    }

    public void updateResource(Game.Resources r, int amount) throws ResourceErrorException{
        int curr = storage.get(r);
        if(amount + curr < 0)
            throw new ResourceErrorException("Not enough resources");
        storage.put(r,amount+curr);
    }
}
