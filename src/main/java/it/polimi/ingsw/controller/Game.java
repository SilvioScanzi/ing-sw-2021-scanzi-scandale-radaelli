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

    public LeaderCardDeck getLeadercarddeck() {
        return leadercarddeck;
    }

    public void LCTestsetup(){
        nplayer = 1;
        inkwell = 0;
        players.add(new Pair<>("player",new Board(leadercarddeck.getLeaderCards())));
        players.get(0).getValue().getLeadercardsplayed().addAll(leadercarddeck.getLeaderCards());
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

        boolean gotConversion = false;
        for(LeaderCard LC : playerBoard.getLeadercardsplayed()){
            gotConversion = gotConversion || LC.getAbility().doConvert();
        }

        if(market.getWhiteMarbles(row, i) == requestedWMConversion.size() && gotConversion){ //checking white marbles in market equals requested conversions
            for(int j=0;j<requestedWMConversion.size() && playerBoard.getLeadercardsplayed().size()>0;j++){ //checking LC presence and if there are conversions
                if(requestedWMConversion.get(j)>0 && requestedWMConversion.get(j)<=playerBoard.getLeadercardsplayed().size()){ //checking index of LC
                    try{
                        tmp.add(leaderCardConversion(player,playerBoard.getLeadercardsplayed().get(requestedWMConversion.get(j))));
                    }catch(IllegalArgumentException e) {throw e;}
                }
            }
        }else if(requestedWMConversion.size()!=0 || gotConversion){throw new IllegalArgumentException("Requested a number of white conversions not congruent");}

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

    //from strongbox, warehouse and leader card (cloned) checks if requested Resources are present and, if so,
    //makes the canges. If there are no errors actually modifies everything, otherwise throws exception and rollback.
    private void consumeResources (Board playerBoard, Strongbox sb, Warehouse wr, HashMap<Resources, Integer> cost,
                                   HashMap<LeaderCard,Integer> LCCapacity, ArrayList<Pair<String,Integer>> userChoice)
                                    throws IllegalArgumentException{
        HashMap<Resources, Integer> selectedResources = new HashMap<>();
        for(Resources r: Resources.values()){
            selectedResources.put(r,0);
        }
        for(Pair<String,Integer> p: userChoice){
            Resources r;
            try{
                r = Resources.getResourceFromString(p.getKey());
            } catch (Exception e) {throw e;}

            if(1<=p.getValue() && p.getValue()<=3) {
                try {
                    if(wr.checkResourcePresent(p.getValue(),r)){
                        wr.subDepot(p.getValue(),1);
                        selectedResources.put(r,selectedResources.get(r)+1);
                    } else throw new IllegalArgumentException("Non hai la risorsa nel depot");
                }catch (Exception e) {e.printStackTrace();}
            } else if (p.getValue()==4 || p.getValue()==5){
                LeaderCard LC;
                try{
                    LC = playerBoard.getLeadercardsplayed().get(p.getValue()-4);
                }catch (Exception e) {throw e;}
                if(LCCapacity.get(LC)>0 && LC.getAbility().getRestype().equals(r)){
                    LCCapacity.put(LC,LCCapacity.get(LC)-1);
                    selectedResources.put(r, (1+selectedResources.get(r)));
                } else throw new IllegalArgumentException("Non hai leader card valide");
            } else if (p.getValue()==6){
                if(sb.getResource(r)>0){
                    sb.subResource(r,1);
                    selectedResources.put(r, (1+selectedResources.get(r)));
                }else throw new IllegalArgumentException("Non hai la risorsa nello strongbox");
            } else throw new IllegalArgumentException("Selezione scorretta");
        }
        for(Resources r:selectedResources.keySet()){
            if(selectedResources.get(r)!=0 && !selectedResources.get(r).equals(cost.get(r)))
                throw new IllegalArgumentException("You don't have enough resources to buy the card");
        }

    }

    //Player (nickname) selects colour and level; method checks for costs and adds to the board
    public void getDevelopmentCard(Colours c, int level, int player, int slotNumber, ArrayList<Pair<String, Integer>> userChoice) throws Exception{
        DevelopmentCard DC = developmentcardmarket.peekFirstCard(c,level);
        HashMap<Resources, Integer> cost = new HashMap<>(DC.getCost());
        HashMap<LeaderCard,Integer> LCCapacity = new HashMap<>();

        Board playerBoard = players.get(player).getValue();
        Warehouse wr = playerBoard.getWarehouse().clone();
        Strongbox sb = playerBoard.getStrongbox().clone();

        for(LeaderCard LC: playerBoard.getLeadercardsplayed()){
            LCCapacity.put(LC,LC.getAbility().getStashedResources());
        }
        //Check if LC played give some discount
        for(LeaderCard LC : playerBoard.getLeadercardsplayed()){
            cost = LC.getAbility().doDiscount(cost);
        }
        try{
            consumeResources(playerBoard, sb, wr, cost, LCCapacity, userChoice);
            Slot requestedSlot = playerBoard.getSlot(slotNumber);
            requestedSlot.addCard(DC);
            playerBoard.setWarehouse(wr);
            playerBoard.setStrongbox(sb);
            for (LeaderCard L : playerBoard.getLeadercardsplayed()) {
                L.getAbility().doUpdateSlot(L.getAbility().getRestype(),
                        LCCapacity.get(L)-L.getAbility().getStashedResources());
            }
            developmentcardmarket.getFirstCard(c, level);
        }catch(Exception e) {
            throw e;
        }
    }

    //if player chooses to activate a leader card or the base production, the last position of the ArrayList
    // contained in userChoice is the to-be-produced resource
    public void activateProduction(int player, HashMap<Integer,ArrayList<Pair<String,Integer>>> userChoice) throws IllegalArgumentException{
        boolean[] activated = new boolean[] {false,false,false,false,false,false};

        for(Integer i: userChoice.keySet()){
            if(activated[i-1])throw new IllegalArgumentException("Non puoi attivare due volte le produzioni");
            activated[i-1]=true;
        }

        HashMap<Resources, Integer> cost;
        HashMap<LeaderCard,Integer> LCCapacity = new HashMap<>();

        Board playerBoard = players.get(player).getValue();
        Warehouse wr = playerBoard.getWarehouse().clone();
        Strongbox sb = playerBoard.getStrongbox().clone();

        for(LeaderCard LC: playerBoard.getLeadercardsplayed()){
            LCCapacity.put(LC,LC.getAbility().getStashedResources());
        }
        ArrayList<Resources> tmpHand = new ArrayList<>();
        int producedFaith=0;
        for(Integer i: userChoice.keySet()){
            if (1<=i && i<=3){
                try{
                    cost = new HashMap<>(playerBoard.getSlot(i).getFirstCard().getrequiredResources());
                    producedFaith += playerBoard.getSlot(i).getFirstCard().getproducedFaith();
                    for(Resources r: playerBoard.getSlot(i).getFirstCard().getproducedResources().keySet()){
                        for(int j=0; j<playerBoard.getSlot(i).getFirstCard().getproducedResources().get(r); j++){
                            tmpHand.add(r);
                        }
                    }
                }catch (EmptyDeckException e){throw new IllegalArgumentException("Slot vuoto");}
            }
            else if (i==4 || i==5){
                try{
                    cost = new HashMap<>();
                    cost.put(playerBoard.getLeadercardsplayed().get(i-4).getAbility().getRestype(),1);
                    tmpHand.add(Resources.getResourceFromString(userChoice.get(i).remove(userChoice.get(i).size()-1).getKey()));
                    producedFaith ++;
                }catch (Exception e){throw new IllegalArgumentException("Errore nella produzione della leader card");}
            }
            else if (i==6){
                try{
                    tmpHand.add(Resources.getResourceFromString(userChoice.get(i).remove(userChoice.get(i).size()-1).getKey()));
                    int count=0;
                    for (int j=0; j<userChoice.get(i).size(); j++){
                        count += userChoice.get(i).get(j).getValue();
                    }
                    if (count==2){
                        cost = new HashMap<>();
                        for (int j=0; j<userChoice.get(i).size(); j++){
                            cost.put(Resources.getResourceFromString(userChoice.get(i).get(j).getKey()),userChoice.get(i).get(j).getValue());
                        }
                    } else throw new IllegalArgumentException("Errore nella produzione di base");

                }catch (Exception e){throw new IllegalArgumentException("Errore nella produzione della leader card");}
            }
            else throw new IllegalArgumentException("Scegli una produzione valida");
            try{
                consumeResources(playerBoard, sb, wr, cost, LCCapacity, userChoice.get(i));
            }catch (Exception e){throw new IllegalArgumentException("Impossibile produrre");}
        }

        //Actually modifying the model (no errors)
        playerBoard.setStrongbox(sb);
        playerBoard.setWarehouse(wr);
        for (LeaderCard L : playerBoard.getLeadercardsplayed()) {
            L.getAbility().doUpdateSlot(L.getAbility().getRestype(),
                    LCCapacity.get(L)-L.getAbility().getStashedResources());
        }
        playerBoard.getHand().addAll(tmpHand);
        playerBoard.dumpHandIntoStrongbox();
        for(int i=0; i<producedFaith; i++){
            players.get(player).getValue().getFaithtrack().advanceTrack();
            int tmp = players.get(player).getValue().getFaithtrack().checkPopeFavor();
            if(tmp!=-1) popeEvent(tmp);
        }
    }

    //The ArrayList of triplets contains user choices on resources to move. In particular, the string represents the resource to move,
    //the first Integer (_2) represents the source (FROM), the last Integer (_3) represents the destination (TO)
    //1-3 for the depots, 4-5 for the Leader Cards (Extra slot only), 0 to the hand which is used to make the swaps
    //In the end, if there are resources left in the Hand, there needs to be a check from the caller of this method.
    public void moveAction(int player,ArrayList<Triplet<String,Integer,Integer>> userChoice) throws IllegalArgumentException{
        Board playerBoard = players.get(player).getValue();
        Warehouse wr = playerBoard.getWarehouse().clone();
        HashMap<LeaderCard,Integer> LCCapacity = new HashMap<>();
        for(LeaderCard LC: playerBoard.getLeadercardsplayed()){
            LCCapacity.put(LC,LC.getAbility().getStashedResources());
        }
        ArrayList<Resources> tmpHand = (ArrayList<Resources>)playerBoard.getHand().clone();

        Resources r;

        //Removing every resource got from the user from the cloned storages
        for(Triplet<String,Integer,Integer> uc : userChoice) {
            //try-catch to check and remove resources from their (cloned) location
            try {
                r = Resources.getResourceFromString(uc.get_1());
                if (1 <= uc.get_2() && uc.get_2() <= 3) {
                    if (wr.checkResourcePresent(uc.get_2(), r)) {
                        wr.subDepot(uc.get_2(), 1);
                    } else throw new IllegalArgumentException("Wrong resource requested");
                } else if (4 == uc.get_2() || uc.get_2() == 5) {
                    LeaderCard LC;
                    try {
                        LC = playerBoard.getLeadercardsplayed().get(uc.get_2() - 4);
                    } catch (Exception e) {
                        throw e;
                    }
                    if (LCCapacity.get(LC) > 0 && LC.getAbility().getRestype().equals(r)) {
                        LCCapacity.put(LC, LCCapacity.get(LC) - 1);
                    } else throw new IllegalArgumentException("No corresponding leader card");
                } else if (uc.get_2() == 0) {
                    if (tmpHand.remove(r)) ;
                    else throw new IllegalArgumentException("No such resource in hand");
                } else throw new IllegalArgumentException("Invalid position requested");
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (ResourceErrorException e) {
                throw new IllegalArgumentException("Not enough resources to move");
            }
            //Resources moved to the hand are added now. This makes possible for the user to swap depots (firstly, he decides to move the
            // resources from a depot to the hand, those resources are moved, and then he can take the resources from the hand and dump
            // them into another depot)
            if(uc.get_3() == 0){
                tmpHand.add(r);
            }
        }

        //Adding every resource got from the user to the cloned storages
        for(Triplet<String,Integer,Integer> uc : userChoice){
            //try-catch to check and insert resources into their (cloned) location
            try{
                r = Resources.getResourceFromString(uc.get_1());
                if(1 <= uc.get_3() && uc.get_3() <= 3){
                    wr.addDepot(uc.get_3(), r, 1);
                }
                else if(4 == uc.get_3() || uc.get_3() == 5){
                    LeaderCard LC;
                    try{
                        LC = playerBoard.getLeadercardsplayed().get(uc.get_3()-4);
                    }catch (Exception e) {throw e;}
                    if(LCCapacity.get(LC)<LC.getAbility().getCapacity() && LC.getAbility().getRestype().equals(r)){
                        LCCapacity.put(LC,LCCapacity.get(LC)+1);
                    } else throw new IllegalArgumentException("No corresponding leader card");
                }
                else if(uc.get_3() != 0) throw new IllegalArgumentException("Invalid position requested");
            }catch(IllegalArgumentException e){throw e;}
            catch (IncompatibleResourceException e){throw new IllegalArgumentException("Invalid Resource placement");}
            catch (ResourceErrorException e){throw new IllegalArgumentException("Not enough space in warehouse");}
        }

        //if ok, actually modify everything
        playerBoard.setWarehouse(wr);
        for (LeaderCard L : playerBoard.getLeadercardsplayed()) {
            L.getAbility().doUpdateSlot(L.getAbility().getRestype(),
                    LCCapacity.get(L)-L.getAbility().getStashedResources());
        }
        //TODO: Handling the resources left in the hand
        if(tmpHand.size()>0) throw new IllegalArgumentException("There are still some resources in the hand");
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
                amount+=LC.getAbility().getStashedResources();
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