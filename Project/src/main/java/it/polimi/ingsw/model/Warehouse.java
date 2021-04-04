package it.polimi.ingsw.model;
import it.polimi.ingsw.exceptions.IncompatibleResourceException;
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

    public void addDepot(int depotNumber, Resources resource, int quantity) throws IllegalArgumentException, ResourceErrorException, IncompatibleResourceException {
        if(depotNumber==1){
            if(quantity>1 || quantity<0) throw new ResourceErrorException("Depot 1 can't contain these resources",resource,quantity);
            if(depot2.getKey().equals(Optional.of(resource))) throw new IllegalArgumentException("Depot 2 has already got this resource");
            if(depot3.getKey().equals(Optional.of(resource))) throw new IllegalArgumentException("Depot 3 has already got this resource");
            if(!(depot1.getKey().isPresent())){
                depot1.setPair(Optional.of(resource),quantity);
            }
            else throw new ResourceErrorException("Depot 1 is already full",resource,quantity);
        }
        else if(depotNumber==2) {
            if (quantity>2 || quantity<0) throw new ResourceErrorException("Depot 2 can't contain these resources",resource,quantity);
            if(depot1.getKey().equals(Optional.of(resource))) throw new IllegalArgumentException("Depot 1 has already got this resource");
            if(depot3.getKey().equals(Optional.of(resource))) throw new IllegalArgumentException("Depot 3 has already got this resource");
            if(!(depot2.getKey().isPresent())){
                depot2.setPair(Optional.of(resource),quantity);
            }
            else if (depot2.getKey().get().equals(resource) && (depot2.getValue() + quantity <= 2)){
                depot2.setValue(depot2.getValue() + quantity);
            }
            else if(!(depot2.getKey().get().equals(resource))) throw new IncompatibleResourceException("Incompatible resources",resource,depot2.getKey().get());
            else throw new ResourceErrorException("Depot 2 is already full",resource,quantity-(2-depot2.getValue()));
        }
        else if(depotNumber==3){
            if (quantity>3 || quantity<0) throw new ResourceErrorException("Depot 3 can't contain these resources",resource,quantity);
            if(depot1.getKey().equals(Optional.of(resource))) throw new IllegalArgumentException("Depot 1 has already got this resource");
            if(depot2.getKey().equals(Optional.of(resource))) throw new IllegalArgumentException("Depot 2 has already got this resource");
            if(!(depot3.getKey().isPresent())){
                depot3.setPair(Optional.of(resource),quantity);
            }
            else if (depot3.getKey().get().equals(resource) && (depot3.getValue() + quantity <= 3)){
                depot3.setValue(depot3.getValue() + quantity);
            }
            else if(!(depot3.getKey().get().equals(resource))) throw new IncompatibleResourceException("Incompatible resources",resource,depot3.getKey().get());
            else throw new ResourceErrorException("Depot 3 is already full",resource,quantity-(3-depot3.getValue()));
        }
        else
            throw new IllegalArgumentException("Invalid depot number");
    }

    public Pair<Resources,Integer> subDepot(int depotNumber, int quantity) throws IllegalArgumentException, ResourceErrorException {
        Pair<Resources,Integer> tmp;
        if(depotNumber==1){
            if(quantity>1 || quantity<0) throw new ResourceErrorException("Depot 1 doesn't have this many resources",(depot1.getKey().isPresent())?depot1.getKey().get():null,-quantity);
            if((depot1.getKey().isPresent())){
                    tmp = new Pair<>(depot1.getKey().get(),quantity);
                    depot1.setPair(Optional.empty(),0);
                    return tmp;
            }
            else throw new ResourceErrorException("Depot 1 is empty",null,-quantity);
        }
        else if(depotNumber==2){
            if(quantity>2 || quantity<0) throw new ResourceErrorException("Depot 2 doesn't have this many resources",(depot2.getKey().isPresent())?depot2.getKey().get():null,-quantity);
            if((depot2.getKey().isPresent()) && (depot2.getValue() >= quantity)){
                tmp = new Pair<>(depot2.getKey().get(),quantity);
                if(depot2.getValue() == quantity) {
                    depot2.setPair(Optional.empty(), 0);
                }
                else{
                    depot2.setValue(depot2.getValue() - quantity);
                }
                return tmp;
            }
            else throw new ResourceErrorException("Depot 2 doesn't have this many resources",(depot2.getKey().isPresent())?depot2.getKey().get():null,-quantity);
        }
        else if(depotNumber==3){
            if(quantity>3 || quantity<0) throw new ResourceErrorException("Depot 3 doesn't have this many resources",(depot3.getKey().isPresent())?depot3.getKey().get():null,-quantity);
            if((depot3.getKey().isPresent()) && (depot3.getValue() >= quantity)){
                tmp = new Pair<>(depot3.getKey().get(),quantity);
                if(depot3.getValue() == quantity) {
                    depot3.setPair(Optional.empty(), 0);
                }
                else{
                    depot3.setValue(depot3.getValue() - quantity);
                }
                return tmp;
            }
            else throw new ResourceErrorException("Depot 3 doesn't have this many resources",(depot3.getKey().isPresent())?depot3.getKey().get():null,-quantity);
        }
        else throw new IllegalArgumentException("Invalid depot number");
    }

    public ArrayList<Resources> clear(){
        ArrayList<Resources> tmp = new ArrayList<>();
        if(depot1.getKey().isPresent()){
            tmp.add(depot1.getKey().get());
            depot1.setPair(Optional.empty(), 0);
        }
        if(depot2.getKey().isPresent()){
            for(int i=0;i<depot2.getValue();i++){
                tmp.add(depot2.getKey().get());
            }
            depot2.setPair(Optional.empty(), 0);
        }
        if(depot3.getKey().isPresent()){
            for(int i=0;i<depot3.getValue();i++){
                tmp.add(depot3.getKey().get());
            }
            depot3.setPair(Optional.empty(), 0);
        }
        return tmp;
    }
}


