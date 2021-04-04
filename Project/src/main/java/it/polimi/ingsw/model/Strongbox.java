package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.ResourceErrorException;

import java.util.*;

public class Strongbox implements Cloneable {
    private HashMap<Resources,Integer> storage;

    public Strongbox() {
        storage = new HashMap<>();
        storage.put(Resources.Coins,0);
        storage.put(Resources.Stones,0);
        storage.put(Resources.Servants,0);
        storage.put(Resources.Shields,0);
    }

    @Override
    public Strongbox clone() {
        Strongbox tmp = new Strongbox();
        tmp.storage.put(Resources.Coins,this.getResource(Resources.Coins));
        tmp.storage.put(Resources.Stones,this.getResource(Resources.Stones));
        tmp.storage.put(Resources.Servants,this.getResource(Resources.Servants));
        tmp.storage.put(Resources.Shields,this.getResource(Resources.Shields));
        return tmp;
    }

    @Override
    public String toString(){
        String tmp = "Cassa:";
        for(Resources r:storage.keySet()){
            tmp=tmp.concat("\n"+r.toString()+" "+storage.get(r));
        }
        return tmp;
    }

    public int getResource(Resources r){
        return storage.get(r);
    }

    /*public void updateResource(Resources r, int amount) throws ResourceErrorException{
        int curr = storage.get(r);
        if(amount + curr < 0)
            throw new ResourceErrorException("Not enough resources");
        storage.put(r,amount+curr);
    }*/

    public void addResource(Resources r, int amount){
        storage.put(r,amount+storage.get(r));
    }

    public Pair<Resources,Integer> subResource(Resources r, int amount) throws ResourceErrorException {
        int curr = storage.get(r);
        if(amount > curr) throw new ResourceErrorException("Not enough resources",r,curr-amount);
        storage.put(r,curr-amount);
        return new Pair<>(r,amount);
    }
}
