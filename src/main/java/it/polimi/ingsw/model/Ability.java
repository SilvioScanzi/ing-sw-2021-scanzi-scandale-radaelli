package it.polimi.ingsw.model;

import java.util.HashMap;

public interface Ability {
    public HashMap<Resources, Integer> doDiscount(HashMap<Resources, Integer> cost);
    public boolean doActivate();
    public boolean doConvert();
    public boolean doUpdateSlot(Resources resource, int amount);
    public Resources getRestype();
    public int getStashedResources();
    public int getCapacity();
    public String toString();
}
