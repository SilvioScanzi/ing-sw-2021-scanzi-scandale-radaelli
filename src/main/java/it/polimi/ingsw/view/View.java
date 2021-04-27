package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Marbles;
import it.polimi.ingsw.model.Resources;
import it.polimi.ingsw.model.Triplet;

import java.util.ArrayList;

public interface View {
    void setCanInput(boolean a);
    boolean getMessageReady();
    String getMessage();
    void setMessageReady(boolean a);
    void printMarket(Marbles[][] a,Marbles b);
    void printLC(ArrayList<Triplet<Resources,Integer,Integer>> a);
    void playerTurn();
    void setYourTurn(boolean a);
    void yourTurnPrint();
}
