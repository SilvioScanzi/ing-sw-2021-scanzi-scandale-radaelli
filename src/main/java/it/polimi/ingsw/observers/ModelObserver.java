package it.polimi.ingsw.observers;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;

public interface ModelObserver {
    void update(ModelObservable obs,Object obj);
}
