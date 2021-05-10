package it.polimi.ingsw.observers;

import java.util.ArrayList;
import java.util.List;

public class ViewObservable {
    private final List<ViewObserver> observers = new ArrayList<>();

    public void addObserver(ViewObserver observer){
        synchronized (observers) {
            observers.add(observer);
        }
    }

    public void notifyNickname(String message){
        for(ViewObserver obs : observers){
            obs.updateNickname(this,message);
        }
    }

    public void notifyPlayerNumber(int num){
        for(ViewObserver obs : observers){
            obs.updatePlayerNumber(this,num);
        }
    }

    public void notifyDiscardLC(int[] index){
        for(ViewObserver obs : observers){
            obs.updateDiscardLC(this,index);
        }
    }

    public void notifyFinishSetup(ArrayList<String> message){
        for(ViewObserver obs : observers){
            obs.updateFinishSetup(this,message);
        }
    }

    public void notifyTurn(String message){
        for(ViewObserver obs : observers){
            obs.updateMyTurn(this,message);
        }
    }

    public void notifyDisconnected(){
        for(ViewObserver obs : observers){
            obs.updateDisconnected(this);
        }
    }
}
