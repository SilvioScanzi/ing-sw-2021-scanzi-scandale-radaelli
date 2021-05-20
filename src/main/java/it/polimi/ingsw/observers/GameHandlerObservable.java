package it.polimi.ingsw.observers;

import java.util.ArrayList;
import java.util.List;

public class GameHandlerObservable {
    private final List<GameHandlerObserver> observers = new ArrayList<>();

    public void addObserver(GameHandlerObserver observer){
        synchronized (observers) {
            observers.add(observer);
        }
    }

    public void gameHandlerNotify(){
        for(GameHandlerObserver obs : observers){
            obs.gameHandlerUpdate(this);
        }
    }
}
