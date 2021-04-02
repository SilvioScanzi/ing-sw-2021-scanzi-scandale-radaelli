package it.polimi.ingsw.model;

import java.util.*;

public class DevelopmentCard {
    private final int level;
    private final Colours colour;
    private final int victoryPoints;
    private final HashMap<Resources,Integer> cost;               //Resources required to buy the card
    private final HashMap<Resources,Integer> requiredResources;  //Resources required to activate production power
    private final HashMap<Resources,Integer> producedResources;  //Resources produced during the activation
    private final int producedFaith;                              //Faith produced during the activation
    private boolean activated;

    public DevelopmentCard(int level, Colours colour, int victoryPoints, HashMap<Resources, Integer> cost,
                           HashMap<Resources, Integer> requiredResources, HashMap<Resources, Integer> producedResources, int producedFaith) {
        this.level = level;
        this.colour = colour;
        this.victoryPoints = victoryPoints;
        this.cost = cost;
        this.requiredResources = requiredResources;
        this.producedResources = producedResources;
        this.producedFaith = producedFaith;
        activated = false;
    }

    public int getLevel() {
        return level;
    }

    public Colours getColour() {
        return colour;
    }

    public int getvictoryPoints() {
        return victoryPoints;
    }

    public HashMap<Resources, Integer> getCost() {
        return cost;
    }

    public HashMap<Resources, Integer> getrequiredResources() {
        return requiredResources;
    }

    public HashMap<Resources, Integer> getproducedResources() {
        return producedResources;
    }

    public int getproducedFaith() {
        return producedFaith;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
