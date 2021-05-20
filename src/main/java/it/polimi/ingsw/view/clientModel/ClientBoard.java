package it.polimi.ingsw.view.clientModel;

import it.polimi.ingsw.commons.Colours;
import it.polimi.ingsw.commons.Pair;
import it.polimi.ingsw.commons.Resources;
import it.polimi.ingsw.commons.Triplet;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientBoard {
    private final int position;
    private final String nickname;
    private final HashMap<Integer, Pair<Resources,Integer>> warehouse;
    private final ArrayList<Pair<Colours, Integer>> slots;
    private HashMap<Resources,Integer> strongBox;
    private ArrayList<Triplet<Resources,Integer,Integer>> leaderCardsHand;
    private ArrayList<Triplet<Resources,Integer,Integer>> leaderCardsPlayed;
    private ArrayList<Resources> hand;
    private boolean actionDone;
    private int FaithMarker;
    private boolean[] popeFavor;

    public ClientBoard(int position, String nickname){
        this.position = position;
        this.nickname = nickname;
        strongBox = new HashMap<>();
        leaderCardsHand = new ArrayList<>();
        leaderCardsPlayed = new ArrayList<>();
        warehouse = new HashMap<>();
        strongBox = new HashMap<>();
        leaderCardsHand = new ArrayList<>();
        leaderCardsPlayed = new ArrayList<>();
        hand = new ArrayList<>();
        slots = new ArrayList<>();
        slots.add(new Pair<>(Colours.Purple,-1));
        slots.add(new Pair<>(Colours.Purple,-1));
        slots.add(new Pair<>(Colours.Purple,-1));
        FaithMarker = 0;
        popeFavor = new boolean[3];
    }
    public void setActionDone(boolean actionDone) {
        this.actionDone = actionDone;
    }
    public void setLeaderCardsHand(ArrayList<Triplet<Resources, Integer, Integer>> leaderCardsHand) {
        this.leaderCardsHand = leaderCardsHand;
    }

    public void setLeaderCardsPlayed(ArrayList<Triplet<Resources, Integer, Integer>> leaderCardsPlayed) {
        this.leaderCardsPlayed = leaderCardsPlayed;
    }

    public void setHand(ArrayList<Resources> hand) {
        this.hand = hand;
    }

    public void setSlot(int i, Colours c, int vp){
        slots.set(i,new Pair<>(c,vp));
    }

    public void setStrongBox(HashMap<Resources, Integer> strongBox) {
        this.strongBox = strongBox;
    }

    public void setWarehouse(ArrayList<Triplet<Integer, Resources, Integer>> warehouse) {
        this.warehouse.clear();
        for(Triplet<Integer, Resources, Integer> T : warehouse){
            this.warehouse.put(T.get_1(),new Pair<>(T.get_2(),T.get_3()));
        }
    }

    public void setFaithMarker(int faithMarker) {
        FaithMarker = faithMarker;
    }

    public void setPopeFavor(boolean[] popeFavor) {
        this.popeFavor = popeFavor;
    }

    public HashMap<Integer, Pair<Resources, Integer>> getWarehouse() {
        return warehouse;
    }

    public Integer getSlotsVP(int i) {
        return slots.get(i-1).getValue();
    }

    public Pair<Colours,Integer> getSlots(int i) throws IndexOutOfBoundsException {
        return slots.get(i-1);
    }

    public boolean getActionDone() {
        return actionDone;
    }

    public ArrayList<Triplet<Resources, Integer, Integer>> getLeaderCardsHand() {
        return leaderCardsHand;
    }

    public ArrayList<Triplet<Resources, Integer, Integer>> getLeaderCardsPlayed() {
        return leaderCardsPlayed;
    }

    public HashMap<Resources, Integer> getStrongBox() {
        return strongBox;
    }

    public int getPosition() {
        return position;
    }

    public String getNickname() {
        return nickname;
    }

    public int getFaithMarker() {
        return FaithMarker;
    }

    public boolean[] getPopeFavor() {
        return popeFavor;
    }
}
