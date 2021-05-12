package it.polimi.ingsw.network.client;

import it.polimi.ingsw.commons.*;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.observers.ViewObservable;
import it.polimi.ingsw.observers.ViewObserver;
import it.polimi.ingsw.view.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


public class NetworkHandler implements Runnable, ViewObserver {
    public enum NetworkState{connected,disconnected,end}
    private NetworkState state = NetworkState.disconnected;
    private Socket socket;
    private final View view;
    private ObjectOutputStream socketOut;
    private ObjectInputStream socketIn;
    private ClientModel clientModel;

    public NetworkHandler(boolean userInterface){
        try{
            socket = new Socket("127.0.0.1",9090);
            socketOut = new ObjectOutputStream(socket.getOutputStream());
            socketIn = new ObjectInputStream(socket.getInputStream());
            state = NetworkState.connected;
        }catch(Exception e){System.out.println("Connessione non disponibile"); e.printStackTrace();}

        //if user chooses to play with the GUI, boolean userInterface is true
        if(userInterface) {
            view = new GUI();
            new Thread((GUI) view).start();
        }
        else {
            view = new CLI(this);
            new Thread((CLI) view).start();
        }
    }

    @Override
    public void run() {
        while(!state.equals(NetworkState.disconnected)){
            try {
                Object message = socketIn.readObject();
                if(!(message instanceof Message) && !(message instanceof StandardMessages))
                    System.out.println("[NETWORK] Message not recognized (1)");
                else {
                    handleMessage(message);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMessage(Object message){
        //boolean redo;

        //Standard Messages
        if(message instanceof StandardMessages){
            switch((StandardMessages) message){
                case chooseNickName:
                    view.setState(CLI.ViewState.chooseNickName);
                    break;
                case choosePlayerNumber:
                    view.setState(CLI.ViewState.choosePlayerNumber);
                    break;
                case chooseDiscardedLC:
                    view.setState(CLI.ViewState.discardLeaderCard);
                    break;
                case chooseOneResource:
                    view.setState(CLI.ViewState.finishSetupOneResource);
                    break;
                case chooseTwoResource:
                    view.setState(CLI.ViewState.finishSetupTwoResources);
                    break;
                case yourTurn:
                    view.setState(CLI.ViewState.myTurn);
                    break;
            }

            view.printStandardMessage((StandardMessages) message);
        }
        /*if(message instanceof StandardMessages) {
            System.out.println(message.toString());
            if(message.equals(StandardMessages.choosePlayerNumber) || message.equals(StandardMessages.chooseNickName)
                || message.equals(StandardMessages.chooseOneResource) || message.equals(StandardMessages.chooseTwoResource)
                || message.equals(StandardMessages.nicknameAlreadyInUse) || message.equals(StandardMessages.chooseDiscardedLC)){
                        do {
                            view.setCanInput(true);
                            synchronized (view) {
                                while (!view.getMessageReady()) {
                                    try { view.wait(); } catch (InterruptedException e) { e.printStackTrace(); }
                                }
                            }
                            String inputMessage = view.getMessage();
                            redo = buildStandardMessage((StandardMessages) message, inputMessage);
                        }while(!redo);
            }
            else if(message.equals(StandardMessages.yourTurn)){
                view.setYourTurn(true);
                view.setCanInput(false);
                view.setMessageReady(false);
                view.yourTurnPrint();
            }
        }*/

        //Messages
        else if(message instanceof Message){
            //Model related Messages

            //Nicknames
            if(message instanceof NicknameMapMessage){
                clientModel = new ClientModel(((NicknameMapMessage) message).getPlayerMap(), ((NicknameMapMessage) message).getMyNickname());
                view.printNames(((NicknameMapMessage) message).getPlayerMap(), ((NicknameMapMessage) message).getInkwell());
            }

            //Solo play
            else if(message instanceof ActionTokenMessage){
                view.printAT(((ActionTokenMessage) message).getAT());
            }
            else if(message instanceof LorenzoTrackMessage){ ;
                view.printBlackCross(((LorenzoTrackMessage) message).getBlackCross());
            }

            //Public objects
            else if(message instanceof DCMarketMessage){
                clientModel.setCardMarket(((DCMarketMessage) message).getMarket());
                view.printCardMarket(((DCMarketMessage) message).getMarket());
            }
            else if(message instanceof ResourceMarketMessage){
                clientModel.generateWhiteMarbles(((ResourceMarketMessage) message).getGrid());
                view.printResourceMarket(((ResourceMarketMessage) message).getGrid(),((ResourceMarketMessage) message).getRemainingMarble());
            }

            //Board objects
            else if(message instanceof FaithTrackMessage){
                view.printFaithTrack(((FaithTrackMessage) message).getFaithMarker(),((FaithTrackMessage) message).getPopeFavor(),((FaithTrackMessage) message).getNickname());
            }
            else if(message instanceof LeaderCardHandMessage){
                clientModel.setLeaderCardsHand(((LeaderCardHandMessage) message).getLC());
                view.printLeaderCardHand(((LeaderCardHandMessage) message).getLC());
            }
            else if(message instanceof LeaderCardPlayedMessage){
                if(((LeaderCardPlayedMessage) message).getNickname().equals(clientModel.getMyNickname())) clientModel.setLeaderCardsPlayed(((LeaderCardPlayedMessage) message).getLC());
                view.printLeaderCardPlayed(((LeaderCardPlayedMessage) message).getLC(),((LeaderCardPlayedMessage) message).getNickname());
            }
            else if(message instanceof ResourceHandMessage){
                if(((ResourceHandMessage) message).getNickname().equals(clientModel.getMyNickname())) clientModel.setHand(((ResourceHandMessage) message).getHand());
                view.printResourceHand(((ResourceHandMessage) message).getHand(),((ResourceHandMessage) message).getNickname());
            }
            else if(message instanceof SlotMessage){
                if(((SlotMessage) message).getNickname().equals(clientModel.getMyNickname()))
                    clientModel.setSlot(((SlotMessage) message).getSlotIndex(),((SlotMessage) message).getColour(), ((SlotMessage) message).getVictoryPoints());
                view.printSlot(((SlotMessage) message).getSlotIndex(),((SlotMessage) message).getColour(), ((SlotMessage) message).getVictoryPoints(), ((SlotMessage) message).getNickname());
            }
            else if(message instanceof StrongboxMessage){
                if(((StrongboxMessage) message).getNickname().equals(clientModel.getMyNickname())) clientModel.setStrongBox(((StrongboxMessage) message).getStorage());
                view.printStrongBox(((StrongboxMessage) message).getStorage(),((StrongboxMessage) message).getNickname());
            }
            else if(message instanceof WarehouseMessage){
                if(((WarehouseMessage) message).getNickname().equals(clientModel.getMyNickname())) clientModel.setWarehouse(((WarehouseMessage) message).getWarehouse());
                view.printWarehouse(clientModel.getWarehouse(),((WarehouseMessage) message).getNickname());
            }

            //Not Model Related Messages
            else{System.out.println("[NETWORK] Message not recognized (2) " + message.toString());}
        }
    }

    /*private boolean buildStandardMessage(StandardMessages message, String inputMessage) {

        if (message.equals(StandardMessages.chooseNickName) || message.equals(StandardMessages.nicknameAlreadyInUse)) {
            sendObject(new NicknameMessage(inputMessage));

        } else if (message.equals(StandardMessages.choosePlayerNumber)) {
            int n = 0;
            try {
                n = Integer.parseInt(inputMessage);
            } catch (NumberFormatException e) {
                System.out.println(StandardMessages.wrongObject.toString());
                return false;
            }

            if (n < 1 || n > 4) {
                System.out.println("Numero di giocatori non supportato.");
                return false;
            } else {
                sendObject(new ChoosePlayerNumberMessage(n));
            }

        } else if (message.equals(StandardMessages.chooseOneResource)) {
            if (!inputMessage.equals("SC") && !inputMessage.equals("SE") && !inputMessage.equals("PI") && !inputMessage.equals("MO")) {
                System.out.println("Risorsa non supportata: inseriscine una valida");
                return false;
            }
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add(inputMessage);
            sendObject(new FinishSetupMessage(tmp));

        } else if (message.equals(StandardMessages.chooseTwoResource)) {
            ArrayList<String> tmp = new ArrayList<>();
            String[] s = inputMessage.split(" ");
            for (String a : s) {
                if (!a.equals("SC") && !a.equals("SE") && !a.equals("PI") && !a.equals("MO")) {
                    System.out.println("Risorsa non supportata: inseriscine una valida");
                    return false;
                }
                else tmp.add(a);
            }

            if (s.length != 2) {
                System.out.println("Numero errato di risorse: riscrivile");
                return false;
            }
            sendObject(new FinishSetupMessage(tmp));

        } else if (message.equals(StandardMessages.chooseDiscardedLC)) {
            int[] inputChoice = new int[2];
            try {
                String[] s = inputMessage.split(" ");
                if (s.length != 2) {
                    System.out.println("Devi inserire 2 Leader Card da scartare! ");
                    return false;
                }
                for (int i = 0; i < 2; i++) {
                    inputChoice[i] = Integer.parseInt(s[i]);
                    if (inputChoice[i] < 1 || inputChoice[i] > 4) {
                        System.out.println("Errore nella scelta, scegli indici compresi tra 1 e 4.");
                        return false;
                    }
                }
                if (inputChoice[0] == inputChoice[1]) {
                    System.out.println("Errore nella scelta, servono due indici diversi");
                    return false;
                }
            } catch (Exception e) {
                System.out.println("Errore nella scelta!");
                return false;
            }

            sendObject(new DiscardLeaderCardSetupMessage(inputChoice));
        }
        return true;
    }*/

    private boolean checkGotResources(ArrayList<Pair<String, Integer>> userChoice){
        HashMap<Integer, Pair<Resources,Integer>> warehouse = new HashMap<>(clientModel.getWarehouse());
        HashMap<Resources,Integer> strongbox = new HashMap<>(clientModel.getStrongBox());
        ArrayList<Triplet<Resources,Integer,Integer>> leaderCards = new ArrayList<>(clientModel.getLeaderCardsPlayed());
        for(Pair<String,Integer> P : userChoice){
            Resources R = Resources.getResourceFromString(P.getKey());
            if(P.getValue()>=1 && P.getValue()<=3) {
                try {
                    Pair<Resources, Integer> depot = warehouse.get(P.getValue());
                    if (depot.getKey().equals(R)) {
                        if (depot.getValue() > 0) {
                            depot.setValue(depot.getValue() - 1);
                        }
                        else return false;
                    }
                    else return false;
                } catch (NullPointerException e) {
                    return false;
                }
            }
            else if(P.getValue() == 4 || P.getValue() == 5){
                Triplet<Resources,Integer,Integer> LC = leaderCards.get(P.getValue()-4);
                if(R.equals(LC.get_1())){
                    if(LC.get_3()>0){
                        LC.set_3(LC.get_3()-1);
                    }
                    else return false;
                }
                else return false;
            }
            else if(P.getValue() == 6){
                try{
                    int n = strongbox.get(R);
                    if(n>0){
                        strongbox.replace(R, n-1);
                    }
                    else return false;
                }catch (NullPointerException e) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkRightResources(Colours colour, int level, ArrayList<Pair<String, Integer>> userChoice){
        DevelopmentCardParser DCP = new DevelopmentCardParser("");
        HashMap<Resources,Integer> uc = new HashMap<>();
        for(Resources R : Resources.values()){
            uc.put(R,0);
        }
        for(Pair<String,Integer> P : userChoice){
            uc.replace(Resources.getResourceFromString(P.getKey()),uc.get(Resources.getResourceFromString(P.getKey()))+1);
        }
        HashMap<Resources,Integer> cost = DCP.findCostByID(colour,level);
        return cost.equals(uc);
    }

    public void sendObject(Object o){
        try{
            socketOut.writeObject(o);
        }catch(IOException e){e.printStackTrace();}
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

    }

    @Override
    public void updateBuyResources(boolean r, int n, ArrayList<Integer> requestedWMConversion) {
        LeaderCardParser LCP = new LeaderCardParser("");
        if(clientModel.getLeaderCardsPlayed().size()==2) {
            if (LCP.findTypeByID(clientModel.getLeaderCardsPlayed().get(0).get_1(), clientModel.getLeaderCardsPlayed().get(0).get_2()).equals("WhiteMarbleAbility")
                    && LCP.findTypeByID(clientModel.getLeaderCardsPlayed().get(1).get_1(), clientModel.getLeaderCardsPlayed().get(1).get_2()).equals("WhiteMarbleAbility")) {
                if (requestedWMConversion.size() != clientModel.getWhiteMarbles(r, n)) {
                    view.print("Selezione errata nel numero di Leader Card");
                    return;
                }
            }
            else{
                view.print("La conversione avverrà in automatico");
                requestedWMConversion = new ArrayList<>();
            }
        }
        else {
            view.print("La conversione avverrà in automatico");
            requestedWMConversion = new ArrayList<>();
        }
        sendObject(new BuyResourcesMessage(r,n,requestedWMConversion));
    }

    @Override
    public void updateBuyDC(Colours colour, int level, int slot, ArrayList<Pair<String, Integer>> userChoice) {
        if(clientModel.getSlots(slot-1) == -1){
            if(level != 1) {
                view.print("In questo slot puoi posizionare solo una carta di livello 1");
                return;
            }
        }
        else if(clientModel.getSlots(slot-1) < 5){
            if(level != 2) {
                view.print("In questo slot puoi posizionare solo una carta di livello 2");
                return;
            }
        }
        else if(clientModel.getSlots(slot-1) < 9){
            if(level != 3) {
                view.print("In questo slot puoi posizionare solo una carta di livello 3");
                return;
            }
        }
        else {
            view.print("Non puoi posizionare ulteriori carte in questo slot");
            return;
        }


        if(!checkGotResources(userChoice) || !checkRightResources(colour,level,userChoice)){
            view.print("La scelta delle risorse non è corretta");
            return;
        }

        sendObject(new BuyDevelopmentCardMessage(colour,level,slot,userChoice));
    }

    @Override
    public void updateActivateProduction(HashMap<Integer, ArrayList<Pair<String, Integer>>> userChoice) {
    }

    @Override
    public void updateMoveResources(ArrayList<Triplet<String, Integer, Integer>> userChoice) {

    }

    @Override
    public void updateActivateLC(int userChoice) {

    }

    @Override
    public void updateDiscardLC(int userChoice) {

    }

    @Override
    public void updateEndTurn() {

    }
}
