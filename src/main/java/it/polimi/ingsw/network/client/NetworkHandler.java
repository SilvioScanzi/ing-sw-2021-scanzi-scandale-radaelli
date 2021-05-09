package it.polimi.ingsw.network.client;

import it.polimi.ingsw.model.Colours;
import it.polimi.ingsw.model.Pair;
import it.polimi.ingsw.model.Resources;
import it.polimi.ingsw.model.Triplet;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.view.CLI.CLI;
import it.polimi.ingsw.view.View;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


/*TODO: spostare le system.out nella CLI (View interface) in modo da modificare solo quella classe
       una volta che ci sarà la GUI*/
public class NetworkHandler implements Runnable{
    private Socket socket;
    private final View view;
    private ObjectOutputStream socketOut;
    private ObjectInputStream socketIn;
    private Scanner in = new Scanner(System.in);

    public NetworkHandler(){
        try{
            socket = new Socket("127.0.0.1",9090);
            socketOut = new ObjectOutputStream(socket.getOutputStream());
            socketIn = new ObjectInputStream(socket.getInputStream());
        }catch(Exception e){System.out.println("Connessione non disponibile"); e.printStackTrace();}

        view = new CLI(this);
        new Thread((CLI) view).start();
    }

    @Override
    public void run() {
        while(true){
            try {
                Object message = socketIn.readObject();
                handleMessage(message);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMessage(Object message){
        boolean redo = false;
        if(message instanceof StandardMessages) {
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
        }
        else if(message instanceof Message){
            printMessage((Message) message);
        }
    }

    private boolean buildStandardMessage(StandardMessages message, String inputMessage) {
        view.setMessageReady(false);

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

            sendObject(new SetupLCDiscardMessage(inputChoice));
        }

        return true;
    }

    private void printMessage(Message message){
        if(message instanceof MarketMessage){
            view.printMarket(((MarketMessage) message).getGrid(),((MarketMessage) message).getRemainingMarble());
        }
        else if(message instanceof LeaderCardMessage)
            view.printLC(((LeaderCardMessage) message).getLC());
    }

    public void sendObject(Object o){
        try{
            socketOut.writeObject(o);
        }catch(IOException e){e.printStackTrace();}
    }

    public void buildBuyResources(boolean r,int n,ArrayList<Integer> requestedWMConversion){
        sendObject(new BuyResourcesMessage(r,n,requestedWMConversion));
    }

    public void buildEndTurnMessage(){
        sendObject(new TurnDoneMessage(true));
        view.setYourTurn(false);
    }

    public void buildBuyDC(Colours colour,int level,int slot,ArrayList<Pair<String,Integer>> userChoice){
        sendObject(new BuyDevelopmentCardMessage(colour,level,slot,userChoice));
    }

    public void buildMoveResources(ArrayList<Triplet<String, Integer, Integer>> userChoice){
        sendObject(new MoveResourcesMessage(userChoice));
    }

    public void buildActivateLC (int userChoice){
        sendObject(new PlayLeaderCardMessage(userChoice));
    }

    public void buildDiscardLC (int userChoice){
        sendObject(new DiscardLeaderCardMessage(userChoice));
    }

    public void buildActivateProduction(HashMap<Integer, ArrayList<Pair<String,Integer>>> userChoice){
        sendObject(new ProductionMessage(userChoice));
    }
}
