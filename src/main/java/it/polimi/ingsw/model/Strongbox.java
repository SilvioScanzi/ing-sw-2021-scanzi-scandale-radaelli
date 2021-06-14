package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Resources;
import it.polimi.ingsw.exceptions.ResourceErrorException;

import java.util.*;

public class Strongbox implements Cloneable {
    private final HashMap<Resources,Integer> storage;

    public Strongbox() {
        storage = new HashMap<>();
        storage.put(Resources.Coins,0);
        storage.put(Resources.Stones,0);
        storage.put(Resources.Servants,0);
        storage.put(Resources.Shields,0);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Strongbox)) {
            return false;
        }
        Strongbox s = (Strongbox) o;
        for(Resources R : Resources.values()){
            if(!storage.get(R).equals(s.storage.get(R))) return false;
        }
        return true;
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

    public HashMap<Resources, Integer> getStorage() {
        return storage;
    }

    public int getResource(Resources r){
        return storage.get(r);
    }

    public void addResource(Resources r, int amount){
        storage.put(r,amount+storage.get(r));
    }

    public void subResource(Resources r, int amount) throws ResourceErrorException {
        if(storage.get(r)-amount >= 0) storage.put(r,storage.get(r)-amount);
        else throw new ResourceErrorException("There aren't enough resources");
    }
}