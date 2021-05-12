package it.polimi.ingsw.view;

import it.polimi.ingsw.commons.*;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientModel {
    private final String myNickname;
    private final HashMap<Integer,String> playerMap;
    private final HashMap<Pair<Colours,Integer>, Boolean> cardMarket;
    private final Boolean[][] whiteMarbles;
    private final HashMap<Integer, Pair<Resources,Integer>> warehouse;
    private final ArrayList<Pair<Colours, Integer>> slots;
    private HashMap<Resources,Integer> strongBox;
    private ArrayList<Triplet<Resources,Integer,Integer>> leaderCardsHand;
    private ArrayList<Triplet<Resources,Integer,Integer>> leaderCardsPlayed;
    private ArrayList<Resources> hand;
    private boolean actionDone;

    public ClientModel(HashMap<Integer,String> playerMap, String myNickname){
        this.myNickname = myNickname;
        this.playerMap = playerMap;
        cardMarket = new HashMap<>();
        whiteMarbles = new Boolean[3][4];
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
    }

    //setters

    public void setActionDone(boolean actionDone) {
        this.actionDone = actionDone;
    }

    public void setCardMarket(HashMap<Pair<Colours, Integer>, Integer> cardMarket) {
        for(int L=1;L<4;L++){
            for(Colours C: Colours.values()){
                Pair<Colours,Integer> P = new Pair<>(C,L);
                if(cardMarket.containsKey(P)) this.cardMarket.put(P,true);
                else this.cardMarket.put(P,false);
            }
        }
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

    public void generateWhiteMarbles(Marbles[][] grid){
        for(int i=0;i<3;i++){
            for(int j=0;j<4;j++){
                whiteMarbles[i][j] = grid[i][j].equals(Marbles.White);
            }
        }
    }

    //getters

    public HashMap<Pair<Colours, Integer>, Boolean> getCardMarket() {
        return cardMarket;
    }

    public int getWhiteMarbles(boolean row, int i) {
        int n = 0;
        if(row){
            for(int k=0;k<4;k++) n = n + ((whiteMarbles[i-1][k])? 1:0);
        }
        else{
            for(int k=0;k<3;k++) n = n + ((whiteMarbles[k][i-1])? 1:0);
        }
        return n;
    }

    public String getMyNickname() {
        return myNickname;
    }

    public HashMap<Integer, Pair<Resources, Integer>> getWarehouse() {
        return warehouse;
    }

    public Integer getSlots(int i) {
        return slots.get(i).getValue();
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
}
