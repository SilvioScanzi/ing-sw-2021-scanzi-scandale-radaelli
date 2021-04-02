package it.polimi.ingsw.model;

import it.polimi.ingsw.Game;

import java.util.*;

public class LeaderCard {
    private boolean played;
    private boolean discarded;
    private int victoryPoints;
    private Map<Game.Colours,Pair<Integer,Integer>> requiredColours;
    private Map<Game.Resources,Integer> requiredResources;
    private Ability ability;


    public LeaderCard(boolean played, boolean discarded, int victoryPoints, Map<Game.Colours,
            Pair<Integer, Integer>> requiredColours, Map<Game.Resources, Integer> requiredResources, Ability ability) {
        this.played = played;
        this.discarded = discarded;
        this.victoryPoints = victoryPoints;
        this.requiredColours = requiredColours;
        this.requiredResources = requiredResources;
        this.ability = ability;
    }

    public void setPlayed(boolean played) {
        this.played = played;
    }

    public void setDiscarded(boolean discarded) {
        this.discarded = discarded;
    }

    public void setVictoryPoints(int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    public void setRequiredColours(Map<Game.Colours, Pair<Integer, Integer>> requiredColours) {
        this.requiredColours = requiredColours;
    }

    public void setRequiredResources(Map<Game.Resources, Integer> requiredResources) {
        this.requiredResources = requiredResources;
    }

    public boolean isPlayed() {
        return played;
    }

    public boolean isDiscarded() {
        return discarded;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public Map<Game.Colours, Pair<Integer, Integer>> getRequiredColours() {
        return requiredColours;
    }

    public Map<Game.Resources, Integer> getRequiredResources() {
        return requiredResources;
    }
}
