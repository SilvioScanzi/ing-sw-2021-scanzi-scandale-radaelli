package it.polimi.ingsw.view;

import it.polimi.ingsw.commons.*;
import it.polimi.ingsw.network.messages.StandardMessages;
import it.polimi.ingsw.view.clientModel.ClientBoard;

import java.util.ArrayList;
import java.util.HashMap;

public interface View {
    void setState(CLI.ViewState state);

    //print
    void printStandardMessage(StandardMessages message);
    void printNames(HashMap<String, Integer> names, int inkwell);
    void printResourceMarket(Marbles[][] M, Marbles RM);
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
    void print(String string);
    void printBoard(ClientBoard board);
}
