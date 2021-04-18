package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyDeckException;

import java.util.*;

public class Board {
    private final FaithTrack faithtrack;
    private Warehouse warehouse;
    private Strongbox strongbox;
    private final Slot[] slots;
    private final ArrayList<LeaderCard> leadercardshand;
    private final ArrayList<LeaderCard> leadercardsplayed;
    private final ArrayList<Resources> hand;
    private boolean actionDone;
    private int victoryPoints;

    public Board(ArrayList<LeaderCard> leadercards) {
        faithtrack = new FaithTrack();
        warehouse = new Warehouse();
        strongbox = new Strongbox();
        slots = new Slot[3];
        for(int i=0;i<3;i++){
            slots[i] = new Slot();
        }
        leadercardshand = new ArrayList<LeaderCard> (leadercards);
        leadercardsplayed = new ArrayList<LeaderCard>();
        hand = new ArrayList<>();
        actionDone = false;
        victoryPoints = 0;
    }

    public String slottoString(){
        String s = "";
        for(int i=0;i<3;i++){
            Slot slot = slots[i];
            try{
                slot.getFirstCard();
                s=s.concat("Slot numero "+(i+1)+":\n"+slot.getFirstCard().toString()+"\n");
            }catch(EmptyDeckException e) {
                s=s.concat("Lo slot numero "+(i+1)+" è vuoto\n");
            }
        }
        return s;
    }

    public String handtoString(){
        String s = "";
        for(Resources r : hand){
            s=s.concat(r.abbreviation() + " ");
        }
        return s;
    }

    /*public void clearWarehouse(){
        for(int i=1;i<4;i++) {
            hand.addAll(warehouse.clear(i));
        }
    }

    public void clearDepot(int i){
        hand.addAll(warehouse.clear(i));
    }*/

    public ArrayList<Resources> getHand() {
        return hand;
    }

    public ArrayList<LeaderCard> getLeadercardsplayed() {
        return leadercardsplayed;
    }

    public FaithTrack getFaithtrack() {
        return faithtrack;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public Strongbox getStrongbox() {
        return strongbox;
    }

    public Slot getSlot(int index) throws IllegalArgumentException{
        if(index < 1 || index > 3) throw new IllegalArgumentException("Invalid slot: index must be between 1 and 3");
        return slots[index-1];
    }

    public ArrayList<LeaderCard> getLeadercards() {
        return leadercardshand;
    }

    public boolean getActionDone() {
        return actionDone;
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

    public void discardLeaderCard(int i){
        leadercardshand.remove(i-1);
    }

    public void playLeaderCard(int i) throws IllegalArgumentException{
        LeaderCard LC = leadercardshand.get(i-1);
        Map<Colours,Pair<Integer,Integer>> requiredColours = new HashMap<>(LC.getRequiredColours());
        Map<Resources,Integer> requiredResources = new HashMap<>(LC.getRequiredResources());
        ArrayList<Pair<Colours,Integer>> tmp1 = new ArrayList<>();
        Map<Resources,Integer> tmp2 = new HashMap<>();

        for(int j=0;j<3;j++){
            tmp1.addAll(slots[j].getList());
        }
        //Checking level and number of requested card
        for(int j=0;j<tmp1.size();j++){
            if(requiredColours.get(tmp1.get(j).getKey())!=null){
                if(tmp1.get(j).getValue() >= requiredColours.get(tmp1.get(j).getKey()).getValue()){
                    if(requiredColours.get(tmp1.get(j).getKey()).getKey()-1<=0){
                        requiredColours.remove(tmp1.get(j).getKey());
                    }
                    else{
                        requiredColours.put(tmp1.get(j).getKey(),
                                new Pair<>(requiredColours.get(tmp1.get(j).getKey()).getKey()-1,
                                        requiredColours.get(tmp1.get(j).getKey()).getValue()));
                    }
                }
            }
        }

        //Checking resources and number of requested card
        tmp2.putAll(getAllResources());
        for(Resources r : requiredResources.keySet()){
            if(requiredResources.get(r) <= tmp2.get(r)) requiredResources.remove(r);
        }

        if(requiredColours.isEmpty() && requiredResources.isEmpty())
            leadercardsplayed.add(leadercardshand.remove(i-1));
        else
            throw new IllegalArgumentException("Requirements not met");
    }

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
        for(LeaderCard LC : leadercardsplayed){
            tmp.put(LC.getAbility().getRestype(), tmp.get(LC.getAbility().getRestype())+LC.getAbility().getStashedResources());
        }

        return tmp;
    }

    public void dumpHandIntoStrongbox(){
        for(Resources R: hand){
            strongbox.addResource(R,1);
        }
        hand.clear();
    }
}