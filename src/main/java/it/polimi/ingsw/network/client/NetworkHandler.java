package it.polimi.ingsw.network.client;

import it.polimi.ingsw.commons.*;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.observers.ViewObservable;
import it.polimi.ingsw.observers.ViewObserver;
import it.polimi.ingsw.view.*;
import it.polimi.ingsw.view.clientModel.ClientModel;
import it.polimi.ingsw.view.clientModel.ClientBoard;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


public class NetworkHandler implements Runnable, ViewObserver {

    private boolean demolished = false;
    private Socket socket;
    private final View view;
    private ObjectOutputStream socketOut;
    private ObjectInputStream socketIn;
    private ClientModel clientModel;
    private String IP = null;
    private int port = 0;

    public NetworkHandler(View view){
        this.view = view;
    }

    private void closeConnection(){
        try {
            socketOut.close();
            socketIn.close();
            socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }

        view.setState(ViewState.disconnected);
    }

    @Override
    public void run() {
        while(!demolished){
            try {
                Object message = socketIn.readObject();
                if(!(message instanceof Message) && !(message instanceof StandardMessages))
                    System.out.println("[NETWORK] Message not recognized (1)");
                else {
                    handleMessage(message);
                }
            } catch (IOException | ClassNotFoundException e) {
                demolished = true;
                closeConnection();
            }
        }
    }

    private void handleMessage(Object message){
        //Standard Messages
        if(message instanceof StandardMessages){
            switch ((StandardMessages) message) {
                case wait -> view.setState(ViewState.wait);
                case chooseNickName -> view.setState(ViewState.chooseNickName);
                case choosePlayerNumber -> view.setState(ViewState.choosePlayerNumber);
                case gameIsStarting -> view.setState(ViewState.lobbyNotReady);
                case gameNotCreated -> view.setState(ViewState.gameNotCreated);
                case chooseDiscardedLC -> view.setState(ViewState.discardLeaderCard);
                case chooseOneResource -> view.setState(ViewState.finishSetupOneResource);
                case chooseTwoResource -> view.setState(ViewState.finishSetupTwoResources);
                case yourTurn -> {
                    clientModel.getBoard(clientModel.getMyNickname()).setActionDone(false);
                    view.setState(ViewState.myTurn);
                }
                case actionDone -> clientModel.getBoard(clientModel.getMyNickname()).setActionDone(true);
                case notYourTurn -> { synchronized (view) {view.setState(ViewState.notMyTurn);}}
                case fatalError, endGame -> {
                    view.setState(ViewState.disconnected);
                    closeConnection();
                }
                case reconnection -> {
                    view.setState(ViewState.reconnecting);
                }
                case lorenzoWin -> clientModel.setLorenzo(true);
                case moveActionNeeded -> {
                    view.printResourceHand(clientModel.getBoard(clientModel.getMyNickname()).getHand(),clientModel.getMyNickname());
                    view.printWarehouse(clientModel.getBoard(clientModel.getMyNickname()).getWarehouse(),clientModel.getMyNickname());
                }
            }

            view.printStandardMessage((StandardMessages) message);
        }

        //Messages
        else if(message instanceof Message){
            //Model related Messages

            //Nicknames
            if(message instanceof NicknameMapMessage){
                clientModel = new ClientModel(((NicknameMapMessage) message).getPlayerMap(), ((NicknameMapMessage) message).getMyNickname(), ((NicknameMapMessage) message).getInkwell());
                view.printNames(((NicknameMapMessage) message).getPlayerMap(), ((NicknameMapMessage) message).getInkwell());
            }

            else if(message instanceof LCMapMessage){
                clientModel.setLCMap(((LCMapMessage) message).getLCMap());
            }

            //Solo play
            else if(message instanceof ActionTokenMessage){
                view.printAT(((ActionTokenMessage) message).getAT());
            }
            else if(message instanceof LorenzoTrackMessage){
                clientModel.getBoard(clientModel.getMyNickname()).setLorenzoMarker(((LorenzoTrackMessage) message).getBlackCross());
                clientModel.getBoard(clientModel.getMyNickname()).setPopeFavor(((LorenzoTrackMessage) message).getPopeFavor());
                view.printBlackCross(((LorenzoTrackMessage) message).getBlackCross());
            }

            //Public objects
            else if(message instanceof DCMarketMessage){
                clientModel.setCardMarket(((DCMarketMessage) message).getMarket());
                view.printCardMarket(((DCMarketMessage) message).getMarket());
            }
            else if(message instanceof ResourceMarketMessage){
                clientModel.setResourceMarket(((ResourceMarketMessage) message).getGrid(),((ResourceMarketMessage) message).getRemainingMarble());
                view.printResourceMarket(clientModel.getResourceMarket(),clientModel.getRemainingMarble());
            }

            //Board objects
            else if(message instanceof MarketHandMessage){
                clientModel.getBoard(((MarketHandMessage) message).getNickname()).setHand(((MarketHandMessage) message).getHand());
                view.printResourceHand(((MarketHandMessage) message).getHand(),((MarketHandMessage) message).getNickname());
                clientModel.setResourceMarket(((MarketHandMessage) message).getGrid(),((MarketHandMessage) message).getRemainingMarble());
                view.printResourceMarket(clientModel.getResourceMarket(),clientModel.getRemainingMarble());
            }
            else if(message instanceof FaithTrackMessage){
                clientModel.getBoard(((FaithTrackMessage) message).getNickname()).setFaithMarker(((FaithTrackMessage) message).getFaithMarker());
                clientModel.getBoard(((FaithTrackMessage) message).getNickname()).setPopeFavor(((FaithTrackMessage) message).getPopeFavor());
                view.printFaithTrack(((FaithTrackMessage) message).getFaithMarker(),((FaithTrackMessage) message).getPopeFavor(),((FaithTrackMessage) message).getNickname());
            }
            else if(message instanceof LeaderCardHandMessage){
                clientModel.getBoard(clientModel.getMyNickname()).setLeaderCardsHand(((LeaderCardHandMessage) message).getLC());
                view.printLeaderCardHand(((LeaderCardHandMessage) message).getLC());
            }
            else if(message instanceof LeaderCardPlayedMessage){
                clientModel.getBoard(((LeaderCardPlayedMessage) message).getNickname()).setLeaderCardsPlayed(((LeaderCardPlayedMessage) message).getLC());
                view.printLeaderCardPlayed(((LeaderCardPlayedMessage) message).getLC(),((LeaderCardPlayedMessage) message).getNickname());
            }
            else if(message instanceof ResourceHandMessage){
                clientModel.getBoard(((ResourceHandMessage) message).getNickname()).setHand(((ResourceHandMessage) message).getHand());
                view.printResourceHand(((ResourceHandMessage) message).getHand(),((ResourceHandMessage) message).getNickname());
            }
            else if(message instanceof SlotMessage){
                clientModel.getBoard(((SlotMessage) message).getNickname()).setSlot(((SlotMessage) message).getSlots());
                view.printSlot(((SlotMessage) message).getSlots(), ((SlotMessage) message).getNickname());
            }
            else if(message instanceof StrongboxMessage){
                clientModel.getBoard(((StrongboxMessage) message).getNickname()).setStrongBox(((StrongboxMessage) message).getStorage());
                view.printStrongBox(((StrongboxMessage) message).getStorage(),((StrongboxMessage) message).getNickname());
            }
            else if(message instanceof WarehouseMessage){
                clientModel.getBoard(((WarehouseMessage) message).getNickname()).setWarehouse(((WarehouseMessage) message).getWarehouse());
                view.printWarehouse(clientModel.getBoard(((WarehouseMessage) message).getNickname()).getWarehouse(),((WarehouseMessage) message).getNickname());
            }

            else if(message instanceof LeaderCardHandUpdateMessage){
                ArrayList<Triplet<Resources,Integer,Integer>> cards = new ArrayList<>();
                for(Triplet<Resources,Integer,Integer> T : clientModel.getBoard(((LeaderCardHandUpdateMessage) message).getNickname()).getLeaderCardsHand()){
                    cards.add(new Triplet<>(Resources.Servants,-1,-1));
                }
                if(cards.size()>0) cards.remove(0);
                clientModel.getBoard(((LeaderCardHandUpdateMessage) message).getNickname()).setLeaderCardsHand(cards);
            }

            else if(message instanceof VictoryPointsMessage){
                clientModel.setLeaderBoard(((VictoryPointsMessage) message).getVp());
                view.setState(ViewState.endGame);
                view.print("La partita è finita");
                int n = clientModel.getPlayerNumber();
                HashMap <String,Integer> vp = clientModel.getLeaderBoard();

                for (int i = 0; i < n; i++) {
                    String nickname = "";

                    int max = -1;
                    for(String s : vp.keySet()){
                        if(vp.get(s)>max){
                            nickname = s;
                            max = vp.get(s);
                        }
                    }

                    view.print(i + ") " + nickname+" - Punti vittoria: "+vp.get(nickname));
                    vp.remove(nickname);
                }
            }

            //Not Model Related Messages
            else if(message instanceof DisconnectedMessage){
                if(((DisconnectedMessage) message).getNickname().equals(clientModel.getMyNickname())) view.setState(ViewState.disconnected);
                else{
                    view.printDisconnected(((DisconnectedMessage) message).getNickname());
                }
            }
            else if(message instanceof ReconnectMessage){
                view.printReconnect(((ReconnectMessage) message).getNickname());
            }
            else{System.out.println("[NETWORK] Message not recognized (2) " + message);}
        }
    }

    public ClientModel getClientModel() {
        return clientModel;
    }

    public void sendObject(Object o){
        try{
            socketOut.reset();
            socketOut.writeObject(o);
        }catch(IOException e){e.printStackTrace();}
    }

    @Override
    public void updateAddress(String IP,int port){
        try {
            socket = new Socket(IP,port);
            socketOut = new ObjectOutputStream(socket.getOutputStream());
            socketIn = new ObjectInputStream(socket.getInputStream());
            new Thread(this).start();
            this.IP = IP;
            this.port = port;
        }catch(IOException e){view.printStandardMessage(StandardMessages.unavailableConnection);}
    }

    @Override
    public void updateNickname(ViewObservable obs, String message){
        sendObject(new NicknameMessage(message));
    }

    @Override
    public void updatePlayerNumber(ViewObservable obs, int num){
        sendObject(new ChoosePlayerNumberMessage(num));
    }

    @Override
    public void updateSetupDiscardLC(ViewObservable obs, int[] index){
        sendObject(new DiscardLeaderCardSetupMessage(index));
    }

    @Override
    public void updateFinishSetup(ViewObservable obs, ArrayList<String> message){
        sendObject(new FinishSetupMessage(message));
    }

    @Override
    public void updateDisconnected(ViewObservable obs){
        sendObject(new DisconnectedMessage(clientModel.getMyNickname()));
    }

    @Override
    public void updateBuyResources(boolean r, int n, ArrayList<Integer> requestedWMConversion) {
        if(clientModel.getBoard(clientModel.getMyNickname()).getActionDone()){
            view.print("Hai già eseguito un'azione per questo turno");
            return;
        }
        LeaderCardParser LCP = new LeaderCardParser();
        if(clientModel.getBoard(clientModel.getMyNickname()).getLeaderCardsPlayed().size()==2) {
            if (LCP.findTypeByID(clientModel.getBoard(clientModel.getMyNickname()).getLeaderCardsPlayed().get(0).get_1(), clientModel.getBoard(clientModel.getMyNickname()).getLeaderCardsPlayed().get(0).get_2()).equals("WhiteMarbleAbility")
                    && LCP.findTypeByID(clientModel.getBoard(clientModel.getMyNickname()).getLeaderCardsPlayed().get(1).get_1(), clientModel.getBoard(clientModel.getMyNickname()).getLeaderCardsPlayed().get(1).get_2()).equals("WhiteMarbleAbility")) {
                if (requestedWMConversion.size() != clientModel.getWhiteMarbles(r, n)) {
                    view.print("Selezione errata nel numero di Leader Card");
                    return;
                }
            }
            else{
                requestedWMConversion = new ArrayList<>();
            }
        }
        else {
            requestedWMConversion = new ArrayList<>();
        }
        sendObject(new BuyResourcesMessage(r,n,requestedWMConversion));
    }

    @Override
    public void updateBuyDC(Colours colour, int level, int slot, ArrayList<Pair<String, Integer>> userChoice) {
        if(clientModel.getBoard(clientModel.getMyNickname()).getActionDone()){
            view.print("Hai già eseguito un'azione per questo turno");
            return;
        }
        sendObject(new BuyDevelopmentCardMessage(colour,level,slot,userChoice));
    }

    @Override
    public void updateActivateProduction(HashMap<Integer, ArrayList<Pair<String, Integer>>> userChoice) {
        if(clientModel.getBoard(clientModel.getMyNickname()).getActionDone()){
            view.print("Hai già eseguito un'azione per questo turno");
            return;
        }
        sendObject(new ProductionMessage(userChoice));
    }

    @Override
    public void updateMoveResources(ArrayList<Triplet<String, Integer, Integer>> userChoice) {
        sendObject(new MoveResourcesMessage(userChoice));
    }

    @Override
    public void updateActivateLC(int userChoice) {
        sendObject(new PlayLeaderCardMessage(userChoice));
    }

    @Override
    public void updateDiscardLC(int userChoice) {
        sendObject(new DiscardLeaderCardMessage(userChoice));
    }

    @Override
    public void updateEndTurn() {
        if(clientModel.getBoard(clientModel.getMyNickname()).getActionDone()){
            sendObject(new TurnDoneMessage(true));
        }
        else {
            view.print("Non hai ancora svolto l'azione per questo turno");
            view.print("@@@");
        }
    }

    @Override
    public void updatePrintRequest(String message) {
        if(message.equals("Plancia comune")){
            view.printResourceMarket(clientModel.getResourceMarket(),clientModel.getRemainingMarble());
            view.printCardMarket(clientModel.getCardMarket());
            view.print("@@@");
        }
        else if(message.startsWith("G")){
            ClientBoard cb = null;
            int pos = Integer.parseInt(message.split(" ")[1]);
            for(String S : clientModel.getBoards().keySet()){
                if(clientModel.getBoard(S).getPosition()==pos){
                    cb = clientModel.getBoard(S);
                }
            }
            if(cb==null) view.print("Il giocatore selezionato non esiste");
            else view.printBoard(cb);
        }
        else{
            view.printLeaderCardHand(clientModel.getBoard(clientModel.getMyNickname()).getLeaderCardsHand());
        }
    }

    @Override
    public void updateReconnection(boolean r){
        if(r) sendObject(new ReconnectMessage("Y"));
        else sendObject(new ReconnectMessage("N"));
    }

    @Override
    public void updateAnotherGame(){
        try {
            demolished = false;
            socket = new Socket(IP,port);
            socketOut = new ObjectOutputStream(socket.getOutputStream());
            socketIn = new ObjectInputStream(socket.getInputStream());
            new Thread(this).start();
            view.clearView();
        }catch(IOException e){view.demolish();}
    }

    @Override
    public void updateDemolish(){
        view.demolish();
    }
}
