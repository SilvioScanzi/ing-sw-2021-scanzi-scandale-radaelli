package it.polimi.ingsw.observers;

import it.polimi.ingsw.commons.Colours;
import it.polimi.ingsw.commons.Pair;
import it.polimi.ingsw.commons.Triplet;

import java.util.ArrayList;
import java.util.HashMap;

public interface ViewObserver {
    void updateNickname(ViewObservable obs, String message);
    void updatePlayerNumber(ViewObservable obs, int num);
    void updateSetupDiscardLC(ViewObservable obs, int[] index);
    void updateFinishSetup(ViewObservable obs, ArrayList<String> message);
    void updateAddress(String IP,int port);
    void updateDisconnected(ViewObservable obs);    //TODO: fargli scegliere se disconnettersi
    void updateBuyResources(boolean r,int n,ArrayList<Integer> requestedWMConversion);
    void updateBuyDC(Colours colour, int level, int slot, ArrayList<Pair<String, Integer>> userChoice);
    void updateActivateProduction(HashMap<Integer, ArrayList<Pair<String,Integer>>> userChoice);
    void updateMoveResources(ArrayList<Triplet<String,Integer,Integer>> userChoice);
    void updateActivateLC(int userChoice);
    void updateDiscardLC(int userChoice);
    void updateEndTurn();
    void updatePrintRequest(String message);
    void updateReconnection(boolean r);
    void updateAnotherGame();
    void updateDemolish();
}
