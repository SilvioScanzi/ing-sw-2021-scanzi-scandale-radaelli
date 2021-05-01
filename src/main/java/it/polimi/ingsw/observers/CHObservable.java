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

    public void notifyLobby(Object o){
        for(CHObserver obs : observers){
            obs.updateLobby(this,o);
        }
    }
}
