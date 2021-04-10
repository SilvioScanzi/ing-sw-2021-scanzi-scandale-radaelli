package it.polimi.ingsw.model;

import java.util.*;

public class LeaderCard {
    private int victoryPoints;
    private Map<Colours,Pair<Integer,Integer>> requiredColours; //Key = Colour of the required Card, Value: Pair. First element is the number of cards required, second element is the level of the cards required
    private Map<Resources,Integer> requiredResources;
    private Ability ability;

    @Override
    public String toString(){
        String tmp = "Punti vittoria: "+victoryPoints+"\n";
        for(Colours c: requiredColours.keySet()){
            tmp=tmp.concat("Colore richiesto: "+c.toString()+"; Numero di carte richiesto: "+requiredColours.get(c).getKey()+"; Livello richiesto: "+requiredColours.get(c).getValue()+"\n");
        }
        for(Resources r:requiredResources.keySet()){
            tmp=tmp.concat("Risorsa richiesta: "+r.toString()+"; Numero: "+requiredResources.get(r)+"\n");
        }
        tmp=tmp.concat(ability.toString()+"\n");
        return tmp;
    }

    public LeaderCard(int victoryPoints, Map<Colours,Pair<Integer, Integer>> requiredColours, Map<Resources,Integer> requiredResources, String type, Resources restype, int cap) {
        this.victoryPoints = victoryPoints;
        this.requiredColours = requiredColours;
        this.requiredResources = requiredResources;
        if(type.equals("DiscountAbility")){
            ability = new DiscountAbility(restype,cap);
        }
        else if(type.equals("ExtraSlotAbility")){
            ability = new ExtraSlotAbility(restype,cap);
        }
        else if(type.equals("ProductionPowerAbility")){
            ability = new ProductionPowerAbility(restype);
        }
        else if(type.equals("WhiteMarbleAbility")){
            ability = new ProductionPowerAbility(restype);
        }
    }

    public Ability getAbility() {
        return ability;
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
