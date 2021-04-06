package it.polimi.ingsw.model;

import java.util.*;

public class LeaderCard {
    private int victoryPoints;
    private Map<Colours,Pair<Integer,Integer>> requiredColours; //Key = Colour of the required Card, Value: Pair. First element is the number of cards required, second element is the level of the cards required
    private Map<Resources,Integer> requiredResources;
    private Ability ability;


    public LeaderCard(int victoryPoints, Map<Colours,Pair<Integer, Integer>> requiredColours, Map<Resources,Integer> requiredResources, Ability ability) {
        this.victoryPoints = victoryPoints;
        this.requiredColours = requiredColours;
        this.requiredResources = requiredResources;
        this.ability = ability;
    }

    public Ability getAbility() {
        return ability;
    }

    public void setVictoryPoints(int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    public void setRequiredColours(Map<Colours, Pair<Integer, Integer>> requiredColours) {
        this.requiredColours = requiredColours;
    }

    public void setRequiredResources(Map<Resources, Integer> requiredResources) {
        this.requiredResources = requiredResources;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public Map<Colours, Pair<Integer, Integer>> getRequiredColours() {
        return requiredColours;
    }

    public Map<Resources, Integer> getRequiredResources() {
        return requiredResources;
    }
}
