package it.polimi.ingsw.observers;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;

public class ModelObservable {
    private final List<ModelObserver> observers = new ArrayList<>();

    public void addObserver(ModelObserver observer){
        synchronized (observers) {
            observers.add(observer);
        }
    }

    public void notify(Object obj){
        for(ModelObserver obs : observers){
            obs.update(this,obj);
        }
    }
}
