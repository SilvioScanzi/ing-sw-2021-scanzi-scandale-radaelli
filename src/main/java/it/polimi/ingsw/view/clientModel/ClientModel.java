package it.polimi.ingsw.view.clientModel;

import it.polimi.ingsw.commons.*;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientModel {
    private final String myNickname;
    private final HashMap<String, ClientBoard> boards;
    private HashMap<Pair<Colours,Integer>, Integer> cardMarket;
    private Marbles[][] resourceMarket;
    private Marbles remainingMarble;
    private final int playerNumber;

    public ClientModel(HashMap<String,Integer> playerMap, String myNickname, int inkwell){
        this.myNickname = myNickname;
        boards = new HashMap<>();
        playerNumber = playerMap.size();

        int i=1;
        int j=inkwell;
        while(i<=playerNumber){
            for(String S:playerMap.keySet()){
                if(playerMap.get(S) == j){
                    boards.put(S,new ClientBoard(i,S));
                    if(!S.equals(myNickname)){
                        boards.get(S).setLeaderCardsHand(new ArrayList<>(){{add(new Triplet<>(Resources.Servants,-1,-1));add(new Triplet<>(Resources.Servants,-1,-1));add(new Triplet<>(Resources.Servants,-1,-1));add(new Triplet<>(Resources.Servants,-1,-1));}});
                    }
                }
            }
            i=i+1;
            j=(j+1)%playerNumber;
        }
        cardMarket = new HashMap<>();
    }

    //setters
    public void setCardMarket(HashMap<Pair<Colours, Integer>, Integer> cardMarket) {
        this.cardMarket = cardMarket;
    }

    public void setResourceMarket(Marbles[][] resourceMarket, Marbles remainingMarble){
        this.resourceMarket = resourceMarket;
        this.remainingMarble = remainingMarble;
    }

    public void setResourceMarket(String[][] resourceMarket, String remainingMarble){
        this.resourceMarket = new Marbles[3][4];
        for(int i=0;i<3;i++){
            for(int j=0;j<4;j++){
                this.resourceMarket[i][j] = Marbles.getMarbleFromString(resourceMarket[i][j]);
            }
        }
        this.remainingMarble = Marbles.getMarbleFromString(remainingMarble);
    }

    //getters

    public HashMap<Pair<Colours, Integer>, Integer> getCardMarket() {
        return cardMarket;
    }

    public int getWhiteMarbles(boolean row, int i) {
        int n = 0;
        if(row){
            for(int k=0;k<4;k++) n = n + ((resourceMarket[i-1][k].equals(Marbles.White))? 1:0);
        }
        else{
            for(int k=0;k<3;k++) n = n + ((resourceMarket[k][i-1].equals(Marbles.White))? 1:0);
        }
        return n;
    }

    public String getMyNickname() {
        return myNickname;
    }

    public ClientBoard getBoard(String nickname){
        return boards.get(nickname);
    }

    public Marbles[][] getResourceMarket(){
        return resourceMarket;
    }

    public Marbles getRemainingMarble() {
        return remainingMarble;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public HashMap<String, ClientBoard> getBoards() {
        return boards;
    }
}
