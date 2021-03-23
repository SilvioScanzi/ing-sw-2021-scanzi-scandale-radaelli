package it.polimi.ingsw;
import java.util.*;

public class Warehouse {
    private Game.Resources depot1;
    private Game.Resources[] depot2;
    private Game.Resources[] depot3;
    //Che schifo

    public Warehouse() {
        depot1 = Game.Resources.Nothing;
        depot2 = new Game.Resources[2];
        depot2[0] = Game.Resources.Nothing;
        depot2[1] = Game.Resources.Nothing;
        depot3 = new Game.Resources[3];
        depot3[0] = Game.Resources.Nothing;
        depot3[1] = Game.Resources.Nothing;
        depot3[2] = Game.Resources.Nothing;
    }

    public Pair<Game.Resources,Integer> getDepot(int number) throws IllegalArgumentException{

        if(number==1) {
            if (depot1.equals(Game.Resources.Nothing))
                return new Pair(Game.Resources.Nothing,0);
            else{
                return new Pair(depot1,1);
            }
        }

        else if (number==2) {
            if (depot2[0].equals(Game.Resources.Nothing))
                return new Pair(Game.Resources.Nothing,0);
            else if(depot2[1].equals(Game.Resources.Nothing))
                return new Pair(depot2,1);
            else
                return new Pair(depot2,2);
        }

        else if (number==3) {
            if (depot3[0].equals(Game.Resources.Nothing))
                return new Pair(Game.Resources.Nothing,0);
            else if(depot3[1].equals(Game.Resources.Nothing))
                return new Pair(depot3,1);
            else if(depot3[2].equals(Game.Resources.Nothing))
                return new Pair(depot3,2);
            else
                return new Pair(depot3,3);
        }

        else    throw new IllegalArgumentException("Invalid depot number");
    }

    public void addDepot (int number, Game.Resources res, int quantity) throws IllegalArgumentException, InvalidPlacementException {
        if (quantity<0)
            throw new IllegalArgumentException("Invalid Quantity");
        if(quantity>number)
            throw new InvalidPlacementException("Excessive amount of resources");
        if (number==1){
            if(depot1.equals(Game.Resources.Nothing))
                depot1=res;
            else
                throw new InvalidPlacementException("Depot already full");
        }
        if(number==2){
            if(depot2[0].equals(Game.Resources.Nothing))
                for(int i=0;i<quantity;i++){
                    depot2[i]=res;
                }
            else if (!(depot2[0].equals(res)))  throw new InvalidPlacementException("Depot occupied by another resource");
        }
    }

}


