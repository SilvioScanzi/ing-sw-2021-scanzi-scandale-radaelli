package it.polimi.ingsw;
import java.util.*;

public class DevelopmentCard {
    private int level;
    private Game.Colours colour;
    private int victoryPoints;
    private Map<Game.Resources,Integer> cost;               //Resources required to buy the card
    private Map<Game.Resources,Integer> requiredResources;  //Resources required to activate production power
    private Map<Game.Resources,Integer> producedResources;  //Resources produced during the activation
    private int producedFaith;                              //Faith produced during the activation
    private boolean activated;

    public DevelopmentCard(int level, Game.Colours colour, int victoryPoints, Map<Game.Resources, Integer> cost, 
                           Map<Game.Resources, Integer> requiredResources, Map<Game.Resources, Integer> producedResources, int producedFaith) {
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

    public Map<Game.Resources, Integer> getCost() {
        return cost;
    }

    public Map<Game.Resources, Integer> getrequiredResources() {
        return requiredResources;
    }

    public Map<Game.Resources, Integer> getproducedResources() {
        return producedResources;
    }

    public int getproducedFaith() {
        return producedFaith;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setColour(Game.Colours colour) {
        this.colour = colour;
    }

    public void setvictoryPoints(int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    public void setCost(Map<Game.Resources, Integer> cost) {
        this.cost = cost;
    }

    public void setrequiredResources(Map<Game.Resources, Integer> requiredResources) {
        this.requiredResources = requiredResources;
    }

    public void setproducedResources(Map<Game.Resources, Integer> producedResources) {
        this.producedResources = producedResources;
    }

    public void setproducedFaith(int producedFaith) {
        this.producedFaith = producedFaith;
    }
}
