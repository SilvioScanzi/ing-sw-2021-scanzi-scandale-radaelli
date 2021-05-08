package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyException;
import it.polimi.ingsw.exceptions.RequirementsNotMetException;
import it.polimi.ingsw.observers.ModelObservable;

import java.util.*;

public class Board extends ModelObservable {
    private final String nickname;
    private final FaithTrack faithtrack;
    private Warehouse warehouse;
    private Strongbox strongbox;
    private final Slot[] slots;
    private final ArrayList<LeaderCard> leaderCardsHand;
    private final ArrayList<LeaderCard> leaderCardsPlayed;
    private ArrayList<Resources> hand;
    private boolean actionDone;
    private int victoryPoints;

    public Board(ArrayList<LeaderCard> leadercards, String nickname) {
        this.nickname = nickname;
        faithtrack = new FaithTrack();
        warehouse = new Warehouse();
        strongbox = new Strongbox();
        slots = new Slot[3];
        for(int i=0;i<3;i++){
            slots[i] = new Slot();
        }
        leaderCardsHand = new ArrayList<> (leadercards);
        leaderCardsPlayed = new ArrayList<>();
        hand = new ArrayList<>();
        actionDone = false;
        victoryPoints = 0;
    }

    //toString Methods for debugging
    public String slottoString(){
        String s = "";
        for(int i=0;i<3;i++){
            Slot slot = slots[i];
            try{
                slot.getFirstCard();
                s=s.concat("Slot numero "+(i+1)+":\n"+slot.getFirstCard().toString()+"\n");
            }catch(EmptyException e) {
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

    //getters
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

    public ArrayList<LeaderCard> getLeaderCardsPlayed() {
        return leaderCardsPlayed;
    }

    public FaithTrack getFaithtrack() {
        return faithtrack;
    }

    public Slot getSlot(int index) throws IndexOutOfBoundsException{
        if(index < 1 || index > 3) throw new IndexOutOfBoundsException("Invalid slot: index must be between 1 and 3");
        return slots[index-1];
    }

    public ArrayList<LeaderCard> getLeadercards() {
        return leaderCardsHand;
    }

    public int getVictoryPoints() {
        return victoryPoints;
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
        for(LeaderCard LC : leaderCardsPlayed){
            tmp.put(LC.getAbility().getResType(), tmp.get(LC.getAbility().getResType())+LC.getAbility().getStashedResources());
        }

        return tmp;
    }

    //setters
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

    public void playLeaderCard(int i) throws RequirementsNotMetException,IndexOutOfBoundsException {
        LeaderCard LC = leaderCardsHand.get(i-1);
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
            leaderCardsPlayed.add(leaderCardsHand.remove(i-1));
        else
            throw new RequirementsNotMetException("Cannot play selected leader card");
    }
}