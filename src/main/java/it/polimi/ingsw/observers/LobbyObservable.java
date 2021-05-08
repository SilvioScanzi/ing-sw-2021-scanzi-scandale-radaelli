package it.polimi.ingsw.observers;

import java.util.ArrayList;
import java.util.List;

public class LobbyObservable {
    private final List<LobbyObserver> observers = new ArrayList<>();

    public void addObserver(LobbyObserver observer){
        synchronized (observers) {
            observers.add(observer);
        }
    }

    public void lobbyNotify(){
        for(LobbyObserver obs : observers){
            obs.lobbyUpdate(this);
        }
    }
}
