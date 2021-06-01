package it.polimi.ingsw.observers;

import it.polimi.ingsw.network.messages.*;

public interface CHObserver {
    void updateDisconnected(CHObservable obs);
    void updateLCDiscard(CHObservable obs, DiscardLeaderCardSetupMessage message);
    void updateFinishSetup(CHObservable obs, FinishSetupMessage message);
    void updateBuyResources(CHObservable obs, BuyResourcesMessage message);
    void updateBuyDC(CHObservable obs, BuyDevelopmentCardMessage message);
    void updateProduction(CHObservable obs, ProductionMessage message);
    void updateMoveResources(CHObservable obs, MoveResourcesMessage message);
    void updatePlayLeaderCard(CHObservable obs, PlayLeaderCardMessage message);
    void updateDiscardLeaderCard(CHObservable obs, DiscardLeaderCardMessage message);
    void updateTurnDone(CHObservable obs);
}
