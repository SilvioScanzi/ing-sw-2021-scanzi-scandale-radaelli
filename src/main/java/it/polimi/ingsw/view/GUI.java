package it.polimi.ingsw.view;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.messages.StandardMessages;
import it.polimi.ingsw.observers.ViewObservable;

import java.util.ArrayList;
import java.util.HashMap;

public class GUI extends ViewObservable implements View, Runnable{
    public GUI(){

    }

    @Override
    public void setState(CLI.ViewState state) {

    }

    @Override
    public void print(String string) {

    }

    @Override
    public void printStandardMessage(StandardMessages message) {

    }

    @Override
    public void printResourceMarket(Marbles[][] a, Marbles b) {

    }

    @Override
    public void printLeaderCardHand(ArrayList<Triplet<Resources, Integer, Integer>> LC) {

    }

    @Override
    public void printLeaderCardPlayed(ArrayList<Triplet<Resources, Integer, Integer>> LC, String nickname) {

    }

    @Override
    public void printResourceHand(ArrayList<Resources> H, String nickname) {

    }

    @Override
    public void printAT(ActionToken AT) {

    }

    @Override
    public void printBlackCross(int BC) {

    }

    @Override
    public void printCardMarket(HashMap<Pair<Colours, Integer>, Integer> CM) {

    }

    @Override
    public void printFaithTrack(int FM, boolean[] PF, String nickname) {

    }

    @Override
    public void printSlot(int I, Colours C, int VP, String nickname) {

    }

    @Override
    public void printStrongBox(HashMap<Resources, Integer> SB, String nickname) {

    }

    @Override
    public void printWarehouse(HashMap<Integer, Pair<Resources, Integer>> WH, String nickname) {

    }

    @Override
    public void run() {

    }
}
