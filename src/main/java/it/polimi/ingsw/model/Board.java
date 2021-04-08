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
    private boolean flagIncompatibleResources;
    private boolean flagResourceError;
    private boolean baseProductionActivated;

    public Board(String nickname, ArrayList<LeaderCard> leadercards) {
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
        flagIncompatibleResources = false;
        flagResourceError = false;
        baseProductionActivated = false;
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

    public void setFlagIncompatibleResources(boolean flagIncompatibleResources) {
        this.flagIncompatibleResources = flagIncompatibleResources;
    }

    public void setFlagResourceError(boolean flagResourceError) {
        this.flagResourceError = flagResourceError;
    }

    public void clearWarehouse(){
        for(int i=1;i<4;i++) {
            hand.addAll(warehouse.clear(i));
        }
    }

    public void clearDepot(int i){
        hand.addAll(warehouse.clear(i));
    }

    public ArrayList<Resources> getHand() {
        return hand;
    }

    public ArrayList<LeaderCard> getLeadercardsplayed() {
        return leadercardsplayed;
    }

    public void setHand(ArrayList<Resources> resources) {
        hand.addAll(resources);
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

    public Slot[] getSlots() {
        return slots;
    }

    public Slot getSlot(int index) throws IllegalArgumentException{
        if(index < 1 || index > 3) throw new IllegalArgumentException("Invalid slot: index must be between 1 and 3");
        return slots[index-1];
    }

    public ArrayList<LeaderCard> getLeadercards() {
        return leadercardshand;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public void setStrongbox(Strongbox strongbox) {
        this.strongbox = strongbox;
    }

    public void discardLeaderCard(int i){
        leadercardshand.remove(i-1);
    }

    public void playLeaderCard(int i) throws IllegalArgumentException{
        LeaderCard LC = leadercardshand.get(i-1);
        Map<Colours,Pair<Integer,Integer>> requiredColours = new HashMap<>(LC.getRequiredColours());
        Map<Resources,Integer> requiredResources = new HashMap<>(LC.getRequiredResources());
        ArrayList<Pair<Colours,Integer>> tmp = new ArrayList<>();
        for(int j=0;j<3;j++){
            tmp.addAll(slots[j].getList());
        }
        //Checking level and number of requested card
        for(int j=0;j<tmp.size();j++){
            if(requiredColours.get(tmp.get(j).getKey())!=null){
                if(tmp.get(j).getValue() >= requiredColours.get(tmp.get(j).getKey()).getValue()){
                    if(requiredColours.get(tmp.get(j).getKey()).getKey()-1<=0){
                        requiredColours.remove(tmp.get(j).getKey());
                    }
                    else{
                        requiredColours.put(tmp.get(j).getKey(), new Pair<>(requiredColours.get(tmp.get(j).getKey()).getKey()-1,requiredColours.get(tmp.get(j).getKey()).getValue()));
                    }
                }
            }
        }
        //MANCA: CONTROLLO SULLE RISORSE
        if(requiredColours.isEmpty() && requiredResources.isEmpty())
            leadercardsplayed.add(leadercardshand.remove(i-1));
        else
            throw new IllegalArgumentException("Requirements not met");
    }

    public void dumpHandIntoStrongbox(){
        for(Resources R: hand){
            strongbox.addResource(R,1);
        }
        hand.clear();
    }

    public void production(ArrayList<Resources> usedResources, ArrayList<Resources> gotResource) throws IllegalArgumentException{
        int count = 0;
        Warehouse wr = warehouse.clone();
        Strongbox sb = strongbox.clone();

        for(Resources res : usedResources){
            if(wr.checkResourcePresent(1,res)) {
                try{
                    wr.subDepot(1,1);
                    count++;
                }catch(Exception e){e.printStackTrace();}
            }
            else if(wr.checkResourcePresent(2,res)) {
                try{
                    wr.subDepot(2,1);
                    count++;
                }catch(Exception e){e.printStackTrace();}
            }
            else if(wr.checkResourcePresent(3,res)) {
                try{
                    wr.subDepot(3,1);
                    count++;
                }catch(Exception e){e.printStackTrace();}
            }
        }

        for(Resources res : usedResources){
            if(sb.getResource(res) > 0 ){
                    sb.subResource(res,1);
                    count++;
            }
        }

        if(count==usedResources.size()){
            hand.addAll(gotResource);
            setWarehouse(wr);
            setStrongbox(sb);
        }
        else throw new IllegalArgumentException("Not enough resources");
    }

    public int slotProduction(int slotNumber) throws EmptyDeckException{
        DevelopmentCard DC;
        try{
           DC = slots[slotNumber-1].getFirstCard();
        }catch(EmptyDeckException e){throw e;}

        ArrayList<Resources> reqRes = new ArrayList<>();
        ArrayList<Resources> prodRes = new ArrayList<>();

        for(Resources r : DC.getrequiredResources().keySet()){
            for(int i=0; i<DC.getrequiredResources().get(r); i++) {
                reqRes.add(r);}
        }

        for(Resources r : DC.getproducedResources().keySet()){
            for(int i=0; i<DC.getproducedResources().get(r); i++) {
                prodRes.add(r);}
        }

        try{
            production(reqRes,prodRes);
        }catch (Exception e) { throw e;}

        return DC.getproducedFaith();
    }

    public void leaderProduction(int leadercardnumber, Resources R) throws IllegalArgumentException{
        if(leadercardsplayed.get(leadercardnumber-1)==null) throw new IllegalArgumentException("Wrong LC number");
        if(!leadercardsplayed.get(leadercardnumber-1).getAbility().getType().equals(Ability.AbilityType.ProductionPowerAbility)) throw new IllegalArgumentException("Wrong LC");
        ArrayList<Resources> usedResources = new ArrayList<>();
        usedResources.add(leadercardsplayed.get(leadercardnumber-1).getAbility().getRestype());
        ArrayList<Resources> gotResources = new ArrayList<>();
        gotResources.add(R);
        try{
            production(usedResources,gotResources);
        }catch (Exception e) { throw e;}
    }
}