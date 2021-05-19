package it.polimi.ingsw.model;
import it.polimi.ingsw.commons.Colours;
import it.polimi.ingsw.commons.Resources;

import java.util.*;

public class DevelopmentCard {
    private final int level;                                        //level between 1 and 3
    private final Colours colour;
    private final int victoryPoints;
    private final HashMap<Resources,Integer> cost;                  //Resources required to buy the card
    private final HashMap<Resources,Integer> requiredResources;     //Resources required to activate production power
    private final HashMap<Resources,Integer> producedResources;     //Resources produced during the activation
    private final int producedFaith;                                //Faith produced during the activation

    public DevelopmentCard(int level, Colours colour, int victoryPoints, HashMap<Resources, Integer> cost, HashMap<Resources, Integer> requiredResources, HashMap<Resources, Integer> producedResources, int producedFaith) {
        this.level = level;
        this.colour = colour;
        this.victoryPoints = victoryPoints;
        this.cost = cost;
        this.requiredResources = requiredResources;
        this.producedResources = producedResources;
        this.producedFaith = producedFaith;
    }

    public int getLevel() {
        return level;
    }

    public Colours getColour() {
        return colour;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public HashMap<Resources, Integer> getCost() {
        return cost;
    }

    public HashMap<Resources, Integer> getRequiredResources() {
        return requiredResources;
    }

    public HashMap<Resources, Integer> getProducedResources() {
        return producedResources;
    }

    public int getProducedFaith() {
        return producedFaith;
    }
}
