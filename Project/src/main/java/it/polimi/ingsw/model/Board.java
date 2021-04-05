package it.polimi.ingsw.model;

import java.util.*;

public class Board {
    private final FaithTrack faithtrack;
    private Warehouse warehouse;
    private Strongbox strongbox;
    private final Slot[] slots;
    private final ArrayList<LeaderCard> leadercardshand;
    private final ArrayList<LeaderCard> leadercardsplayed;
    private ArrayList<Resources> hand;
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

    public void baseProduction(ArrayList<Resources> usedResources, Resources gotResource) throws IllegalArgumentException{
        ArrayList<Pair<Integer,Resources>> depotResource = new ArrayList<>();   //1,2,3 for depots; 4 for strongbox

        for(Resources res : usedResources){
            if(warehouse.checkResourcePresent(1,res)) {
                depotResource.add(new Pair<>(1,res));
                usedResources.remove(res);
            }
            else if(warehouse.checkResourcePresent(2,res)) {
                depotResource.add(new Pair<>(2,res));
                usedResources.remove(res);
            }
            else if(warehouse.checkResourcePresent(3,res)) {
                depotResource.add(new Pair<>(3,res));
                usedResources.remove(res);
            }
        }

        for(Resources res : usedResources){
           if(strongbox.getResource(res) > 0 ){
               depotResource.add(new Pair<>(4,res));
               usedResources.remove(res);
           }
        }

        if(depotResource.size()==2){
            try{
                for(int i=0; i<2; i++){
                    if(0<=depotResource.get(i).getKey() && depotResource.get(i).getKey() <=3)
                        warehouse.subDepot(depotResource.get(i).getKey(),1);
                    else strongbox.subResource(depotResource.get(i).getValue(),1);
                    hand.add(depotResource.get(i).getValue());
                }
            }catch(Exception e){ e.printStackTrace(); }
        }
        else throw new IllegalArgumentException("Not enough resources");
    }
}
