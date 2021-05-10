package it.polimi.ingsw.view;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.HashMap;

public interface View {
    void setCanInput(boolean a);
    boolean getMessageReady();
    String getMessage();
    void setMessageReady(boolean a);
    void playerTurn();
    void setYourTurn(boolean a);
    void yourTurnPrint();

    //print
    void printResourceMarket(Marbles[][] a, Marbles b);
    void printLeaderCardHand(ArrayList<Triplet<Resources,Integer,Integer>> LC);
    void printLeaderCardPlayed(ArrayList<Triplet<Resources,Integer,Integer>> LC, String nickname);
    void printResourceHand(ArrayList<Resources> H, String nickname);
    void printAT(ActionToken AT);
    void printBlackCross(int BC);
    void printCardMarket(HashMap<Pair<Colours,Integer>, Integer> CM);
    void printFaithTrack(int FM, boolean[] PF, String nickname);
    void printSlot(int I, Colours C, int VP, String nickname);
    void printStrongBox(HashMap<Resources,Integer> SB, String nickname);
    void printWarehouse(HashMap<Integer,Pair<Resources,Integer>> WH, String nickname);
}
