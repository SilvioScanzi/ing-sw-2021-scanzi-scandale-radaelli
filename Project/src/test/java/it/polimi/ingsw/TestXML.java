package it.polimi.ingsw;
import it.polimi.ingsw.model.DevelopmentCard;
import it.polimi.ingsw.model.DevelopmentCardMarket;

public class TestXML {
    public static void main(String[] args){
        DevelopmentCardMarket DC = new DevelopmentCardMarket();
        DevelopmentCard D = DC.getFirstCard(Game.Colours.Green,1);
        System.out.println("livello: " + D.getLevel());
        if(D.getColour().equals(Game.Colours.Green)) System.out.println("Colore: verde");
        else System.out.println("Colore: Blu");
        System.out.println("COSTO:");
        D.getCost().entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " " + entry.getValue());
        });
        System.out.println("REQUISITI:");
        D.getrequiredResources().entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " " + entry.getValue());
        });
        System.out.println("PRODOTTI:");
        D.getproducedResources().entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " " + entry.getValue());
        });
        System.out.println("Fede:" + D.getproducedFaith() + "\n");

        D = DC.getFirstCard(Game.Colours.Green,1);
        System.out.println("livello: " + D.getLevel());
        if(D.getColour().equals(Game.Colours.Green)) System.out.println("Colore: verde");
        else System.out.println("Colore: Blu");
        System.out.println("COSTO:");
        D.getCost().entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " " + entry.getValue());
        });
        System.out.println("REQUISITI:");
        D.getrequiredResources().entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " " + entry.getValue());
        });
        System.out.println("PRODOTTI:");
        D.getproducedResources().entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " " + entry.getValue());
        });
        System.out.println("Fede:" + D.getproducedFaith());
    }
}
