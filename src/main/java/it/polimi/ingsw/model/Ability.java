package it.polimi.ingsw.model;
import it.polimi.ingsw.exceptions.ResourceErrorException;

public class Ability {
    public enum AbilityType {DiscountAbility,ExtraSlotAbility,ProductionPowerAbility,WhiteMarbleAbility}

    private AbilityType type;
    private Resources restype;
    private int capacity;           //only for ExtraSlot and DiscountAbility (gives the discount amount)
    private boolean activated;      //only for ProductionPower


    public Ability(AbilityType type,Resources restype, int capacity) {
        this.type = type;
        this.restype = restype;
        this.capacity = capacity;
        activated = false;
    }

    public AbilityType getType() {
        return type;
    }

    public Resources getRestype() {
        return restype;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isActivated() {
        return activated;
    }

    public Pair<Resources,Integer> updateCapacity(Resources resource, int amount) throws IllegalArgumentException, ResourceErrorException {
        if(!(resource.equals(restype))) throw new IllegalArgumentException("Not compatible resource");
        if(amount + capacity > 2){
            throw new ResourceErrorException("Too many resources",resource,amount+capacity-2);
        }
        if(amount + capacity < 0){
            throw new ResourceErrorException("Too little resources",resource,amount+capacity);
        }
        capacity = capacity + amount;
        if(amount<0)  return new Pair<>(resource, -amount);
        else return new Pair<>(resource,0);
    }

    public void activate(Resources resUsed) throws IllegalArgumentException{
        if(!(type.equals(AbilityType.ProductionPowerAbility))) throw new IllegalArgumentException("Not activable");
        if(!(resUsed.equals(restype))) throw new IllegalArgumentException("Wrong resource");
        activated = true;
    }

    public void deActivate(){
        if(!(type.equals(AbilityType.ProductionPowerAbility))) throw new IllegalArgumentException("Not activable");
        activated = false;
    }
}
