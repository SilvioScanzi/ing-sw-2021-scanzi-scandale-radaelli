package it.polimi.ingsw;

import it.polimi.ingsw.model.*;

public class TestMarket {
    public static void main(String[] args){
        Market m = new Market();
        System.out.println("\n"+m.toString());

        m.updateMarket(false,1);
        System.out.println("\n"+m.toString());

        m.updateMarket(true,2);
        System.out.println("\n"+m.toString());

        Warehouse w = new Warehouse();
        try{w.addDepot(1,Resources.Coins,1);}
        catch(Exception e) {e.printStackTrace();}
        try{w.addDepot(2,Resources.Stones,2);}
        catch(Exception e) {e.printStackTrace();}
        try{w.addDepot(3,Resources.Coins,3);}
        catch(Exception e) {e.printStackTrace();}
        try{w.addDepot(3,Resources.Shields,1);}
        catch(Exception e) {e.printStackTrace();}
        try{w.addDepot(3,Resources.Shields,1);}
        catch(Exception e) {e.printStackTrace();}
        System.out.println("\n"+w.toString());

        Strongbox s = new Strongbox();
        s.addResource(Resources.Coins,5);
        s.addResource(Resources.Shields,3);
        s.addResource(Resources.Servants,2);
        System.out.println("\n"+s.toString());

        ActionStack A = new ActionStack();
        System.out.println(A.toString());
        System.out.println(A.draw().toString());
        System.out.println(A.toString());
    }
}