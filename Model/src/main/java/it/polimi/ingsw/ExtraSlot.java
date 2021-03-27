package it.polimi.ingsw;

public class ExtraSlot implements Ability{
    private Game.Resources restype;
    private int capacity = 0;



    public Game.Resources getResource() {
        return restype;
    }

    public int getCapacity() {
        return capacity;
    }

    public void updateCapacity(Game.Resources resource, int quantity) throws IllegalArgumentException, ResourceErrorException{
        if(!(resource.equals(restype))) throw new IllegalArgumentException("Not compatible");
        else{
            if(quantity + capacity < 0 || quantity + capacity > 2) throw new ResourceErrorException("Resource error");
            else {
                capacity = capacity + quantity;
            }
        }
    }
}
