package it.polimi.ingsw.model;

import it.polimi.ingsw.Game;

import java.util.*;

public class DevelopmentCard {
    private final int level;
    private final Game.Colours colour;
    private final int victoryPoints;
    private final HashMap<Game.Resources,Integer> cost;               //Resources required to buy the card
    private final HashMap<Game.Resources,Integer> requiredResources;  //Resources required to activate production power
    private final HashMap<Game.Resources,Integer> producedResources;  //Resources produced during the activation
    private final int producedFaith;                              //Faith produced during the activation
    private boolean activated;

    public DevelopmentCard(int level, Game.Colours colour, int victoryPoints, HashMap<Game.Resources, Integer> cost,
                           HashMap<Game.Resources, Integer> requiredResources, HashMap<Game.Resources, Integer> producedResources, int producedFaith) {
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

    public Game.Colours getColour() {
        return colour;
    }

    public int getvictoryPoints() {
        return victoryPoints;
    }

    public HashMap<Game.Resources, Integer> getCost() {
        return cost;
    }

    public HashMap<Game.Resources, Integer> getrequiredResources() {
        return requiredResources;
    }

    public HashMap<Game.Resources, Integer> getproducedResources() {
        return producedResources;
    }

    public int getproducedFaith() {
        return producedFaith;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
