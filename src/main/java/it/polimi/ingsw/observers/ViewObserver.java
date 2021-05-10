package it.polimi.ingsw.observers;

import java.util.ArrayList;

public interface ViewObserver {
    void updateNickname(ViewObservable obs, String message);
    void updatePlayerNumber(ViewObservable obs, int num);
    void updateDiscardLC(ViewObservable obs, int index[]);
    void updateFinishSetup(ViewObservable obs, ArrayList<String> message);
    void updateMyTurn(ViewObservable obs, String message);
    void updateDisconnected(ViewObservable obs);
}
