package it.polimi.ingsw.model;
import it.polimi.ingsw.Game;

public class Ability {
    public enum AbilityType {DiscountAbility,ExtraSlotAbility,ProductionPowerAbility,WhiteMarbleAbility}

    private AbilityType type;
    private Game.Resources restype;
    private int capacity;           //only for ExtraSlot
    private boolean activated;      //only for ProductionPower

    public Ability(AbilityType type,Game.Resources restype) {
        this.type = type;
        this.restype = restype;
        if(this.type.equals(AbilityType.ExtraSlotAbility)){
            capacity = 0;
        }
        else{
            capacity = -1;
        }
        activated = false;
    }

    public AbilityType getType() {
        return type;
    }

    public Game.Resources getRestype() {
        return restype;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isActivated() {
        return activated;
    }

    public Pair<Game.Resources,Integer> updateCapacity(Game.Resources resource, int amount) throws IllegalArgumentException, ResourceErrorException {
        if(!(resource.equals(restype))) throw new IllegalArgumentException("Not compatible resource");
        if(amount + capacity > 2){
            throw new ResourceErrorException("Too many resources");
        }
        if(amount + capacity < 0){
            throw new ResourceErrorException("Too little resources");
        }
        capacity = capacity + amount;
        if(amount<0)  return new Pair<>(resource, -amount);
        else return new Pair<>(resource,0);
    }

    public void activate(Game.Resources resUsed) throws IllegalArgumentException{
        if(!(type.equals(AbilityType.ProductionPowerAbility))) throw new IllegalArgumentException("Not activatable");
        if(!(resUsed.equals(restype))) throw new IllegalArgumentException("Wrong resource");
        activated = true;
    }

    public void deActivate(){
        if(!(type.equals(AbilityType.ProductionPowerAbility))) throw new IllegalArgumentException("Not activatable");
        activated = false;
    }
}
