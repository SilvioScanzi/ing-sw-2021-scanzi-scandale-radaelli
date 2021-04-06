package it.polimi.ingsw;
import it.polimi.ingsw.model.*;

public class TestXML {
    public static void main(String[] args){
        int count=1;
        DevelopmentCardMarket DC = new DevelopmentCardMarket();
        for(Colours c : Colours.values()){
            for(int i=1; i<4; i++){
                for(int j=0;j<4;j++) {
                    System.out.println(DC.getFirstCard(c,i).toString());
                    System.out.println("Carta numero: " + count + "\n");
                    count=count+1;
                }
            }
        }
    }
}
