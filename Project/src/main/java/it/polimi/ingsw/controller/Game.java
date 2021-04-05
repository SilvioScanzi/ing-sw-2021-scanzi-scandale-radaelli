package it.polimi.ingsw.controller;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;
import java.util.*;

public class Game {
    private int inkwell;
    private int nplayer;
    private ArrayList<Pair<String,Board>> players;
    private Market market;
    private DevelopmentCardMarket developmentcardmarket;
    private boolean[] vaticanReport;
    private LeaderCardDeck leadercarddeck;

    private LorenzoTrack lorenzo;
    private ActionStack actionStack;

    public Game() {
        inkwell = 0;
        nplayer = 0;
        players = new ArrayList<>();
        market = new Market();
        developmentcardmarket = new DevelopmentCardMarket();
        vaticanReport = new boolean[] {false,false,false};
        leadercarddeck = new LeaderCardDeck();
        lorenzo = new LorenzoTrack();
        actionStack = new ActionStack();
    }

    //Initializes variables and boards, assigning nicknames
    public void setup(String[] names){
        nplayer = names.length;
        inkwell = (int)(Math.random() * nplayer);
        for(int i=0;i<nplayer;i++){
            players.add(new Pair<>(names[i],new Board(names[i],leadercarddeck.getLeaderCards())));
        }
    }

    public void getMarketResources(int player, boolean row, int i){
        Board playerBoard = players.get(player).getValue();
        ArrayList<Marbles> marbles = market.updateMarket(row,i);
        playerBoard.getHand().addAll(conversion(marbles,playerBoard));
        //playerBoard.clearWarehouse();
    }

    //MANCA: controllo sulle leader cards
    private ArrayList<Resources> conversion(ArrayList<Marbles> marbles, Board playerBoard){
        ArrayList<LeaderCard> leadercardsplayed = playerBoard.getLeadercardsplayed();
        ArrayList<Resources> tmp = new ArrayList<>();
        for(Marbles m : marbles){
            switch(m){
                case Blue: tmp.add(Resources.Shields);
                case Grey: tmp.add(Resources.Stones);
                case Purple: tmp.add(Resources.Servants);
                case Yellow: tmp.add(Resources.Coins);
                case Red: {
                    playerBoard.getFaithtrack().advanceTrack();
                    if(playerBoard.getFaithtrack().checkPopeFavor()!=-1) popeEvent(playerBoard.getFaithtrack().checkPopeFavor());
                }
                //case White:;
            }
        }
        return tmp;
    }

    private void popeEvent(int index){
        if(!(vaticanReport[index-1])) {
            for(int i=0;i<nplayer;i++){
                players.get(i).getValue().getFaithtrack().setPopeFavor(index);
            }
            vaticanReport[index-1] = true;
        }
    }

    public void setResources(int player, Resources r, int depot){
        Board playerBoard = players.get(player).getValue();
        try{
            playerBoard.getWarehouse().addDepot(depot,r,1);
        }
        catch(IllegalArgumentException e){
            e.printStackTrace();
        }
        catch(ResourceErrorException e){
            playerBoard.setFlagResourceError(true);
        }
        catch(IncompatibleResourceException e){
            playerBoard.setFlagIncompatibleResources(true);
        }
    }

    public void discardRemainingResources(int player){
        Board playerBoard = players.get(player).getValue();
        for(Resources r : playerBoard.getHand()){
            for(int i=0;i<nplayer;i++){
                if(i!=player){
                    players.get(i).getValue().getFaithtrack().advanceTrack();
                }
            }
            int cell=-1;
            for(int i=0;i<nplayer;i++){
                cell = Math.max(players.get(i).getValue().getFaithtrack().checkPopeFavor(),cell);
            }
            if(cell!=-1) popeEvent(cell);
        }
        playerBoard.getHand().clear();
    }

    //MANCA: controllo sugli sconti delle leader cards
    //MANCA: controllo sulle risorse delle leader cards
    //Player (nickname) selects colour and level; method checks for costs and adds to the board
    public void getDevelopmentCard(Colours c, int level, int player, int slotNumber){
        DevelopmentCard DC = developmentcardmarket.peekFirstCard(c,level);
        HashMap<Resources, Integer> cost = new HashMap<>(DC.getCost());

        Board playerBoard = players.get(player).getValue();
        Warehouse wr = playerBoard.getWarehouse().clone();
        Strongbox sb = playerBoard.getStrongbox().clone();

        //warehouse check and reduce resources in cost, to show how many are still required in strongbox
        for(int i=1;i<=3;i++){
            Pair<Optional<Resources>,Integer> tmp = wr.getDepot(i);
            if(tmp.getKey().isPresent()){
                if(cost.get(tmp.getKey().get()) != null){
                    if(cost.get(tmp.getKey().get()) <= tmp.getValue()) {    //have enough resources
                        try{
                            wr.subDepot(i,cost.get(tmp.getKey().get()));
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                        cost.remove(tmp.getKey().get());
                    }
                    else {    //not enough resources, modify the remaining value in cost
                        try{
                            wr.subDepot(i,tmp.getValue());
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                        cost.put(tmp.getKey().get(), cost.get(tmp.getKey().get()) - tmp.getValue());
                    }
                }
            }
        }
        //pool strongbox and warehouse resources
        boolean check = true;
        if(!cost.isEmpty()){
            for(Resources r : cost.keySet()){
                if(sb.getResource(r) - cost.get(r) < 0) check = false;  //not enough resources
                else {
                    try{
                        sb.subResource(r,cost.get(r));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

        if(check){
            try{
                Slot requestedSlot = playerBoard.getSlot(slotNumber);
                requestedSlot.addCard(DC);      //TESTING: si modifica anche nell board reale?
                playerBoard.setWarehouse(wr);
                playerBoard.setStrongbox(sb);
                developmentcardmarket.getFirstCard(c,level);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void activateBaseProduction(int player, ArrayList<Resources> usedResources, Resources gotResources){
        try{
            players.get(player).getValue().production(usedResources,new ArrayList<Resources>() {{add(gotResources);}}); //TESTING MASSIVO
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    //slots number goes from 1 to 3
    public void activateDevelopmentCardProduction(int player, int slot){
        try{
            int faith = players.get(player).getValue().slotProduction(slot);
            for(int i=0; i<faith; i++){
                players.get(player).getValue().getFaithtrack().advanceTrack();
                int tmp = players.get(player).getValue().getFaithtrack().checkPopeFavor();
                if(tmp!=-1) popeEvent(tmp);
            }
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    public boolean checkEndGame(int player){
        if(vaticanReport[2]) return true;
        else{
            Board playerBoard = players.get(player).getValue();
            int count = 0;
            for(int i=0;i<3;i++){
                count += playerBoard.getSlot(i+1).getDevelopmentcards().size();
            }
            if(count == 7) return true;
        }
        return false;
    }



    //ONLY FOR SINGLE PLAYER
    public void activatedToken(){
        ActionToken AT = actionStack.draw();
        switch(AT){
            case Advance2: {
                for(int i=0;i<2;i++){
                    lorenzo.advanceBlackCross();
                    if(lorenzo.checkPopeFavor()!=-1) popeEvent(lorenzo.checkPopeFavor());
                }
            }
            case AdvanceAndRefresh: {
                lorenzo.advanceBlackCross();
                if(lorenzo.checkPopeFavor()!=-1) popeEvent(lorenzo.checkPopeFavor());
                actionStack = new ActionStack();
            }
            case DeleteBlue: developmentcardmarket.deleteCards(Colours.Blue);
            case DeleteGreen: developmentcardmarket.deleteCards(Colours.Green);
            case DeletePurple: developmentcardmarket.deleteCards(Colours.Purple);
            case DeleteYellow: developmentcardmarket.deleteCards(Colours.Yellow);
            default: ;
        }
    }

    public boolean checkLorenzoWin(){
        if(lorenzo.getBlackCross() == 24) return true;
        if(developmentcardmarket.lorenzoWin()) return true;
        return false;
    }
}
