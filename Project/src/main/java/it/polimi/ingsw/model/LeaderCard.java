package it.polimi.ingsw.model;

import java.util.*;

public class LeaderCard {
    private int victoryPoints;
    private Map<Colours,Pair<Integer,Integer>> requiredColours;
    private Map<Resources,Integer> requiredResources;
    private Ability ability;


    public LeaderCard(boolean played, boolean discarded, int victoryPoints, Map<Colours,
            Pair<Integer, Integer>> requiredColours, Map<Resources, Integer> requiredResources, Ability ability) {
        this.victoryPoints = victoryPoints;
        this.requiredColours = requiredColours;
        this.requiredResources = requiredResources;
        this.ability = ability;
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
