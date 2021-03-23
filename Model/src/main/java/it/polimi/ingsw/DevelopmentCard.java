package it.polimi.ingsw;
import java.util.*;

public class DevelopmentCard {
    private int level;
    private Game.Colours colour;
    private int victorypoints;
    private Map<Game.Resources,Integer> cost;
    private Map<Game.Resources,Integer> requiredresources;
    private Map<Game.Resources,Integer> producedresources;
    private int producedfaith;

    public DevelopmentCard() {
        //Da implementare con generazione a monte
    }

    public int getLevel() {
        return level;
    }

    public Game.Colours getColour() {
        return colour;
    }

    public int getVictorypoints() {
        return victorypoints;
    }

    public Map<Game.Resources, Integer> getCost() {
        return cost;
    }

    public Map<Game.Resources, Integer> getRequiredresources() {
        return requiredresources;
    }

    public Map<Game.Resources, Integer> getProducedresources() {
        return producedresources;
    }

    public int getProducedfaith() {
        return producedfaith;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setColour(Game.Colours colour) {
        this.colour = colour;
    }

    public void setVictorypoints(int victorypoints) {
        this.victorypoints = victorypoints;
    }

    public void setCost(Map<Game.Resources, Integer> cost) {
        this.cost = cost;
    }

    public void setRequiredresources(Map<Game.Resources, Integer> requiredresources) {
        this.requiredresources = requiredresources;
    }

    public void setProducedresources(Map<Game.Resources, Integer> producedresources) {
        this.producedresources = producedresources;
    }

    public void setProducedfaith(int producedfaith) {
        this.producedfaith = producedfaith;
    }
}
