package it.polimi.ingsw.model;

import it.polimi.ingsw.Game;

import java.util.*;

public class Strongbox implements Cloneable {
    private HashMap<Game.Resources,Integer> storage;

    public Strongbox() {
        storage = new HashMap<>();
        storage.put(Game.Resources.Coins,0);
        storage.put(Game.Resources.Stones,0);
        storage.put(Game.Resources.Servants,0);
        storage.put(Game.Resources.Shields,0);
    }

    @Override
    public Strongbox clone() {
        Strongbox tmp = new Strongbox();
        tmp.storage.put(Game.Resources.Coins,this.getResource(Game.Resources.Coins));
        tmp.storage.put(Game.Resources.Stones,this.getResource(Game.Resources.Stones));
        tmp.storage.put(Game.Resources.Servants,this.getResource(Game.Resources.Servants));
        tmp.storage.put(Game.Resources.Shields,this.getResource(Game.Resources.Shields));
        return tmp;
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
