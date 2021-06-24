package it.polimi.ingsw.controller;

import it.polimi.ingsw.commons.*;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.observers.ModelObservable;

import java.util.*;

public class Game extends ModelObservable {
    private int inkwell;
    private int playerNumber;
    private final ArrayList<Board> players;
    private final ResourceMarket resourceMarket;
    private final DevelopmentCardMarket developmentCardMarket;
    private final boolean[] vaticanReport;
    private final LeaderCardDeck leaderCardDeck;

    private final LorenzoTrack lorenzo;
    private ActionStack actionStack;

    public Game() {
        inkwell = 0;
        playerNumber = 0;
        players = new ArrayList<>();
        resourceMarket = new ResourceMarket();
        developmentCardMarket = new DevelopmentCardMarket();
        vaticanReport = new boolean[] {false,false,false};
        leaderCardDeck = new LeaderCardDeck();
        lorenzo = new LorenzoTrack();
        actionStack = new ActionStack();
    }

    /**
     * method used for testing, it initialize a game with a standard configuration (further explained in each model class)
     * @param Arandom is just used to overload the method and the methods in the model
     */
    public Game(int Arandom){
        inkwell = 0;
        playerNumber = 0;
        players = new ArrayList<>();
        resourceMarket = new ResourceMarket(Arandom);
        developmentCardMarket = new DevelopmentCardMarket(Arandom);
        vaticanReport = new boolean[] {false,false,false};
        leaderCardDeck = new LeaderCardDeck(Arandom);
        lorenzo = new LorenzoTrack();
        actionStack = new ActionStack(Arandom);
    }

    //Setters
    public void setInkwell(int inkwell) {
        this.inkwell = inkwell;
    }

    //Getters
    public int getInkwell() {
        return inkwell;
    }

    public Board getBoard(int i){return players.get(i);}

    public Board getBoard(String nickname){
        for(int i=0;i<playerNumber;i++){
            if(getBoard(i).getNickname().equals(nickname))
                return getBoard(i);
        }
        return null;
    }

    public ResourceMarket getMarket() {
        return resourceMarket;
    }

    public DevelopmentCardMarket getDevelopmentCardMarket() {
        return developmentCardMarket;
    }

    /**
     * the method returns all the Leader Cards of the chosen player, played or not.
     * The leader cards are returned with their index (which is the position of that card in the board) and a pair of:
     * Resources which is the resource used for the ability and an Integer which is the number of victory points
     * which the card gives. With these 2 information it can be determined which card it is
     * @param nickname is the name of the chosen player
     * @return leader cards of the player
     */
    public HashMap<Integer,Pair<Resources,Integer>> getLCMap(String nickname){
        HashMap<Integer,Pair<Resources,Integer>> tmp = new HashMap<>();
        Board playerBoard = getBoard(nickname);
        for(Integer i : playerBoard.getLeaderCardsHand().keySet()){
            tmp.put(i,new Pair<>(playerBoard.getLeaderCardsHand().get(i).getAbility().getResType(),playerBoard.getLeaderCardsHand().get(i).getVictoryPoints()));
        }
        for(Integer i : playerBoard.getLeaderCardsPlayed().keySet()){
            tmp.put(i,new Pair<>(playerBoard.getLeaderCardsPlayed().get(i).getAbility().getResType(),playerBoard.getLeaderCardsPlayed().get(i).getVictoryPoints()));
        }
        return tmp;
    }


    //Methods for the game setup

    /**
     * Initializes variables and boards, assigning nicknames and dealing each player 4 leader cards
     * (2 of which will be discarded)
     * @param names is the list of names of the players in the current game
     */
    public void setup(ArrayList<String> names){
        playerNumber = names.size();
        for(int i = 0; i< playerNumber; i++){
            players.add(new Board(leaderCardDeck.getLeaderCards(), names.get(i)));
        }
        //send to the players both the markets
        notifyDCMarket(developmentCardMarket);
        notifyResourceMarket(resourceMarket.getGrid(),resourceMarket.getRemainingMarble());
        for(int i = 0; i< playerNumber; i++){
            notifyLCHand(players.get(i).getLeaderCardsHand(),players.get(i).getNickname());
        }
        if(playerNumber == 1){
            notifyLorenzo(lorenzo,players.get(0).getFaithTrack().getPopeFavor());
        }
    }

    /**
     * The method is called by all players but the first. A player specifies which resource (or 2 resources) wants
     * to get from the setup and, after checking his position (2 -> 1 resource, 3 -> 1 resource and 1 faith point, 4 -> 2 resources and 2 faith points)
     * if the request is correct, the boards are updated
     * @param player position of the player in the game (from 1 to n number of total players)
     * @param userChoice resource(s) which the player wants to get, passed via String
     * @throws IllegalArgumentException if the String which should represent a Resource (MO,PI,SC,SE) is in a wrong format
     */
    public void finishingSetup(int player, ArrayList<String> userChoice) throws IllegalArgumentException{

        ArrayList<Resources> R = new ArrayList<>();
        for(String s : userChoice){
             R.add(Resources.getResourceFromAbbr(s));
        }

        //player 2
        if(player == (inkwell + 1)% playerNumber) {
            try{
                players.get(player).getWarehouse().addDepot(1,R.get(0),1);
            }catch(Exception e){e.printStackTrace();}
        }
        //player 3
        if(playerNumber >2){
            if(player == (inkwell + 2)% playerNumber) {
                players.get(player).getFaithTrack().advanceTrack();
                notifyFT(getBoard(player).getFaithTrack(),getBoard(player).getNickname());
                try{
                    players.get(player).getWarehouse().addDepot(1,R.get(0),1);
                }catch(Exception e){e.printStackTrace();}
            }
        }
        //player 4
        if(playerNumber >3){
            if(player == (inkwell + 3)% playerNumber) {
                players.get(player).getFaithTrack().advanceTrack();
                notifyFT(getBoard(player).getFaithTrack(),getBoard(player).getNickname());
                try{
                    if(R.get(0).equals(R.get(1)))
                        players.get(player).getWarehouse().addDepot(2,R.get(0),2);
                    else {
                        players.get(player).getWarehouse().addDepot(1,R.get(0),1);
                        players.get(player).getWarehouse().addDepot(2,R.get(1),1);
                    }
                }catch(Exception e){e.printStackTrace();}
            }
        }
        notifyWR(players.get(player).getWarehouse(),players.get(player).getNickname());
    }

    //Discards leader cards in the setup phase of the game

    /**
     * The method is used in the setup phase of the game, when players need to specify which 2 leader cards they want to discard.
     * @param player position of the player
     * @param discardedLC array of integers which specify the indexes of the card to discard
     * @throws IllegalArgumentException if the array has not exactly 2 indexes or the indexes are identical
     * @throws IndexOutOfBoundsException if the indexes specified are out of bounds (they have to be between 1 and 4)
     */
    public void discardSelectedLC(int player, int[] discardedLC) throws IllegalArgumentException, IndexOutOfBoundsException{
        if(discardedLC.length != 2 || discardedLC[0] == discardedLC[1]) throw new IllegalArgumentException("Input from user is not compatible");

        //sorting in descending order the index
        if(discardedLC[0]<discardedLC[1]){
            int tmp = discardedLC[0];
            discardedLC[0] = discardedLC[1];
            discardedLC[1] = tmp;
        }

        if(discardedLC[0] > 4 || discardedLC[0] <= 0 || discardedLC[1] <= 0){
            throw new IndexOutOfBoundsException("Selected leader cards do not exist");
        }

        for (int i : discardedLC) {
            players.get(player).discardLeaderCard(i);
        }

        ArrayList<LeaderCard> LCHand = new ArrayList<>();
        for(Integer I : players.get(player).getLeaderCardsHand().keySet()){
            LCHand.add(players.get(player).getLeaderCardsHand().get(I));

        }
        players.get(player).getLeaderCardsHand().clear();
        players.get(player).getLeaderCardsHand().put(0,LCHand.remove(0));
        players.get(player).getLeaderCardsHand().put(1,LCHand.remove(0));

        notifyLCHand(players.get(player).getLeaderCardsHand(),players.get(player).getNickname());
    }


    //Methods used in the actual game

    /**
     * The method check if there can be a possible pope event. It is called after a player reaches a pope tile.
     * Firstly, it is checked if the pope event for that tile was already triggered, if not it triggers it,
     * setting the pope favors in all the boards which are qualified for it
     * @param index is the index of the pope event (1 for the first, 2 for the second, 3 for the third)
     */
    private void popeEvent(int index){
        if(!(vaticanReport[index-1])) {
            for(int i = 0; i< playerNumber; i++){
                players.get(i).getFaithTrack().setPopeFavor(index);
                if(players.get(i).getFaithTrack().getPopeFavor()[index-1]){
                    notifyFT(players.get(i).getFaithTrack(),players.get(i).getNickname());
                }
            }
            vaticanReport[index-1] = true;
        }
    }

    /**
     * The method is used to perform the first part of the "buy resources" action. Player selects row/column
     * and the method check if the action can be performed. In particular, it is checked if the player has played 1
     * leader card with the ability of conversion of the white marble (or 0) and, if so, it converts automatically.
     * If two leader cards of this type are played, player needs to specify for each white marble which card he wants
     * to use.
     * @param player position of the player
     * @param row boolean which indicates if the players chose a row (true) or a column (false)
     * @param i index of the row/column chosen by the player
     * @param requestedWMConversion Arraylist of indexes of the leader cards used if a player has exactly 2 leader cards with the ability
     *                              to convert the white marble into some other resource. The indexes specify for each white marble of
     *                              the row/column which leader card is going to be used for the conversion
     * @throws ActionAlreadyDoneException if the player has already done his action for his turn
     * @throws IllegalArgumentException if the indexes selected aren't exactly the same number of white marbles in the selected row/column
     * @throws IndexOutOfBoundsException if the row/column selected doesn't exist (i out of bounds)
     */
    public synchronized void BuyMarketResourcesAction(int player, boolean row, int i, ArrayList<Integer> requestedWMConversion) throws ActionAlreadyDoneException, IllegalArgumentException, IndexOutOfBoundsException {
        Board playerBoard = players.get(player);
        if(playerBoard.getActionDone()) throw new ActionAlreadyDoneException("Current player has already done his action for the turn");
        if((row && (i<1 || i>3)) || (!row && (i<1 || i>4))) throw new IndexOutOfBoundsException();
        ArrayList<Resources> tmp = new ArrayList<>();

        ArrayList<Integer> conversionIndex = new ArrayList<>();
        for(Integer j : playerBoard.getLeaderCardsPlayed().keySet()){
            if(playerBoard.getLeaderCardsPlayed().get(j).getAbility().doConvert()) conversionIndex.add(j);
        }

        if(conversionIndex.size() == 1){
            for(int j = 0; j< resourceMarket.getWhiteMarbles(row, i); j++){
                tmp.add(playerBoard.getLeaderCardsPlayed().get(conversionIndex.get(0)).getAbility().getResType());
            }
        }
        //checking if white marbles in market equals requested conversions
        else if(conversionIndex.size()>1 && resourceMarket.getWhiteMarbles(row, i) == requestedWMConversion.size()) {
            for (Integer index : requestedWMConversion) {
                //checking LC presence and if there are conversions
                if (index > 0 && index <= playerBoard.getLeaderCardsPlayed().size())
                    //checking index of LC
                    tmp.add(playerBoard.getLeaderCardsPlayed().get(index - 1).getAbility().getResType());
                else throw new IndexOutOfBoundsException("Selected leader card does not exist");
            }
        }
        else if(conversionIndex.size()!=0) throw new IllegalArgumentException("Requested a number of white conversions not congruent");

        ArrayList<Marbles> marbles = resourceMarket.updateMarket(row,i);
        playerBoard.getHand().addAll(standardConversion(marbles,playerBoard));
        playerBoard.getHand().addAll(tmp);

        notifyMarketHand(playerBoard.getHand(), playerBoard.getNickname(), resourceMarket.getGrid(),resourceMarket.getRemainingMarble());
        notifyActionDone(playerBoard.getNickname());

        playerBoard.setActionDone(true);
        playerBoard.setLastActionMarket(true);
        playerBoard.setMoveNeeded(true);
    }

    /**
     * Helper method which converts Marbles to resources, ignoring the white marble
     * @param marbles is the Arraylist of the non-white marbles selected by the player
     * @param playerBoard board of the player whose Faith Track needs to be updated (if a red marble is in the set)
     * @return The Arraylist of resources got from the action
     */
    private ArrayList<Resources> standardConversion(ArrayList<Marbles> marbles, Board playerBoard){
        ArrayList<Resources> tmp = new ArrayList<>();
        for(Marbles m : marbles){
            switch(m){
                case Blue: tmp.add(Resources.Shields); break;
                case Grey: tmp.add(Resources.Stones); break;
                case Purple: tmp.add(Resources.Servants); break;
                case Yellow: tmp.add(Resources.Coins); break;
                case Red: {
                    playerBoard.getFaithTrack().advanceTrack();
                    if(playerBoard.getFaithTrack().checkPopeFavor()!=-1) popeEvent(playerBoard.getFaithTrack().checkPopeFavor());
                    notifyFT(playerBoard.getFaithTrack(),playerBoard.getNickname());
                    break;
                }
                default: break;
            }
        }
        return tmp;
    }

    //Discard resources and advance other players

    /**
     * Used to discard all the resources left in the player's "Hand" after a market action.
     * For each resource discarded, every other player gets to advance his faith track
     * @param player position of the player who is discarding the resources
     */
    public synchronized void discardRemainingResources(int player) {
        Board playerBoard = players.get(player);
        if (playerNumber > 1) {
            for (Resources ignored : playerBoard.getHand()) {
                for (int i = 0; i < playerNumber; i++) {
                    if (i != player) {
                        players.get(i).getFaithTrack().advanceTrack();
                    }
                    //Checking if any player got to a Pope tile
                    int cell = -1;
                    for (int j = 0; j < playerNumber; j++) {
                        cell = Math.max(players.get(j).getFaithTrack().checkPopeFavor(), cell);
                    }
                    if (cell != -1) popeEvent(cell);
                }
            }
            for (int i = 0; i < playerNumber; i++) {
                if (i != player) notifyFT(players.get(i).getFaithTrack(), players.get(i).getNickname());
            }
        } else {
            for (Resources ignored : playerBoard.getHand()) {
                lorenzo.advanceBlackCross();
                if (lorenzo.checkPopeFavor() != -1) popeEvent(lorenzo.checkPopeFavor());
            }
            notifyLorenzo(lorenzo,players.get(0).getFaithTrack().getPopeFavor());
        }

        playerBoard.getHand().clear();
        notifyHand(new ArrayList<>(), playerBoard.getNickname());
        playerBoard.setMoveNeeded(false);
        playerBoard.setLastActionMarket(false);
        notifyResourceBuyDone(playerBoard.getNickname());
    }

    /**
     * Method used for buying a development card from the market. User specifies which card via various parameters and
     * which resources he's going to use to buy that card. Different exceptions are thrown if anything goes wrong.
     * @param c Colour of the selected development card
     * @param level Level of the selected development card
     * @param player position of the player who is performing the action
     * @param slotNumber slot in which the player wants to put the card
     * @param userChoice Arraylist of the specified resources used to buy the card. Represented via a Pair of String (Abbreviation of the resource)
     *                   and Integer which represent the place from which the player is picking the resource. 1-3 for depots in warehouse,
     *                   4-5 for leader cards, 6 for strongbox
     * @throws ActionAlreadyDoneException if the player has already done his action for his turn
     * @throws EmptyException if the card selected doesn't exist (the relative pile is empty)
     * @throws InvalidPlacementException if the card is placed on a slot which cannot contain the card
     * @throws RequirementsNotMetException if the specified resources aren't enough/exactly correct for buying the card
     * @throws ResourceErrorException if the specified resources aren't actually present in the specified locations
     * @throws LeaderCardNotCompatibleException if the player wanted to use a resource from a leader card which is not of the correct
     * type (has an ability which isn't extra space for resources)
     * @throws IndexOutOfBoundsException if the player specifies a location which doesn't exist
     * @throws IllegalArgumentException if the specified resources are in a wrong format
     */
    public synchronized void BuyDevelopmentCardAction(Colours c, int level, int player, int slotNumber, ArrayList<Pair<String, Integer>> userChoice)
            throws ActionAlreadyDoneException,EmptyException,InvalidPlacementException,RequirementsNotMetException,ResourceErrorException,LeaderCardNotCompatibleException,IndexOutOfBoundsException,IllegalArgumentException{

        Board playerBoard = players.get(player);
        if(playerBoard.getActionDone()) throw new ActionAlreadyDoneException("Current player has already done his action for the turn");
        DevelopmentCard DC;
        DC = developmentCardMarket.peekFirstCard(c, level);
        HashMap<Resources, Integer> cost = new HashMap<>(DC.getCost());
        HashMap<LeaderCard,Integer> LCCapacity = new HashMap<>();

        Warehouse wr = playerBoard.getWarehouse().clone();
        Strongbox sb = playerBoard.getStrongbox().clone();

        for(Integer I : playerBoard.getLeaderCardsPlayed().keySet()){
            LeaderCard LC = playerBoard.getLeaderCardsPlayed().get(I);
            LCCapacity.put(LC,LC.getAbility().getStashedResources());
        }

        //Check if LC played give some discount
        for(Integer I : playerBoard.getLeaderCardsPlayed().keySet()){
            LeaderCard LC = playerBoard.getLeaderCardsPlayed().get(I);
            cost = LC.getAbility().doDiscount(cost);
        }


        //Try to consume the resources needed
        consumeResources(playerBoard, sb, wr, cost, LCCapacity, userChoice);

        //Getting the slot requested by the player
        Slot requestedSlot = playerBoard.getSlot(slotNumber);
        requestedSlot.addCard(DC);
        notifySlot(playerBoard.getSlots(), playerBoard.getNickname());

        if(!wr.equals(playerBoard.getWarehouse())) {
            playerBoard.setWarehouse(wr);
            notifyWR(playerBoard.getWarehouse(), playerBoard.getNickname());
        }

        if(!sb.equals(playerBoard.getStrongbox())){
            playerBoard.setStrongbox(sb);
            notifySB(playerBoard.getStrongbox(),playerBoard.getNickname());
        }

        boolean updated = false;
        for (Integer I : playerBoard.getLeaderCardsPlayed().keySet()) {
            LeaderCard L = playerBoard.getLeaderCardsPlayed().get(I);
            if(LCCapacity.get(L)!=L.getAbility().getStashedResources()) {
                L.getAbility().doUpdateSlot(L.getAbility().getResType(), LCCapacity.get(L) - L.getAbility().getStashedResources());
                updated = true;
            }
        }
        if(updated) {
            notifyLCPlayed(playerBoard.getLeaderCardsPlayed(), playerBoard.getNickname());
        }


        developmentCardMarket.getFirstCard(c, level);
        playerBoard.setActionDone(true);
        notifyActionDone(playerBoard.getNickname());
        notifyDCMarket(developmentCardMarket);
    }

    /**
     * Method used to activate the productions of any card/base production. The player chooses which productions he wants
     * to activate and, for each production, which resources will be used for that production.
     * @param player position of the player who is performing the action
     * @param userChoice HashMap with the number of the production as the index (1-3 for the slots, 4-5 for the leader cards,
     *                   6 for the base production) and an Arraylist of pairs which contains the resources used to activate each production.
     *                   Specifically, the String represents the resource selected and the integer the location from which the resource is picked.
     *                   (1-3 for depots in warehouse, 4-5 for leader cards, 6 for strongbox)
     *                   In the case of index 4-5-6 of the production (leader cards or base production) the player also
     *                   has to specify which resource he wants to get. In the last position of the relative Arraylist,
     *                   there is the resource chosen with a symbolic -1 for the position
     * @throws ActionAlreadyDoneException if the player has already done his action for his turn
     * @throws LeaderCardNotCompatibleException if the player chose to activate a leader card for his production,
     * but the selected leader card has another ability
     * @throws IllegalArgumentException if the player chose something other than 1-6 as the index of the production
     * @throws EmptyException if the slot specified for the production is empty
     * @throws ResourceErrorException if the player specified a resource which isn't actually on his board
     * @throws RequirementsNotMetException if the player chose the resources to activate the production incorrectly
     */
    public synchronized void activateProductionAction(int player, HashMap<Integer,ArrayList<Pair<String,Integer>>> userChoice)
            throws IndexOutOfBoundsException,ActionAlreadyDoneException,LeaderCardNotCompatibleException,IllegalArgumentException,EmptyException,ResourceErrorException,RequirementsNotMetException {
        Board playerBoard = players.get(player);
        if(playerBoard.getActionDone()) throw new ActionAlreadyDoneException("Current player has already done his action for the turn");
        HashMap<Resources, Integer> cost;
        HashMap<LeaderCard,Integer> LCCapacity = new HashMap<>();

        Warehouse wr = playerBoard.getWarehouse().clone();
        Strongbox sb = playerBoard.getStrongbox().clone();

        for(Integer I : playerBoard.getLeaderCardsPlayed().keySet()){
            LeaderCard LC = playerBoard.getLeaderCardsPlayed().get(I);
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
                if (!playerBoard.getLeaderCardsPlayed().get(i - 4).getAbility().doActivate())
                    throw new LeaderCardNotCompatibleException("Leader card is not compatible");
                cost = new HashMap<>();
                cost.put(playerBoard.getLeaderCardsPlayed().get(i - 4).getAbility().getResType(), 1);
                tmpHand.add(Resources.getResourceFromAbbr(userChoice.get(i).remove(userChoice.get(i).size() - 1).getKey()));
                producedFaith++;
            }
            else if (i==6){
                    tmpHand.add(Resources.getResourceFromAbbr((userChoice.get(i).remove(userChoice.get(i).size()-1).getKey())));

                    if (userChoice.get(i).size()==2){
                        cost = new HashMap<>();
                        for (int j=0; j<userChoice.get(i).size(); j++){
                            Resources r = Resources.getResourceFromAbbr(userChoice.get(i).get(j).getKey());
                            cost.put(r,(cost.get(r) == null)?1:cost.get(r)+1);
                        }
                    } else throw new RequirementsNotMetException("Error in the base production");
            } else throw new IllegalArgumentException("Error in the selection of the production");

            consumeResources(playerBoard, sb, wr, cost, LCCapacity, userChoice.get(i));
        }

        //Actually modifying the model (no errors)
        if(tmpHand.size()>0) {
            playerBoard.setStrongbox(sb);
            playerBoard.getHand().addAll(tmpHand);
            playerBoard.dumpHandIntoStrongbox();
            notifySB(playerBoard.getStrongbox(), playerBoard.getNickname());
        }

        if(producedFaith>0) {
            for (int i = 0; i < producedFaith; i++) {
                players.get(player).getFaithTrack().advanceTrack();
                int tmp = players.get(player).getFaithTrack().checkPopeFavor();
                if (tmp != -1) popeEvent(tmp);
            }
            notifyFT(players.get(player).getFaithTrack(),players.get(player).getNickname());
        }

        if(!wr.equals(playerBoard.getWarehouse())) {
            playerBoard.setWarehouse(wr);
            notifyWR(playerBoard.getWarehouse(), playerBoard.getNickname());
        }

        boolean updated = false;
        for (Integer I : playerBoard.getLeaderCardsPlayed().keySet()) {
            LeaderCard L = playerBoard.getLeaderCardsPlayed().get(I);
            if(LCCapacity.get(L)!=L.getAbility().getStashedResources()) {
                L.getAbility().doUpdateSlot(L.getAbility().getResType(), LCCapacity.get(L) - L.getAbility().getStashedResources());
                updated = true;
            }
        }
        if(updated) {
            notifyLCPlayed(playerBoard.getLeaderCardsPlayed(), playerBoard.getNickname());
        }
        playerBoard.setActionDone(true);
        notifyActionDone(playerBoard.getNickname());
    }

    /**
     *  Helper method used to consume the resources of a player board. The method is always called on cloned storages
     *  and changes the state of these storages after performing the required consumptions, but only after
     *  checking if the requested resources are present. Otherwise, throws an exception and nothing is changed.
     * @param playerBoard board that needs to be updated
     * @param sb cloned strongbox of that board
     * @param wr cloned warehouse of that board
     * @param cost Hashmap in which there are the resources needed (and their number) for the required action (buying a card or activating a production)
     * @param LCCapacity cloned storages related to leader cards with the ability to contain extra resources
     * @param userChoice Arraylist of pair (resource and the location from which they are taken)
     * @throws ResourceErrorException if the specified resources aren't actually on the player board
     * @throws LeaderCardNotCompatibleException if the leader card used to store/retrieve resources has an ability which isn't extra space for resources
     * or if the leader card has the right ability, but contains a different type of resources
     * @throws RequirementsNotMetException if after the selection, the resources aren't enough or right to perform the action
     * @throws IllegalArgumentException if the format of the resources isn't right
     */
    private void consumeResources (Board playerBoard, Strongbox sb, Warehouse wr, HashMap<Resources, Integer> cost, HashMap<LeaderCard,Integer> LCCapacity, ArrayList<Pair<String,Integer>> userChoice)
            throws ResourceErrorException,LeaderCardNotCompatibleException,RequirementsNotMetException,IllegalArgumentException{
        if(userChoice.size()==0){
            throw new RequirementsNotMetException("You don't have enough resources to perform the action");
        }
        HashMap<Resources, Integer> selectedResources = new HashMap<>();
        for(Resources r: Resources.values()){
            selectedResources.put(r,0);
        }
        for(Pair<String,Integer> p: userChoice){
            Resources r;
            r = Resources.getResourceFromAbbr(p.getKey());

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
                LC = playerBoard.getLeaderCardsPlayed().get(p.getValue()-4);
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
            } else throw new IndexOutOfBoundsException("Selection not compatible");
        }
        for(Resources r:selectedResources.keySet()){
            if(selectedResources.get(r)!=0 && !selectedResources.get(r).equals(cost.get(r)))
                throw new RequirementsNotMetException("You don't have enough resources to buy the card");
        }

    }


    /**
     * Method used to move resources between depots and leader cards. First, the request is simplified, reducing the moves
     * performed, compressing them into a single move for each resource.
     * At the end of the move, if there are resources left in the hand, it is checked whether the move was performed after the
     * buy resource. If so, the resources are discarded
     * @param player position of the player who is performing the action
     * @param userChoice ArrayList of triplets which contains player choices on resources to move. The string represents the resource to move
     *                   the first Integer represents the source location (FROM), the last Integer represents the destination (TO)
     *                   1-3 for the depots, 4-5 for the Leader Cards (Extra slot only), 0 to the hand (used to contain the resources got from the market)
     * @throws BadRequestException if the player is trying to put resources in the hand
     * @throws LeaderCardNotCompatibleException if the player is trying to put resources on a leader card with an incorrect ability
     * @throws ResourceErrorException if the player doesn't actually has the resources in his board
     * @throws InvalidPlacementException if the player moves incorrectly the resources (at the end, there is the same type of the resource in different depots)
     * @throws IllegalArgumentException if the player chooses an invalid index (other than 0-5)
     * @throws IncompatibleResourceException if the player moves a resource into a depot which contains another type of resource
     */
    public synchronized void moveResources(int player, ArrayList<Triplet<String,Integer,Integer>> userChoice) throws BadRequestException, LeaderCardNotCompatibleException,
            ResourceErrorException, IllegalArgumentException, IncompatibleResourceException, InvalidPlacementException{
        Board playerBoard = players.get(player);
        Warehouse wr = playerBoard.getWarehouse().clone();
        HashMap<LeaderCard,Integer> LCCapacity = new HashMap<>();
        for(Integer I : playerBoard.getLeaderCardsPlayed().keySet()){
            LeaderCard LC = playerBoard.getLeaderCardsPlayed().get(I);
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

        for (Triplet<String, Integer, Integer> stringIntegerIntegerTriplet : userChoice) {
            if (stringIntegerIntegerTriplet.get_3() == 0) {
                throw new BadRequestException("Resources cannot be moved into hand");
            }
        }

        Resources r;

        //Removing every resource got from the user from the cloned storages
        for(Triplet<String,Integer,Integer> uc : userChoice) {
            //try-catch to check and remove resources from their (cloned) location
            try {
                r = Resources.getResourceFromAbbr(uc.get_1());
            } catch (IllegalArgumentException e) { throw new BadRequestException("Resource requested in a wrong format"); }
            if (1 <= uc.get_2() && uc.get_2() <= 3) {
                if (wr.checkResourcePresent(uc.get_2(), r)) {
                    try {
                        wr.subDepot(uc.get_2(), 1);
                    }catch(IllegalArgumentException | ResourceErrorException e){e.printStackTrace();}
                } else throw new BadRequestException("Wrong resource requested");
            } else if (4 == uc.get_2() || uc.get_2() == 5) {
                LeaderCard LC;
                LC = playerBoard.getLeaderCardsPlayed().get(uc.get_2() - 4);
                if (LCCapacity.get(LC) > 0 && LC.getAbility().getResType().equals(r)) {
                    LCCapacity.put(LC, LCCapacity.get(LC) - 1);
                } else throw new LeaderCardNotCompatibleException("Not compatible leader card");
            } else if (uc.get_2() == 0) {
                if (!tmpHand.remove(r)) {
                    throw new ResourceErrorException("No such resource in hand");
                }
            } else throw new IllegalArgumentException("Invalid position requested");

            // Resources moved to the hand are added now. This makes possible for the user to swap depots (firstly, he decides to move the
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
                r = Resources.getResourceFromAbbr(uc.get_1());
            } catch (IllegalArgumentException e) { throw new BadRequestException("Resource requested in a wrong format"); }
                if(1 <= uc.get_3() && uc.get_3() <= 3){
                    wr.addDepot(uc.get_3(), r, 1);
                }
                else if(4 == uc.get_3() || uc.get_3() == 5){
                    LeaderCard LC;
                    LC = playerBoard.getLeaderCardsPlayed().get(uc.get_3()-4);
                    if(LCCapacity.get(LC)<LC.getAbility().getCapacity() && LC.getAbility().getResType().equals(r)){
                        LCCapacity.put(LC,LCCapacity.get(LC)+1);
                    } else throw new LeaderCardNotCompatibleException("Not compatible leader card");
                }
                else if(uc.get_3() != 0) throw new IllegalArgumentException("Invalid position requested");
        }

        //if ok, actually modify everything
        playerBoard.setWarehouse(wr);
        playerBoard.setHand(tmpHand);

        boolean updated = false;
        for (Integer I : playerBoard.getLeaderCardsPlayed().keySet()) {
            LeaderCard L = playerBoard.getLeaderCardsPlayed().get(I);
            if(LCCapacity.get(L)!=L.getAbility().getStashedResources()) {
                L.getAbility().doUpdateSlot(L.getAbility().getResType(), LCCapacity.get(L) - L.getAbility().getStashedResources());
                updated = true;
            }
        }
        if(updated) {
            notifyLCPlayed(playerBoard.getLeaderCardsPlayed(), playerBoard.getNickname());
        }


        notifyWR(playerBoard.getWarehouse(), playerBoard.getNickname());
        if(tmpHand.size() == 0) {
            playerBoard.setMoveNeeded(false);
            if(playerBoard.getLastActionMarket()){
                playerBoard.setLastActionMarket(false);
                notifyResourceBuyDone(playerBoard.getNickname());
            }
        }
        else if(playerBoard.getLastActionMarket()) {
            discardRemainingResources(player);
            return;
        }
        else if(tmpHand.size()>0) playerBoard.setMoveNeeded(true);
        notifyHand(playerBoard.getHand(), playerBoard.getNickname());
    }

    /**
     * Method used to discard leader cards
     * @param player position of the player who is performing the action
     * @param leaderCardIndex index of the card which the player wants to discard
     * @throws IndexOutOfBoundsException if the index isn't a valid one
     */
    public synchronized void discardLeaderCard(int player, int leaderCardIndex) throws IndexOutOfBoundsException{
        if(!players.get(player).getLeaderCardsHand().containsKey(leaderCardIndex - 1)) {throw new IndexOutOfBoundsException("Leader card does not exist"); }
        players.get(player).discardLeaderCard(leaderCardIndex);
        players.get(player).getFaithTrack().advanceTrack();
        int tmp = players.get(player).getFaithTrack().checkPopeFavor();
        if(tmp!=-1) popeEvent(tmp);
        notifyFT(players.get(player).getFaithTrack(), players.get(player).getNickname());
        notifyLCHand(players.get(player).getLeaderCardsHand(),players.get(player).getNickname());
    }

    /**
     * Method used to play leader cards
     * @param player position of the player who is performing the action
     * @param leaderCardIndex index of the card which the player wants to discard
     * @throws RequirementsNotMetException if the requirements for that card aren't fulfilled
     * @throws IndexOutOfBoundsException if the index isn't a valid one
     */
    public synchronized void playLeaderCard(int player, int leaderCardIndex) throws RequirementsNotMetException, IndexOutOfBoundsException{
        if(!players.get(player).getLeaderCardsHand().containsKey(leaderCardIndex-1)) throw new IndexOutOfBoundsException("Leader card does not exist");
        players.get(player).playLeaderCard(leaderCardIndex);

        notifyLCHand(players.get(player).getLeaderCardsHand(),players.get(player).getNickname());
        notifyLCPlayed(players.get(player).getLeaderCardsPlayed(),players.get(player).getNickname());
    }

    /**
     * Method used to check if at least one end game condition is met for a player
     * @return true if the condition is met, otherwise false
     */
    public boolean checkEndGame() {
        if (vaticanReport[2]) return true;
        else {
            for (Board b : players) {
                int count = 0;
                for (int i = 0; i < 3; i++) {
                    count += b.getSlot(i + 1).getDevelopmentCards().size();
                }
                if (count == 7) {
                    countVictoryPoints();
                    return true;
                }
            }
            return false;
        }
    }

    public void countVictoryPoints(){
        int tmp;
        for(int i = 0; i< playerNumber; i++){
            Board playerBoard = players.get(i);
            tmp=0;

            //faithTrack points
            int faithMarker = playerBoard.getFaithTrack().getFaithMarker();
            if(3<=faithMarker && faithMarker<=5) tmp+=1;
            else if(faithMarker<=8) tmp+=2;
            else if(faithMarker<=11) tmp+=4;
            else if(faithMarker<=14) tmp+=6;
            else if(faithMarker<=17) tmp+=9;
            else if(faithMarker<=20) tmp+=12;
            else if(faithMarker<=23) tmp+=16;
            else if(faithMarker==24) tmp+=20;

            for(int j=0;j<3;j++){
                if(playerBoard.getFaithTrack().getPopeFavor()[j]) tmp+=2+j;
            }

            //leader card points
            for (Integer I : playerBoard.getLeaderCardsPlayed().keySet()) {
                LeaderCard LC = playerBoard.getLeaderCardsPlayed().get(I);
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
            for (Integer I : playerBoard.getLeaderCardsPlayed().keySet()) {
                LeaderCard LC = playerBoard.getLeaderCardsPlayed().get(I);
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

    /**
     * Method used for single player games. Activates the action token for the round,
     * updating the model after the activation.
     */
    public void activatedToken(){
        ActionToken AT = actionStack.draw();
        notifyActionToken(AT);
        switch (AT) {
            case Advance2 -> {
                for (int i = 0; i < 2; i++) {
                    lorenzo.advanceBlackCross();
                    if (lorenzo.checkPopeFavor() != -1) popeEvent(lorenzo.checkPopeFavor());
                }
                notifyLorenzo(lorenzo,players.get(0).getFaithTrack().getPopeFavor());
            }
            case AdvanceAndRefresh -> {
                lorenzo.advanceBlackCross();
                if (lorenzo.checkPopeFavor() != -1) popeEvent(lorenzo.checkPopeFavor());
                actionStack = new ActionStack();
                notifyLorenzo(lorenzo,players.get(0).getFaithTrack().getPopeFavor());
            }
            case DeleteBlue -> {
                developmentCardMarket.deleteCards(Colours.Blue);
                notifyDCMarket(developmentCardMarket);
            }
            case DeleteGreen -> {
                developmentCardMarket.deleteCards(Colours.Green);
                notifyDCMarket(developmentCardMarket);
            }
            case DeletePurple -> {
                developmentCardMarket.deleteCards(Colours.Purple);
                notifyDCMarket(developmentCardMarket);
            }
            case DeleteYellow -> {
                developmentCardMarket.deleteCards(Colours.Yellow);
                notifyDCMarket(developmentCardMarket);
            }
        }
    }

    public boolean checkLorenzoWin(){
        if(lorenzo.getBlackCross() == 24) return true;
        return developmentCardMarket.lorenzoWin();
    }

}