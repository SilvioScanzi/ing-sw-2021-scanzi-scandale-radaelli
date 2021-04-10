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

    //costruttore per il testing non randomico
    public Game(int Arandom){
        inkwell = 0;
        nplayer = 0;
        players = new ArrayList<>();
        market = new Market(Arandom);
        developmentcardmarket = new DevelopmentCardMarket(Arandom);
        vaticanReport = new boolean[] {false,false,false};
        leadercarddeck = new LeaderCardDeck(Arandom);
        lorenzo = new LorenzoTrack();
        actionStack = new ActionStack(Arandom);
    }

    //TODO: TABELLA A PAGINA 3 (IN BASSO) DEL REGOLAMENTO
    //Initializes variables and boards, assigning nicknames
    public void setup(ArrayList<String> names){
        nplayer = names.size();
        inkwell = (int)(Math.random() * nplayer);
        for(int i=0;i<nplayer;i++){
            players.add(new Pair<>(names.get(i),new Board(leadercarddeck.getLeaderCards())));
        }
    }

    public int getInkwell() {
        return inkwell;
    }

    public Market getMarket() {
        return market;
    }

    public DevelopmentCardMarket getDevelopmentcardmarket() {
        return developmentcardmarket;
    }

    public String getPlayers(int i) {
        return players.get(i).getKey();
    }

    public Board getBoard(int i){return players.get(i).getValue();}

    //Adds the resources from the market to the hand, ignoring white marbles
    public void getMarketResources(int player, boolean row, int i, ArrayList<Integer> requestedWMConversion) throws IllegalArgumentException {
        Board playerBoard = players.get(player).getValue();
        ArrayList<Resources> tmp = new ArrayList<>();

        if(market.getWhiteMarbles(row, i) == requestedWMConversion.size()){ //checking white marbles in market equals requested conversions
            for(int j=0;j<requestedWMConversion.size() && playerBoard.getLeadercardsplayed().size()>0;j++){ //checking LC presence and if there are conversions
                if(requestedWMConversion.get(j)>0 && requestedWMConversion.get(j)<=playerBoard.getLeadercardsplayed().size()){ //checking index of LC
                    try{
                        tmp.add(leaderCardConversion(player,playerBoard.getLeadercardsplayed().get(requestedWMConversion.get(j))));
                    }catch(IllegalArgumentException e) {throw e;}
                }
            }
        }

        ArrayList<Marbles> marbles = market.updateMarket(row,i);
        playerBoard.getHand().addAll(standardConversion(marbles,playerBoard));
        playerBoard.getHand().addAll(tmp);
    }

    public void discardSelectedLC(int player, int[] discardedLC) throws IllegalArgumentException {
        if(discardedLC.length!=2) throw new IllegalArgumentException("Errore nelle LC da eliminare");

        //sorting in descending order the index
        if(discardedLC[0]<discardedLC[1]){
            int tmp = discardedLC[0];
            discardedLC[0] = discardedLC[1];
            discardedLC[1] = tmp;
        }

        for(int i=0;i<discardedLC.length;i++){
            if(1<=discardedLC[i] && discardedLC[i]<=4-i) players.get(player).getValue().discardLeaderCard(i);
            else throw new IllegalArgumentException("Indice non risolvibile");
        }

    }

    //Converts Marbles to resources
    private ArrayList<Resources> standardConversion(ArrayList<Marbles> marbles, Board playerBoard){
        ArrayList<Resources> tmp = new ArrayList<>();
        for(Marbles m : marbles){
            switch(m){
                case Blue: tmp.add(Resources.Shields); break;
                case Grey: tmp.add(Resources.Stones); break;
                case Purple: tmp.add(Resources.Servants); break;
                case Yellow: tmp.add(Resources.Coins); break;
                case Red: {
                    playerBoard.getFaithtrack().advanceTrack();
                    if(playerBoard.getFaithtrack().checkPopeFavor()!=-1) popeEvent(playerBoard.getFaithtrack().checkPopeFavor());
                    break;
                }
                default: break;
            }
        }
        return tmp;
    }

    //Checks if LC provided can convert white marbles to resources, if so, gives them to the hand
    public Resources leaderCardConversion(int player, LeaderCard LC) throws IllegalArgumentException{
        Board playerBoard = players.get(player).getValue();
        Resources tmp;
        if(LC.getAbility().doConvert()){
            tmp = LC.getAbility().getRestype();
        }
        else throw new IllegalArgumentException("Leader card has a different ability");

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

    //Stash resources into depots
    public void setResourcesToDepot(int player, Resources r, int depot) throws IllegalArgumentException, ResourceErrorException, IncompatibleResourceException{
        Board playerBoard = players.get(player).getValue();
        try{
            playerBoard.getWarehouse().addDepot(depot,r,1);
        }
        catch(Exception e){
            throw e;
        }
    }

    //Stash resources into leadercards
    public void setResourcesToLeaderCard(int player, Resources r, int amount, LeaderCard LC) throws IllegalArgumentException{
        Board playerBoard = players.get(player).getValue();
        if(!LC.getAbility().doUpdateSlot(r,amount));
        throw new IllegalArgumentException("Action went wrong");
    }

    //Discard resources and advance other players
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

    //Player (nickname) selects colour and level; method checks for costs and adds to the board
    public void getDevelopmentCard(Colours c, int level, int player, int slotNumber) throws Exception{
        DevelopmentCard DC = developmentcardmarket.peekFirstCard(c,level);
        HashMap<Resources, Integer> cost = new HashMap<>(DC.getCost());

        Board playerBoard = players.get(player).getValue();
        Warehouse wr = playerBoard.getWarehouse().clone();
        Strongbox sb = playerBoard.getStrongbox().clone();

        //Check if LC played give some discount
        for(LeaderCard LC : playerBoard.getLeadercardsplayed()){
            cost = LC.getAbility().doDiscount(cost);
        }

        //warehouse check and reduce resources in cost, to show how many are still required in strongbox
        for(int i=1;i<=3;i++){
            Pair<Optional<Resources>,Integer> tmp = wr.getDepot(i);
            if(tmp.getKey().isPresent()){
                if(cost.get(tmp.getKey().get()) != null){
                    if(cost.get(tmp.getKey().get()) <= tmp.getValue()) {    //have enough resources
                        try{
                            Resources r = tmp.getKey().get();
                            wr.subDepot(i,cost.get(tmp.getKey().get()));
                            cost.remove(r);
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    else {    //not enough resources, modify the remaining value in cost
                        try{
                            Resources r = tmp.getKey().get();
                            int amount = tmp.getValue();
                            wr.subDepot(i,tmp.getValue());
                            cost.put(r, cost.get(r) - amount);
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        //leadercard abilities check (extra resource storage)
        HashMap<LeaderCard,Integer> LCCapacity = new HashMap<>();
        for(LeaderCard LC : playerBoard.getLeadercardsplayed()) {
            for (Resources r : cost.keySet()) {
                if (r.equals(LC.getAbility().getRestype())) {
                    if (cost.get(r) > LC.getAbility().getCapacity()) {
                        cost.put(r, cost.get(r) - LC.getAbility().getCapacity());
                        LCCapacity.put(LC, -LC.getAbility().getCapacity());
                    } else {
                        LCCapacity.put(LC, -cost.get(r));
                        cost.remove(r);
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
                requestedSlot.addCard(DC);
                playerBoard.setWarehouse(wr);
                playerBoard.setStrongbox(sb);
                for(LeaderCard L : playerBoard.getLeadercardsplayed()){
                    if(LCCapacity.get(L)!=null){
                        L.getAbility().doUpdateSlot(L.getAbility().getRestype(),LCCapacity.get(L));
                    }
                }
                developmentcardmarket.getFirstCard(c,level);
            }catch (Exception e){
                throw e;
            }
        }
        else{
            throw new IllegalArgumentException("You don't have enough resources to buy the card");
        }
    }

    public void activateBaseProduction(int player, ArrayList<Resources> usedResources, Resources gotResources) throws IllegalArgumentException{
        try{
            players.get(player).getValue().production(usedResources,new ArrayList<Resources>() {{add(gotResources);}}); //TESTING MASSIVO
        }catch (IllegalArgumentException e){
            throw e;
        }
    }

    //slots number goes from 1 to 3
    public void activateDevelopmentCardProduction(int player, int slot) throws EmptyDeckException{
        try{
            int faith = players.get(player).getValue().slotProduction(slot);
            for(int i=0; i<faith; i++){
                players.get(player).getValue().getFaithtrack().advanceTrack();
                int tmp = players.get(player).getValue().getFaithtrack().checkPopeFavor();
                if(tmp!=-1) popeEvent(tmp);
            }
        }catch (EmptyDeckException e){
            throw e;
        }
    }

    public void activateLeaderCardProduction(int player, int leaderCardIndex, Resources gotResource) throws IllegalArgumentException{
        try{
            players.get(player).getValue().leaderProduction(leaderCardIndex,gotResource);
            players.get(player).getValue().getFaithtrack().advanceTrack();
            int tmp = players.get(player).getValue().getFaithtrack().checkPopeFavor();
            if(tmp!=-1) popeEvent(tmp);
        }
        catch(IllegalArgumentException e){
            throw e;
        }
    }

    public void discardLeaderCard(int player, int leaderCardIndex){
        players.get(player).getValue().discardLeaderCard(leaderCardIndex);
        players.get(player).getValue().getFaithtrack().advanceTrack();
        int tmp = players.get(player).getValue().getFaithtrack().checkPopeFavor();
        if(tmp!=-1) popeEvent(tmp);
    }

    public void playLeaderCard(int player, int leaderCardIndex) throws IllegalArgumentException{
        try {
            players.get(player).getValue().playLeaderCard(leaderCardIndex);
        }
        catch(Exception e) {throw e;}
    }

    public boolean checkEndGame(int player){
        if(vaticanReport[2]) return true;
        else{
            Board playerBoard = players.get(player).getValue();
            int count = 0;
            for(int i=0;i<3;i++){
                count += playerBoard.getSlot(i+1).getDevelopmentcards().size();
            }
            if(count == 7) {
                countVictoryPoints();
                return true;
            }
        }
        return false;
    }

    private void countVictoryPoints(){
        int tmp;
        for(int i=0;i<nplayer;i++){
            Board playerBoard = players.get(i).getValue();
            tmp=0;

            //faithTrack points
            int faithMarker = playerBoard.getFaithtrack().getFaithMarker();
            if(3<=faithMarker && faithMarker<=5) tmp+=1;
            else if(faithMarker<=8) tmp+=2;
            else if(faithMarker<=11) tmp+=4;
            else if(faithMarker<=14) tmp+=6;
            else if(faithMarker<=17) tmp+=9;
            else if(faithMarker<=20) tmp+=12;
            else if(faithMarker<=23) tmp+=16;
            else if(faithMarker==24) tmp+=20;

            for(int j=0;j<3;j++){
                if(playerBoard.getFaithtrack().getPopeFavor()[j]) tmp+=2+j;
            }

            //leadercard points
            for(LeaderCard LC : playerBoard.getLeadercardsplayed()){
                tmp += LC.getVictoryPoints();
            }

            //development card points
            for(int j=1;j<=3;j++){
                for(DevelopmentCard DC : playerBoard.getSlot(j).getDevelopmentcards()){
                    tmp+=DC.getvictoryPoints();
                }
            }

            //resource points
            int amount = 0;
            for(int j=1;j<=3;j++){
                amount+=playerBoard.getWarehouse().getDepot(j).getValue();
            }
            for(Resources r : Resources.values()){
                amount+=playerBoard.getStrongbox().getResource(r);
            }
            for(LeaderCard LC : playerBoard.getLeadercardsplayed()){
                amount+=LC.getAbility().getCapacity();
            }
            tmp+=(amount-amount%5)/5;

            playerBoard.setVictoryPoints(tmp);
        }
    }



    //ONLY FOR SINGLE PLAYER
    public ActionToken activatedToken(){
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
        }
        return AT;
    }

    public boolean checkLorenzoWin(){
        if(lorenzo.getBlackCross() == 24) return true;
        if(developmentcardmarket.lorenzoWin()) return true;
        return false;
    }
}