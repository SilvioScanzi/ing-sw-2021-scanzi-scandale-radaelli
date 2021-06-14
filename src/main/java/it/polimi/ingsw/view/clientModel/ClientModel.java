package it.polimi.ingsw.view.clientModel;

import it.polimi.ingsw.commons.*;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientModel {
    private final String myNickname;
    private final HashMap<String, ClientBoard> boards;
    private HashMap<Pair<Colours,Integer>, Pair<Integer,Integer>> cardMarket;
    private Marbles[][] resourceMarket;
    private Marbles remainingMarble;
    private final int playerNumber;
    private HashMap<Integer,Pair<Resources,Integer>> LCMap = new HashMap<>();
    private boolean lorenzo = false;
    private HashMap<String,Integer> leaderBoard = null;

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
    public void setCardMarket(HashMap<Pair<Colours, Integer>, Pair<Integer,Integer>> cardMarket) {
        this.cardMarket = cardMarket;
    }

    public void setResourceMarket(Marbles[][] resourceMarket, Marbles remainingMarble){
        this.resourceMarket = resourceMarket;
        this.remainingMarble = remainingMarble;
    }

    public void setLCMap(HashMap<Integer, Pair<Resources, Integer>> LCMap) {
        this.LCMap = LCMap;
    }

    public void setLorenzo(boolean lorenzo) {
        this.lorenzo = lorenzo;
    }

    public void setLeaderBoard(HashMap<String, Integer> leaderBoard) {
        this.leaderBoard = leaderBoard;
    }

    //getters
    public HashMap<Integer, Pair<Resources, Integer>> getLCMap() {
        return LCMap;
    }

    public HashMap<Pair<Colours, Integer>, Pair<Integer,Integer>> getCardMarket() {
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

    public HashMap<String, Integer> getLeaderBoard() {
        return leaderBoard;
    }

    public boolean getLorenzo() {
        return lorenzo;
    }
}
