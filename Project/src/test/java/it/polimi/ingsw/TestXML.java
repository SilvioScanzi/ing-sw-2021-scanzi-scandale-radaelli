package it.polimi.ingsw;
import it.polimi.ingsw.model.*;

public class TestXML {
    public static void main(String[] args){
        DevelopmentCardMarket DC = new DevelopmentCardMarket();
        DevelopmentCard D = DC.getFirstCard(Colours.Green,1);
        System.out.println("livello: " + D.getLevel());
        if(D.getColour().equals(Colours.Green)) System.out.println("Colore: verde");
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

        D = DC.getFirstCard(Colours.Green,1);
        System.out.println("livello: " + D.getLevel());
        if(D.getColour().equals(Colours.Green)) System.out.println("Colore: verde");
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
