package it.polimi.ingsw.observers;

import it.polimi.ingsw.network.messages.ChoosePlayerNumberMessage;
import it.polimi.ingsw.network.messages.NicknameMessage;

public interface CH_ServerObserver {
    void updateServerDisconnection(CHObservable obs);
    void updateServerPlayerNumber(CHObservable obs, ChoosePlayerNumberMessage message);
    void updateServerNickname(CHObservable obs, NicknameMessage message);
    void updateServerReconnection(CHObservable obs);
}
