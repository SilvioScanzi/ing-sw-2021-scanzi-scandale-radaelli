package it.polimi.ingsw.observers;

import it.polimi.ingsw.model.Colours;
import it.polimi.ingsw.model.Pair;
import it.polimi.ingsw.model.Resources;
import it.polimi.ingsw.model.Triplet;

import java.util.ArrayList;
import java.util.HashMap;
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

    public void notifySetupDiscardLC(int[] index){
        for(ViewObserver obs : observers){
            obs.updateSetupDiscardLC(this,index);
        }
    }

    public void notifyFinishSetup(ArrayList<String> message){
        for(ViewObserver obs : observers){
            obs.updateFinishSetup(this,message);
        }
    }

    public void notifyBuyResources(boolean r,int n,ArrayList<Integer> requestedWMConversion){
        for(ViewObserver obs : observers){
            obs.updateBuyResources(r,n,requestedWMConversion);
        }
    }

    public void notifyBuyDC(Colours colour, int level, int slot,ArrayList<Pair<String, Integer>> userChoice){
        for(ViewObserver obs : observers){
            obs.updateBuyDC(colour,level,slot,userChoice);
        }
    }

    public void notifyActivateProduction(HashMap<Integer, ArrayList<Pair<String,Integer>>> userChoice){
        for(ViewObserver obs : observers){
            obs.updateActivateProduction(userChoice);
        }
    }

    public void notifyMoveResources(ArrayList<Triplet<String,Integer,Integer>> userChoice){
        for(ViewObserver obs : observers){
            obs.updateMoveResources(userChoice);
        }
    }

    public void notifyActivateLC(int userChoice){
        for(ViewObserver obs : observers){
            obs.updateActivateLC(userChoice);
        }
    }

    public void notifyDiscardLC(int userChoice){
        for(ViewObserver obs : observers){
            obs.updateDiscardLC(userChoice);
        }
    }

    public void notifyEndTurn(){
        for(ViewObserver obs : observers){
            obs.updateEndTurn();
        }
    }
}
