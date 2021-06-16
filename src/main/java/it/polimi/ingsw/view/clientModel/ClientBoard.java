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
    private ArrayList<Pair<Colours, Integer>> slot_1;
    private ArrayList<Pair<Colours, Integer>> slot_2;
    private ArrayList<Pair<Colours, Integer>> slot_3;
    private HashMap<Resources,Integer> strongBox;
    private ArrayList<Triplet<Resources,Integer,Integer>> leaderCardsHand;
    private ArrayList<Triplet<Resources,Integer,Integer>> leaderCardsPlayed;
    private ArrayList<Resources> hand;
    private boolean actionDone;
    private int FaithMarker;
    private int lorenzoMarker;
    private boolean[] popeFavor;

    public ClientBoard(int position, String nickname){
        this.position = position;
        this.nickname = nickname;
        strongBox = new HashMap<>();
        strongBox.put(Resources.Coins,0);
        strongBox.put(Resources.Shields,0);
        strongBox.put(Resources.Servants,0);
        strongBox.put(Resources.Stones,0);
        warehouse = new HashMap<>();
        leaderCardsHand = new ArrayList<>();
        leaderCardsPlayed = new ArrayList<>();
        hand = new ArrayList<>();
        slot_1 = new ArrayList<>();
        slot_2 = new ArrayList<>();
        slot_3 = new ArrayList<>();
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

    public void setSlot(ArrayList<ArrayList<Pair<Colours, Integer>>> slots){
        this.slot_1 = slots.get(0);
        this.slot_2 = slots.get(1);
        this.slot_3 = slots.get(2);
    }

    public void setStrongBox(HashMap<Resources, Integer> strongBox) {
        this.strongBox = strongBox;
        for(Resources R : strongBox.keySet()){
            if(!strongBox.containsKey(R)) strongBox.put(R,0);
        }
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

    public void setLorenzoMarker(int lorenzoMarker) {
        this.lorenzoMarker = lorenzoMarker;
    }

    public HashMap<Integer, Pair<Resources, Integer>> getWarehouse() {
        return warehouse;
    }

    public Integer getSlotsVP(int i) {
        switch(i){
            case 1 -> {
                if(slot_1.size()>0) return slot_1.get(slot_1.size()-1).getValue();
                else return -1;
            }
            case 2 -> {
                if(slot_2.size()>0) return slot_2.get(slot_2.size()-1).getValue();
                else return -1;
            }
            case 3 -> {
                    if(slot_3.size()>0) return slot_3.get(slot_3.size()-1).getValue();
                    else return -1;
                }
            default -> {return -1;}
        }
    }

    public ArrayList<ArrayList<Pair<Colours, Integer>>> getSlots(){
        ArrayList<ArrayList<Pair<Colours, Integer>>> tmp = new ArrayList<>();
        tmp.add(slot_1);
        tmp.add(slot_2);
        tmp.add(slot_3);
        return tmp;
    }

    public ArrayList<Pair<Colours, Integer>> getSlot_1(){
        return slot_1;
    }

    public ArrayList<Pair<Colours, Integer>> getSlot_2(){
        return slot_2;
    }

    public ArrayList<Pair<Colours, Integer>> getSlot_3(){
        return slot_3;
    }

    public Pair<Colours,Integer> getSlots(int i) throws IndexOutOfBoundsException {
        return switch(i){
            case 1 -> slot_1.get(slot_1.size()-1);
            case 2 -> slot_2.get(slot_2.size()-1);
            case 3 -> slot_3.get(slot_3.size()-1);
            default -> new Pair<>();
        };
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

    public ArrayList<Resources> getHand() {
        return hand;
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

    public int getLorenzoMarker() {
        return lorenzoMarker;
    }
}
