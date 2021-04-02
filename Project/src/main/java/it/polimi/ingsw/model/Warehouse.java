package it.polimi.ingsw.model;
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

    public void addDepot(int depotNumber, Resources resource, int quantity) throws IllegalArgumentException, ResourceErrorException{
        if(depotNumber==1){
            if(quantity>1 || quantity<0) throw new ResourceErrorException("Wrong placement");
            if(!(depot1.getKey().isPresent())){
                depot1.setPair(Optional.of(resource),quantity);
            }
            else throw new ResourceErrorException("Depot already full");
        }
        else if(depotNumber==2) {
            if (quantity>2 || quantity<0) throw new ResourceErrorException("Wrong placement");
            if(!(depot2.getKey().isPresent())){
                depot2.setPair(Optional.of(resource),quantity);
            }
            else if (depot2.getKey().get().equals(resource) && (depot2.getValue() + quantity <= 2)){
                depot2.setValue(depot2.getValue() + quantity);
            }
            else if(!(depot2.getKey().get().equals(resource))) throw new IllegalArgumentException("Wrong Resource");
            else throw new ResourceErrorException("Depot not big enough");
        }
        else if(depotNumber==3){
            if (quantity>3 || quantity<0) throw new ResourceErrorException("Wrong placement");
            if(!(depot3.getKey().isPresent())){
                depot3.setPair(Optional.of(resource),quantity);
            }
            else if (depot3.getKey().get().equals(resource) && (depot3.getValue() + quantity <= 3)){
                depot3.setValue(depot3.getValue() + quantity);
            }
            else if(!(depot3.getKey().get().equals(resource))) throw new IllegalArgumentException("Wrong Resource");
            else throw new ResourceErrorException("Depot not big enough");
        }
        else
            throw new IllegalArgumentException("Invalid depot number");
    }

    public Pair<Resources,Integer> subDepot(int depotNumber, int quantity) throws IllegalArgumentException, ResourceErrorException {
        Pair<Resources,Integer> tmp;
        if(depotNumber==1){
            if(quantity>1 || quantity<0) throw new ResourceErrorException("Wrong placement");
            if((depot1.getKey().isPresent())){
                    tmp = new Pair<>(depot1.getKey().get(),quantity);
                    depot1.setPair(Optional.empty(),0);
                    return tmp;
            }
            else throw new ResourceErrorException("Not enough resources");
        }
        else if(depotNumber==2){
            if(quantity>2 || quantity<0) throw new ResourceErrorException("Wrong placement");
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
            else throw new ResourceErrorException("Not enough resources");
        }
        else if(depotNumber==3){
            if(quantity>3 || quantity<0) throw new ResourceErrorException("Wrong placement");
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
            else throw new ResourceErrorException("Not enough resources");
        }
        else throw new IllegalArgumentException("Invalid depot number");
    }
}


