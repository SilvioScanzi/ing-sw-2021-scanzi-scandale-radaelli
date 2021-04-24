package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;

import java.util.*;

public class Game {
    private int inkwell;
    private int playerNumber;
    private final ArrayList<Pair<String,Board>> players;
    private final Market market;
    private final DevelopmentCardMarket developmentCardMarket;
    private final boolean[] vaticanReport;
    private final LeaderCardDeck leaderCardDeck;

    private final LorenzoTrack lorenzo;
    private ActionStack actionStack;

    public Game() {
        inkwell = 0;
        playerNumber = 0;
        players = new ArrayList<>();
        market = new Market();
        developmentCardMarket = new DevelopmentCardMarket();
        vaticanReport = new boolean[] {false,false,false};
        leaderCardDeck = new LeaderCardDeck();
        lorenzo = new LorenzoTrack();
        actionStack = new ActionStack();
    }

    //Only used for testing
    public Game(int Arandom){
        inkwell = 0;
        playerNumber = 0;
        players = new ArrayList<>();
        market = new Market(Arandom);
        developmentCardMarket = new DevelopmentCardMarket(Arandom);
        vaticanReport = new boolean[] {false,false,false};
        leaderCardDeck = new LeaderCardDeck(Arandom);
        lorenzo = new LorenzoTrack();
        actionStack = new ActionStack(Arandom);
    }

    //Getters
    public int getInkwell() {
        return inkwell;
    }

    public String getPlayers(int i) {
        return players.get(i).getKey();
    }

    public Board getBoard(int i){return players.get(i).getValue();}

    public DevelopmentCardMarket getDevelopmentCardMarket() {
        return developmentCardMarket;
    }


    //Methods for the game setup

    //Initializes variables and boards, assigning nicknames
    public void setup(ArrayList<String> names){
        playerNumber = names.size();
        inkwell = (int)(Math.random() * playerNumber);
        for(int i = 0; i< playerNumber; i++){
            players.add(new Pair<>(names.get(i),new Board(leaderCardDeck.getLeaderCards())));
        }
    }

    //Called on all players but the first
    public void finishingSetup(int player, ArrayList<Resources> userChoice){
        //player 2
        if(player == (inkwell + 1)% playerNumber) {
            try{
                players.get(player).getValue().getWarehouse().addDepot(1,userChoice.get(0),1);
            }catch(Exception e){e.printStackTrace();}
        }
        if(playerNumber >2){
            //player 3
            if(player == (inkwell + 2)% playerNumber) {
                players.get(player).getValue().getFaithtrack().advanceTrack();
                try{
                    players.get(player).getValue().getWarehouse().addDepot(1,userChoice.get(0),1);
                }catch(Exception e){e.printStackTrace();}
            }
        }
        if(playerNumber >3){
            //player 3
            if(player == (inkwell + 3)% playerNumber) {
                players.get(player).getValue().getFaithtrack().advanceTrack();
                try{
                    if(userChoice.get(0).equals(userChoice.get(1)))
                        players.get(player).getValue().getWarehouse().addDepot(2,userChoice.get(0),2);
                    else {
                        players.get(player).getValue().getWarehouse().addDepot(1,userChoice.get(0),1);
                        players.get(player).getValue().getWarehouse().addDepot(2,userChoice.get(1),1);
                    }
                }catch(Exception e){e.printStackTrace();}
            }
        }
    }

    //Discards leader cards in the setup phase of the game
    public void discardSelectedLC(int player, int[] discardedLC) throws IllegalArgumentException, IndexOutOfBoundsException{
        if(discardedLC.length!=2) throw new IllegalArgumentException("Input from user is not compatible");

        //sorting in descending order the index
        if(discardedLC[0]<discardedLC[1]){
            int tmp = discardedLC[0];
            discardedLC[0] = discardedLC[1];
            discardedLC[1] = tmp;
        }

        if(discardedLC[0]>4 || discardedLC[0]<0 || discardedLC[1]>3 || discardedLC[1]<0){
            throw new IndexOutOfBoundsException("Selected leader cards do not exist");
        }

        for (int i : discardedLC) {
            players.get(player).getValue().discardLeaderCard(i);
        }
    }


    //Methods used in the actual game

    private void popeEvent(int index){
        if(!(vaticanReport[index-1])) {
            for(int i = 0; i< playerNumber; i++){
                players.get(i).getValue().getFaithtrack().setPopeFavor(index);
            }
            vaticanReport[index-1] = true;
        }
    }

    //Adds the resources from the market to the hand, ignoring white marbles
    public void BuyMarketResourcesAction(int player, boolean row, int i, ArrayList<Integer> requestedWMConversion) throws ActionAlreadyDoneException, IllegalArgumentException, IndexOutOfBoundsException {
        Board playerBoard = players.get(player).getValue();
        if(playerBoard.getActionDone()) throw new ActionAlreadyDoneException("Current player has already done his action for the turn");
        if((row && (i<1 || i>3)) || (!row && (i<1 || i>4))) throw new IndexOutOfBoundsException();
        ArrayList<Resources> tmp = new ArrayList<>();

        ArrayList<Integer> conversionIndex = new ArrayList<>();
        for(int j=0;j<playerBoard.getLeadercardsplayed().size();j++){
            if(playerBoard.getLeadercardsplayed().get(j).getAbility().doConvert()) conversionIndex.add(j);
        }

        if(conversionIndex.size() == 1){
            for(int j=0;j<market.getWhiteMarbles(row, i);j++){
                tmp.add(playerBoard.getLeadercardsplayed().get(conversionIndex.get(0)).getAbility().getResType());
            }
        }
        //checking if white marbles in market equals requested conversions
        else if(conversionIndex.size()>1 && market.getWhiteMarbles(row, i) == requestedWMConversion.size()) {
            for (Integer index : requestedWMConversion) {
                //checking LC presence and if there are conversions
                if (index > 0 && index <= playerBoard.getLeadercardsplayed().size())
                    //checking index of LC
                    tmp.add(playerBoard.getLeadercardsplayed().get(index - 1).getAbility().getResType());
                else throw new IndexOutOfBoundsException("Selected leader card does not exist");
            }
        }
        else if(conversionIndex.size()!=0) throw new IllegalArgumentException("Requested a number of white conversions not congruent");

        ArrayList<Marbles> marbles = market.updateMarket(row,i);
        playerBoard.getHand().addAll(standardConversion(marbles,playerBoard));
        playerBoard.getHand().addAll(tmp);

        playerBoard.setActionDone(true);
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

    //Discard resources and advance other players
    public void discardRemainingResources(int player){
        Board playerBoard = players.get(player).getValue();
        for(Resources ignored : playerBoard.getHand()){
            for(int i = 0; i< playerNumber; i++){
                if(i!=player){
                    players.get(i).getValue().getFaithtrack().advanceTrack();
                }
            }
            //Checking if any player got to a Pope tile
            int cell=-1;
            for(int i = 0; i< playerNumber; i++){
                cell = Math.max(players.get(i).getValue().getFaithtrack().checkPopeFavor(),cell);
            }
            if(cell!=-1) popeEvent(cell);
        }
        playerBoard.getHand().clear();
    }

    //Player selects colour and level; method checks for costs and adds to the board the development card
    public void BuyDevelopmentCardAction(Colours c, int level, int player, int slotNumber, ArrayList<Pair<String, Integer>> userChoice)
            throws ActionAlreadyDoneException,EmptyException,InvalidPlacementException,RequirementsNotMetException,ResourceErrorException,LeaderCardNotCompatibleException,IndexOutOfBoundsException,IllegalArgumentException{
        Board playerBoard = players.get(player).getValue();
        if(playerBoard.getActionDone()) throw new ActionAlreadyDoneException("Current player has already done his action for the turn");
        DevelopmentCard DC;
        DC = developmentCardMarket.peekFirstCard(c, level);
        HashMap<Resources, Integer> cost = new HashMap<>(DC.getCost());
        HashMap<LeaderCard,Integer> LCCapacity = new HashMap<>();

        Warehouse wr = playerBoard.getWarehouse().clone();
        Strongbox sb = playerBoard.getStrongbox().clone();

        for(LeaderCard LC: playerBoard.getLeadercardsplayed()){
            LCCapacity.put(LC,LC.getAbility().getStashedResources());
        }

        //Check if LC played give some discount
        for(LeaderCard LC : playerBoard.getLeadercardsplayed()){
            cost = LC.getAbility().doDiscount(cost);
        }

        //Try to consume the resources needed
        consumeResources(playerBoard, sb, wr, cost, LCCapacity, userChoice);
        //Getting the slot requested by the player
        Slot requestedSlot = playerBoard.getSlot(slotNumber);
        requestedSlot.addCard(DC);
        playerBoard.setWarehouse(wr);
        playerBoard.setStrongbox(sb);
        for (LeaderCard L : playerBoard.getLeadercardsplayed())
        { L.getAbility().doUpdateSlot(L.getAbility().getResType(), LCCapacity.get(L)-L.getAbility().getStashedResources());}
        developmentCardMarket.getFirstCard(c, level);
        playerBoard.setActionDone(true);
    }

    //if player chooses to activate a leader card or the base production, the last position of the ArrayList
    //contained in userChoice is the to-be-produced resource
    public void activateProductionAction(int player, HashMap<Integer,ArrayList<Pair<String,Integer>>> userChoice)
            throws ActionAlreadyDoneException, LeaderCardNotCompatibleException, IllegalArgumentException, EmptyException, ResourceErrorException, RequirementsNotMetException {
        Board playerBoard = players.get(player).getValue();
        if(playerBoard.getActionDone()) throw new ActionAlreadyDoneException("Current player has already done his action for the turn");

        HashMap<Resources, Integer> cost;
        HashMap<LeaderCard,Integer> LCCapacity = new HashMap<>();

        Warehouse wr = playerBoard.getWarehouse().clone();
        Strongbox sb = playerBoard.getStrongbox().clone();

        for(LeaderCard LC: playerBoard.getLeadercardsplayed()){
            LCCapacity.put(LC,LC.getAbility().getStashedResources());
        }
        ArrayList<Resources> tmpHand = new ArrayList<>();
        int producedFaith=0;
        for(Integer i: userChoice.keySet()){
            if (1<=i && i<=3){
                cost = new HashMap<>(playerBoard.getSlot(i).getFirstCard().getRequiredResources());
                producedFaith += playerBoard.getSlot(i).getFirstCard().getProducedFaith();
                for(Resources r: playerBoard.getSlot(i).getFirstCard().getProducedResources().keySet()){
                    for(int j = 0; j<playerBoard.getSlot(i).getFirstCard().getProducedResources().get(r); j++){
                        tmpHand.add(r);
                    }
                }
            }
            else if (i==4 || i==5) {
                if (!playerBoard.getLeadercardsplayed().get(i - 4).getAbility().doActivate())
                    throw new LeaderCardNotCompatibleException("Leader card is not compatible");
                cost = new HashMap<>();
                cost.put(playerBoard.getLeadercardsplayed().get(i - 4).getAbility().getResType(), 1);
                tmpHand.add(Resources.getResourceFromString(userChoice.get(i).remove(userChoice.get(i).size() - 1).getKey()));
                producedFaith++;

            }
            else if (i==6){
                    tmpHand.add(Resources.getResourceFromString((userChoice.get(i).remove(userChoice.get(i).size()-1).getKey())));

                    if (userChoice.get(i).size()==2){
                        cost = new HashMap<>();
                        for (int j=0; j<userChoice.get(i).size(); j++){
                            Resources r = Resources.getResourceFromString(userChoice.get(i).get(j).getKey());
                            cost.put(r,(cost.get(r) == null)?1:cost.get(r)+1);
                        }
                    } else throw new IllegalArgumentException("Errore nella produzione di base");
            } else throw new IllegalArgumentException("Scegli una produzione valida");

            consumeResources(playerBoard, sb, wr, cost, LCCapacity, userChoice.get(i));
        }

        //Actually modifying the model (no errors)
        playerBoard.setStrongbox(sb);
        playerBoard.setWarehouse(wr);
        for (LeaderCard L : playerBoard.getLeadercardsplayed()) {
            L.getAbility().doUpdateSlot(L.getAbility().getResType(),
                    LCCapacity.get(L)-L.getAbility().getStashedResources());
        }
        playerBoard.getHand().addAll(tmpHand);
        playerBoard.dumpHandIntoStrongbox();
        for(int i=0; i<producedFaith; i++){
            players.get(player).getValue().getFaithtrack().advanceTrack();
            int tmp = players.get(player).getValue().getFaithtrack().checkPopeFavor();
            if(tmp!=-1) popeEvent(tmp);
        }

        playerBoard.setActionDone(true);
    }

    //Helper method used to modify the board of a player regarding his resources.
    //From strongbox, warehouse and leader card (cloned) checks if requested resources are present and, if so,
    //consumes them, changing the state of the board. If there are no errors modifies everything, otherwise throws exception and rollback.
    private void consumeResources (Board playerBoard, Strongbox sb, Warehouse wr, HashMap<Resources, Integer> cost, HashMap<LeaderCard,Integer> LCCapacity, ArrayList<Pair<String,Integer>> userChoice)
            throws ResourceErrorException,LeaderCardNotCompatibleException,RequirementsNotMetException,IllegalArgumentException{
        HashMap<Resources, Integer> selectedResources = new HashMap<>();
        for(Resources r: Resources.values()){
            selectedResources.put(r,0);
        }
        for(Pair<String,Integer> p: userChoice){
            Resources r;
            r = Resources.getResourceFromString(p.getKey());

            if(1<=p.getValue() && p.getValue()<=3) {
                if (wr.checkResourcePresent(p.getValue(), r)) {
                    try {
                        wr.subDepot(p.getValue(), 1);
                        selectedResources.put(r, selectedResources.get(r) + 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else throw new ResourceErrorException("There aren't enough resources in the depot");

            } else if (p.getValue()==4 || p.getValue()==5){
                LeaderCard LC;
                LC = playerBoard.getLeadercardsplayed().get(p.getValue()-4);
                if(LCCapacity.get(LC)>0 && LC.getAbility().getResType().equals(r)){
                    LCCapacity.put(LC,LCCapacity.get(LC)-1);
                    selectedResources.put(r, (1+selectedResources.get(r)));
                } else throw new LeaderCardNotCompatibleException("Selected leader card are not compatible");
            } else if (p.getValue()==6){
                if(sb.getResource(r)>0){
                    try {
                        sb.subResource(r, 1);
                    }catch (Exception e) {e.printStackTrace();}
                    selectedResources.put(r, (1+selectedResources.get(r)));
                }else throw new ResourceErrorException("There aren't enough resources in the strongbox");
            } else throw new IllegalArgumentException("Selection not compatible");
        }
        for(Resources r:selectedResources.keySet()){
            if(selectedResources.get(r)!=0 && !selectedResources.get(r).equals(cost.get(r)))
                throw new RequirementsNotMetException("You don't have enough resources to buy the card");
        }

    }

    //The ArrayList of triplets contains user choices on resources to move. The string represents the resource to move,
    //the first Integer (_2) represents the source (FROM), the last Integer (_3) represents the destination (TO)
    //1-3 for the depots, 4-5 for the Leader Cards (Extra slot only), 0 to the hand which is used to make the swaps
    //In the end, if there are resources left in the hand, there needs to be a check from the caller of this method.
    public void moveResources(int player, ArrayList<Triplet<String,Integer,Integer>> userChoice) throws BadRequestException, LeaderCardNotCompatibleException,
            ResourceErrorException, InvalidPlacementException, IllegalArgumentException, IncompatibleResourceException, ResourcesLeftInHandException{
        Board playerBoard = players.get(player).getValue();
        Warehouse wr = playerBoard.getWarehouse().clone();
        HashMap<LeaderCard,Integer> LCCapacity = new HashMap<>();
        for(LeaderCard LC: playerBoard.getLeadercardsplayed()){
            LCCapacity.put(LC,LC.getAbility().getStashedResources());
        }
        ArrayList<Resources> tmpHand = (ArrayList<Resources>)playerBoard.getHand().clone();

        //Simplifying the request
        boolean reduced;
        int j;
        for(int i=0; i<userChoice.size();i++) {
            reduced = false;
            j = i + 1;
            while (j < userChoice.size() && !reduced) {
                if (userChoice.get(i).get_1().equals(userChoice.get(j).get_1()) && userChoice.get(i).get_3().equals(userChoice.get(j).get_2())) {
                    reduced = true;
                    userChoice.get(j).set_2(userChoice.get(i).get_2());
                    userChoice.remove(i);
                    i = i-1;
                }
                j++;
            }
        }

        for(int i=0;i<userChoice.size();i++){
            if(userChoice.get(i).get_2().equals(userChoice.get(i).get_3())){
                userChoice.remove(i);
                i=i-1;
            }
        }

        Resources r;

        //Removing every resource got from the user from the cloned storages
        for(Triplet<String,Integer,Integer> uc : userChoice) {
            //try-catch to check and remove resources from their (cloned) location
            try {
                r = Resources.getResourceFromString(uc.get_1());
            } catch (IllegalArgumentException e) { throw new BadRequestException("Resource requested in a wrong format"); }
            if (1 <= uc.get_2() && uc.get_2() <= 3) {
                if (wr.checkResourcePresent(uc.get_2(), r)) {
                    try {
                        wr.subDepot(uc.get_2(), 1);
                    }catch(IllegalArgumentException | ResourceErrorException e){e.printStackTrace();}
                } else throw new BadRequestException("Wrong resource requested");
            } else if (4 == uc.get_2() || uc.get_2() == 5) {
                LeaderCard LC;
                LC = playerBoard.getLeadercardsplayed().get(uc.get_2() - 4);
                if (LCCapacity.get(LC) > 0 && LC.getAbility().getResType().equals(r)) {
                    LCCapacity.put(LC, LCCapacity.get(LC) - 1);
                } else throw new LeaderCardNotCompatibleException("Not compatible leader card");
            } else if (uc.get_2() == 0) {
                if (!tmpHand.remove(r)) throw new ResourceErrorException("No such resource in hand");
            } else throw new IllegalArgumentException("Invalid position requested");

            //Resources moved to the hand are added now. This makes possible for the user to swap depots (firstly, he decides to move the
            // resources from a depot to the hand, those resources are moved, and then he can take the resources from the hand and dump
            // them into another depot)
            if (uc.get_3() == 0) {
                tmpHand.add(r);
            }
        }

        //Adding every resource got from the user to the cloned storages
        for(Triplet<String,Integer,Integer> uc : userChoice){
            //try-catch to check and insert resources into their (cloned) location
            try {
                r = Resources.getResourceFromString(uc.get_1());
            } catch (IllegalArgumentException e) { throw new BadRequestException("Resource requested in a wrong format"); }
                if(1 <= uc.get_3() && uc.get_3() <= 3){
                    wr.addDepot(uc.get_3(), r, 1);
                }
                else if(4 == uc.get_3() || uc.get_3() == 5){
                    LeaderCard LC;
                    LC = playerBoard.getLeadercardsplayed().get(uc.get_3()-4);
                    if(LCCapacity.get(LC)<LC.getAbility().getCapacity() && LC.getAbility().getResType().equals(r)){
                        LCCapacity.put(LC,LCCapacity.get(LC)+1);
                    } else throw new LeaderCardNotCompatibleException("Not compatible leader card");
                }
                else if(uc.get_3() != 0) throw new IllegalArgumentException("Invalid position requested");

        }

        //if ok, actually modify everything
        playerBoard.setWarehouse(wr);
        for (LeaderCard L : playerBoard.getLeadercardsplayed()) {
            L.getAbility().doUpdateSlot(L.getAbility().getResType(),
                    LCCapacity.get(L)-L.getAbility().getStashedResources());
        }
        if(tmpHand.size()>0) throw new ResourcesLeftInHandException("There are still some resources in the hand");
    }

    public void discardLeaderCard(int player, int leaderCardIndex) throws IndexOutOfBoundsException{
        if(leaderCardIndex>players.get(player).getValue().getLeadercards().size() || leaderCardIndex<=0) throw new IndexOutOfBoundsException("Leader card does not exist");
        players.get(player).getValue().discardLeaderCard(leaderCardIndex);
        players.get(player).getValue().getFaithtrack().advanceTrack();
        int tmp = players.get(player).getValue().getFaithtrack().checkPopeFavor();
        if(tmp!=-1) popeEvent(tmp);
    }

    public void playLeaderCard(int player, int leaderCardIndex) throws RequirementsNotMetException{
        players.get(player).getValue().playLeaderCard(leaderCardIndex);
    }

    public boolean checkEndGame(int player){
        if(vaticanReport[2]) return true;
        else{
            Board playerBoard = players.get(player).getValue();
            int count = 0;
            for(int i=0;i<3;i++){
                count += playerBoard.getSlot(i+1).getDevelopmentCards().size();
            }
            if(count == 7) {
                countVictoryPoints();
                return true;
            }
        }
        return false;
    }

    public void countVictoryPoints(){
        int tmp;
        for(int i = 0; i< playerNumber; i++){
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
                for(DevelopmentCard DC : playerBoard.getSlot(j).getDevelopmentCards()){
                    tmp+=DC.getVictoryPoints();
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

    //Only used for single player
    public LorenzoTrack getLorenzo() {
        return lorenzo;
    }

    public ActionStack getActionStack() {
        return actionStack;
    }

    public void activatedToken(){
        ActionToken AT = actionStack.draw();
        switch(AT){
            case Advance2: {
                for(int i=0;i<2;i++){
                    lorenzo.advanceBlackCross();
                    if(lorenzo.checkPopeFavor()!=-1) popeEvent(lorenzo.checkPopeFavor());
                }
                break;
            }
            case AdvanceAndRefresh: {
                lorenzo.advanceBlackCross();
                if(lorenzo.checkPopeFavor()!=-1) popeEvent(lorenzo.checkPopeFavor());
                actionStack = new ActionStack();
                break;
            }
            case DeleteBlue: developmentCardMarket.deleteCards(Colours.Blue); break;
            case DeleteGreen: developmentCardMarket.deleteCards(Colours.Green); break;
            case DeletePurple: developmentCardMarket.deleteCards(Colours.Purple); break;
            case DeleteYellow: developmentCardMarket.deleteCards(Colours.Yellow); break;
        }
    }

    public boolean checkLorenzoWin(){
        if(lorenzo.getBlackCross() == 24) return true;
        return developmentCardMarket.lorenzoWin();
    }
}