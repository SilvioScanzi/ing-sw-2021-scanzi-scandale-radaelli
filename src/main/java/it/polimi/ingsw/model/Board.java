package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Colours;
import it.polimi.ingsw.commons.Pair;
import it.polimi.ingsw.commons.Resources;
import it.polimi.ingsw.exceptions.RequirementsNotMetException;

import java.util.*;

public class Board{
    private final String nickname;
    private final FaithTrack faithtrack;
    private Warehouse warehouse;
    private Strongbox strongbox;
    private final Slot[] slots;
    private final HashMap<Integer, LeaderCard> leaderCardsHand;
    private final HashMap<Integer, LeaderCard> leaderCardsPlayed;
    private ArrayList<Resources> hand;
    private boolean actionDone;
    private boolean lastActionMarket;
    private boolean moveNeeded;
    private int victoryPoints;

    public Board(HashMap<Integer,LeaderCard> leaderCards, String nickname) {
        this.nickname = nickname;
        faithtrack = new FaithTrack();
        warehouse = new Warehouse();
        strongbox = new Strongbox();
        slots = new Slot[3];
        for(int i=0;i<3;i++){
            slots[i] = new Slot();
        }
        leaderCardsHand = new HashMap<> (leaderCards);
        leaderCardsPlayed = new HashMap<>();
        hand = new ArrayList<>();
        actionDone = false;
        victoryPoints = 0;
    }

    //getters
    public boolean getMoveNeeded() {
        return moveNeeded;
    }

    public boolean getLastActionMarket() {
        return lastActionMarket;
    }

    public String getNickname() {
        return nickname;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public Strongbox getStrongbox() {
        return strongbox;
    }

    public boolean getActionDone() {
        return actionDone;
    }

    public ArrayList<Resources> getHand() {
        return hand;
    }

    public HashMap<Integer, LeaderCard> getLeaderCardsPlayed() {
        return leaderCardsPlayed;
    }

    public FaithTrack getFaithTrack() {
        return faithtrack;
    }

    public Slot[] getSlots() {
        return slots;
    }

    public Slot getSlot(int index) throws IndexOutOfBoundsException{
        if(index < 1 || index > 3) throw new IndexOutOfBoundsException("Invalid slot: index must be between 1 and 3");
        return slots[index-1];
    }

    public HashMap<Integer,LeaderCard> getLeaderCardsHand() {
        return leaderCardsHand;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * Method used to count every resource on the board, including warehouse, strongbox and leader cards
     * @return Hashmap with Resources and relative quantities in the board
     */
    public HashMap<Resources,Integer> getAllResources(){
        HashMap<Resources,Integer> tmp = new HashMap<>();

        //adds strongbox resources
        for(Resources r : Resources.values()){
            tmp.put(r,strongbox.getResource(r));
        }

        //adds warehouse resources
        for(int i=1;i<=3;i++){
            Pair<Optional<Resources>,Integer> whtmp = new Pair<>(warehouse.getDepot(i));
            if(whtmp.getKey().isPresent()) tmp.put(whtmp.getKey().get(), tmp.get(whtmp.getKey().get())+whtmp.getValue());
        }

        //adds leader cards resources
        for(Integer I : leaderCardsPlayed.keySet()){
            LeaderCard LC = leaderCardsPlayed.get(I);
            tmp.put(LC.getAbility().getResType(), tmp.get(LC.getAbility().getResType())+LC.getAbility().getStashedResources());
        }

        return tmp;
    }

    //setters

    public void setMoveNeeded(boolean moveNeeded) {
        this.moveNeeded = moveNeeded;
    }

    public void setLastActionMarket(boolean lastActionMarket) {
        this.lastActionMarket = lastActionMarket;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public void setStrongbox(Strongbox strongbox) {
        this.strongbox = strongbox;
    }

    public void setActionDone(boolean actionDone) {
        this.actionDone = actionDone;
    }

    public void setVictoryPoints(int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    public void setHand(ArrayList<Resources> hand) {
        this.hand = hand;
    }



    //action related methods
    public void dumpHandIntoStrongbox(){
        for(Resources R: hand){
            strongbox.addResource(R,1);
        }
        hand.clear();
    }

    public void discardLeaderCard(int i){
        leaderCardsHand.remove(i-1);
    }

    /**
     * Method used to play a leader card
     * @param i index of the leader card to play
     * @throws RequirementsNotMetException if the player doesn't meet all the criteria to play that card
     * @throws IndexOutOfBoundsException if the index given doesn't match a valid index on the board
     */
    public void playLeaderCard(int i) throws RequirementsNotMetException,IndexOutOfBoundsException {
        LeaderCard LC = leaderCardsHand.get(i-1);
        Map<Colours,Pair<Integer,Integer>> requiredColours = new HashMap<>(LC.getRequiredColours());
        Map<Resources,Integer> requiredResources = new HashMap<>(LC.getRequiredResources());
        ArrayList<Pair<Colours,Integer>> tmp1 = new ArrayList<>();

        for(int j=0;j<3;j++){
            tmp1.addAll(slots[j].getList());
        }
        //Checking level and number of requested card
        for (Pair<Colours, Integer> coloursIntegerPair : tmp1) {
            if (requiredColours.get(coloursIntegerPair.getKey()) != null) {
                if (coloursIntegerPair.getValue() >= requiredColours.get(coloursIntegerPair.getKey()).getValue()) {
                    if (requiredColours.get(coloursIntegerPair.getKey()).getKey() - 1 <= 0) {
                        requiredColours.remove(coloursIntegerPair.getKey());
                    } else {
                        requiredColours.put(coloursIntegerPair.getKey(),
                                new Pair<>(requiredColours.get(coloursIntegerPair.getKey()).getKey() - 1,
                                        requiredColours.get(coloursIntegerPair.getKey()).getValue()));
                    }
                }
            }
        }

        //Checking resources and number of requested card
        Map<Resources, Integer> tmp2 = new HashMap<>(getAllResources());
        for(Resources r : requiredResources.keySet()){
            if(requiredResources.get(r) <= tmp2.get(r)) requiredResources.remove(r);
        }

        if(requiredColours.isEmpty() && requiredResources.isEmpty())
            leaderCardsPlayed.put(i-1,leaderCardsHand.remove(i-1));
        else
            throw new RequirementsNotMetException("Cannot play selected leader card");
    }
}