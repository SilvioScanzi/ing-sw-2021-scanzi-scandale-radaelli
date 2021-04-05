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
               try{
                   sb.subResource(res,1);
                   count++;
               }catch(Exception e){e.printStackTrace();}
           }
        }

        if(count==usedResources.size()){
            hand.addAll(gotResource);
            setWarehouse(wr);
            setStrongbox(sb);
        }
        else throw new IllegalArgumentException("Not enough resources");
    }

    public int slotProduction(int slotNumber) throws IllegalArgumentException{
        if(!slots[slotNumber-1].getFirstCard().isPresent()) throw new IllegalArgumentException("Wrong slot");
        DevelopmentCard DC = slots[slotNumber-1].getFirstCard().get();

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
}
