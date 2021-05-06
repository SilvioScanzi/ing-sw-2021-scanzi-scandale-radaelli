package it.polimi.ingsw.observers;

import java.util.ArrayList;
import java.util.List;

public class CHObservable {
    private final List<CHObserver> observers = new ArrayList<>();

    public void addObserver(CHObserver observer){
        synchronized (observers) {
            observers.add(observer);
        }
    }

    public void notify(Object o){
        for(CHObserver obs : observers){
            obs.update(this,o);
        }
    }

    public void removeObserver(CHObserver observer){
        synchronized (observers) {
            observers.remove(observer);
        }
    }
}
