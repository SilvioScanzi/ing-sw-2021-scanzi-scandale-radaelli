package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.Resources;
import it.polimi.ingsw.model.Triplet;
import it.polimi.ingsw.model.Warehouse;

import java.io.Serializable;
import java.util.ArrayList;

public class WarehouseMessage extends Message implements Serializable {
    ArrayList<Triplet<Integer,Resources,Integer>> warehouse = new ArrayList<>();
    String nickname;
    //triplet: number of depot, resource, quantity; only sending depots with at least one resource

    public WarehouseMessage(Warehouse wr,String s){
        for(int i=1;i<=3;i++){
            if(wr.getDepot(i).getKey().isPresent())
                warehouse.add(new Triplet<>(i,wr.getDepot(i).getKey().get(),wr.getDepot(i).getValue()));
        }
        nickname = s;
    }

    public ArrayList<Triplet<Integer, Resources, Integer>> getWarehouse() {
        return warehouse;
    }

    public String getNickname() {
        return nickname;
    }
}
