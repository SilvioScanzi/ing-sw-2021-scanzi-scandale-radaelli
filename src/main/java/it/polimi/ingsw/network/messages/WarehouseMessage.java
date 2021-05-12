package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.commons.Resources;
import it.polimi.ingsw.commons.Triplet;
import it.polimi.ingsw.model.Warehouse;

import java.io.Serializable;
import java.util.ArrayList;

public class WarehouseMessage extends Message implements Serializable {
    public final ArrayList<Triplet<Integer,Resources,Integer>> warehouse = new ArrayList<>();
    public final String nickname;
    //triplet: number of depot, resource, quantity; only sending depots with at least one resource

    public WarehouseMessage(Warehouse wr,String nickname){
        for(int i=1;i<=3;i++){
            if(wr.getDepot(i).getKey().isPresent())
                warehouse.add(new Triplet<>(i,wr.getDepot(i).getKey().get(),wr.getDepot(i).getValue()));
        }
        this.nickname = nickname;
    }

    public ArrayList<Triplet<Integer, Resources, Integer>> getWarehouse() {
        return warehouse;
    }

    public String getNickname() {
        return nickname;
    }
}
