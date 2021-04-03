package it.polimi.ingsw;
import it.polimi.ingsw.model.*;

public class TestXML {
    public static void main(String[] args){
        int count=1;
        DevelopmentCardMarket DC = new DevelopmentCardMarket();
        for(Colours c : Colours.values()){
            for(int i=1; i<4; i++){
                for(int j=0;j<4;j++) {
                    System.out.println("Colore: " + c + " Livello: " + i);
                    DevelopmentCard D = DC.getFirstCard(c, i);
                    System.out.println("Punti vittoria: " + D.getvictoryPoints());
                    System.out.println("Costo: ");
                    D.getCost().entrySet().forEach(entry -> {
                        System.out.println(entry.getKey() + " " + entry.getValue());
                    });
                    System.out.println("Risorse necessarie per la produzione: ");
                    D.getrequiredResources().entrySet().forEach(entry -> {
                        System.out.println(entry.getKey() + " " + entry.getValue());
                    });
                    System.out.println("Risorse prodotte: ");
                    D.getproducedResources().entrySet().forEach(entry -> {
                        System.out.println(entry.getKey() + " " + entry.getValue());
                    });
                    System.out.println("Fede prodotta: " + D.getproducedFaith());
                    System.out.println("Carta numero: " + count + "\n");
                    count=count+1;
                }
            }
        }
    }
}
