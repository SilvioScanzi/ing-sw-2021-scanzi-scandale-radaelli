package it.polimi.ingsw.model;
import it.polimi.ingsw.exceptions.IncompatibleResourceException;
import it.polimi.ingsw.exceptions.InvalidPlacementException;
import it.polimi.ingsw.exceptions.ResourceErrorException;

import java.util.*;

public class Warehouse implements Cloneable {
    private  Pair<Optional<Resources>,Integer> depot1;
    private  Pair<Optional<Resources>,Integer> depot2;
    private  Pair<Optional<Resources>,Integer> depot3;

    public Warehouse(){
        depot1 = new Pair<>(Optional.empty(),0);
        depot2 = new Pair<>(Optional.empty(),0);
        depot3 = new Pair<>(Optional.empty(),0);
    }

    @Override
    public Warehouse clone() {
        Warehouse tmp = new Warehouse();
        tmp.depot1 = new Pair<>(this.depot1.getKey(),this.depot1.getValue());
        tmp.depot2 = new Pair<>(this.depot2.getKey(),this.depot2.getValue());
        tmp.depot3 = new Pair<>(this.depot3.getKey(),this.depot3.getValue());
        return tmp;
    }

    @Override
    public String toString(){
        String tmp = "Magazzino:\nDeposito 1: ";
        if(depot1.getKey().isPresent()){
            tmp=tmp.concat(depot1.getKey().get().abbreviation());
        }
        tmp=tmp.concat("\nDeposito 2: ");
        if(depot2.getKey().isPresent()){
            for(int i=0;i<depot2.getValue();i++){
                tmp=tmp.concat(depot2.getKey().get().abbreviation()+" ");
            }
        }
        tmp=tmp.concat("\nDeposito 3: ");
        if(depot3.getKey().isPresent()){
            for(int i=0;i<depot3.getValue();i++){
                tmp=tmp.concat(depot3.getKey().get().abbreviation()+" ");
            }
        }
        return tmp;
    }

    public Pair<Optional<Resources>,Integer> getDepot(int depotNumber) throws IllegalArgumentException{
        if(depotNumber==1)
            return depot1;
        else if(depotNumber==2)
            return depot2;
        else if(depotNumber==3)
            return depot3;
        else
            throw new IllegalArgumentException("Invalid depot number");
    }

    public void addDepot(int depotNumber, Resources resource, int quantity) throws IndexOutOfBoundsException,ResourceErrorException,IncompatibleResourceException,InvalidPlacementException{
        if(depotNumber==1){
            if(quantity != 1) throw new ResourceErrorException("Depot 1 can't contain these resources");
            if(depot2.getKey().equals(Optional.of(resource))) throw new InvalidPlacementException("Depot 2 has already got this resource");
            if(depot3.getKey().equals(Optional.of(resource))) throw new InvalidPlacementException("Depot 3 has already got this resource");
            if(!(depot1.getKey().isPresent())) depot1.setPair(Optional.of(resource),quantity);
            else throw new ResourceErrorException("Depot 1 is already full");
        }
        else if(depotNumber==2) {
            if (quantity>2 || quantity<=0) throw new ResourceErrorException("Depot 2 can't contain these resources");
            if(depot1.getKey().equals(Optional.of(resource))) throw new InvalidPlacementException("Depot 1 has already got this resource");
            if(depot3.getKey().equals(Optional.of(resource))) throw new InvalidPlacementException("Depot 3 has already got this resource");
            if(!(depot2.getKey().isPresent())){
                depot2.setPair(Optional.of(resource),quantity);
            }
            else if (depot2.getKey().get().equals(resource) && (depot2.getValue() + quantity <= 2)){
                depot2.setValue(depot2.getValue() + quantity);
            }
            else if(!(depot2.getKey().get().equals(resource))) throw new IncompatibleResourceException("Incompatible resources");
            else throw new ResourceErrorException("Depot 2 is already full");
        }
        else if(depotNumber==3){
            if (quantity>3 || quantity<=0) throw new ResourceErrorException("Depot 3 can't contain these resources");
            if(depot1.getKey().equals(Optional.of(resource))) throw new InvalidPlacementException("Depot 1 has already got this resource");
            if(depot2.getKey().equals(Optional.of(resource))) throw new InvalidPlacementException("Depot 2 has already got this resource");
            if(!(depot3.getKey().isPresent())){
                depot3.setPair(Optional.of(resource),quantity);
            }
            else if (depot3.getKey().get().equals(resource) && (depot3.getValue() + quantity <= 3)){
                depot3.setValue(depot3.getValue() + quantity);
            }
            else if(!(depot3.getKey().get().equals(resource))) throw new IncompatibleResourceException("Incompatible resources");
            else throw new ResourceErrorException("Depot 3 is already full");
        }
        else
            throw new IndexOutOfBoundsException("Invalid depot number");
    }

    public void subDepot(int depotNumber, int quantity) throws IndexOutOfBoundsException, ResourceErrorException {
        if(depotNumber==1){
            if(quantity>1 || quantity<0) throw new ResourceErrorException("Depot 1 doesn't have this many resources");
            if((depot1.getKey().isPresent())){
                depot1.setPair(Optional.empty(),0);
            }
            else throw new ResourceErrorException("Depot 1 is empty");
        }
        else if(depotNumber==2){
            if(quantity>2 || quantity<0) throw new ResourceErrorException("Depot 2 doesn't have this many resources");
            if((depot2.getKey().isPresent()) && (depot2.getValue() >= quantity)){
                if(depot2.getValue() == quantity) {
                    depot2.setPair(Optional.empty(), 0);
                }
                else{
                    depot2.setValue(depot2.getValue() - quantity);
                }
            }
            else throw new ResourceErrorException("Depot 2 doesn't have this many resources");
        }
        else if(depotNumber==3){
            if(quantity>3 || quantity<0) throw new ResourceErrorException("Depot 3 doesn't have this many resources");
            if((depot3.getKey().isPresent()) && (depot3.getValue() >= quantity)){
                if(depot3.getValue() == quantity) {
                    depot3.setPair(Optional.empty(), 0);
                }
                else{
                    depot3.setValue(depot3.getValue() - quantity);
                }
            }
            else throw new ResourceErrorException("Depot 3 doesn't have this many resources");
        }
        else throw new IndexOutOfBoundsException("Invalid depot number");
    }

    public boolean checkResourcePresent(int depot, Resources resource) throws IndexOutOfBoundsException{
        if(depot<1 || depot>3) throw new IndexOutOfBoundsException();
        return getDepot(depot).getKey().isPresent() && getDepot(depot).getKey().get().equals(resource);
    }
}
