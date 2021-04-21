package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.messages.BuyResourcesMessage;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.messages.MoveActionMessage;
import it.polimi.ingsw.messages.StandardMessages;
import it.polimi.ingsw.model.Resources;

import java.util.ArrayList;
import java.util.HashMap;

public class Lobby implements Runnable {
    private final Game game;
    private boolean endGame = false;
    private final int playerNumber;
    private final HashMap<ClientHandler,String> clientNames = new HashMap<>();
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    private final ArrayList<String> playersName = new ArrayList<>();

    public Lobby(int playerNumber){
        this.playerNumber = playerNumber;
        game = new Game();
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public int getAddedPlayers(){
        return clients.size();
    }

    public void addPlayer(ClientHandler CH) {
        clients.add(CH);
    }

    @Override
    public void run() {

        for(ClientHandler CH : clients){
            CH.setNameQueue(clientNames);
        }

        for(ClientHandler CH : clients){
            CH.sendStandardMessage(StandardMessages.chooseNickName);
        }
        for(int i=0;i<playerNumber;i++){
            synchronized (clientNames) {
                if(clientNames.get(clients.get(i))==null) try{clientNames.wait();}catch(Exception e){e.printStackTrace();}
                playersName.add(clientNames.get(clients.get(i)));
            }
        }

        game.setup(playersName);
        for(int i=0;i<playerNumber;i++){
            //richiedi le leader card che vuole scartare
            //writeObject con le carte pescate; readObject con quelle scelte
            int[] LCDiscardChoice = new int[2];
            game.discardSelectedLC(i,LCDiscardChoice);
        }

        if (playerNumber == 1) playingSolo();
        else playingMultiplayer();
    }

    private void playingSolo(){

    }

    public void playingMultiplayer(){
        for(int i=0;i<playerNumber;i++){
            if(i%game.getInkwell() > 0){
                //richiedi le risorse extra che vuole ricevere
                ArrayList<Resources> userChoice = new ArrayList<>();
                game.finishingSetup(i,userChoice);
            }
        }

        for(int i= game.getInkwell();!endGame;i=(i+1)%playerNumber) {
            boolean actionDone = false;
            boolean turnDone = false;
            while (!turnDone) {
                //WriteObject con un'unica stringa
                /*System.out.println("Giocatore Attivo: " + game.getPlayers(i));
                System.out.println("Scegli l'azione che vuoi compiere: ");
                System.out.println("1 - Compra Risorse dal mercato");
                System.out.println("2 - Compra una Carta sviluppo");
                System.out.println("3 - Attiva la produzione delle tue carte");
                System.out.println("Oppure scegli un'azione bonus: ");
                System.out.println("4 - Sposta le risorse dal magazzino");
                System.out.println("5 - Gioca una carta leader");
                System.out.println("6 - Scarta una carta leader");
                System.out.println("0 - Fine turno");*/
                //String choice = scanner.nextLine();
                synchronized (clients.get(i)) {
                    while (!clients.get(i).getMessageReady()) {
                        try {
                            clients.get(i).wait();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Message message = clients.get(i).getMessage();
                    handleMessage(message, i);
                }
            }
        }
    }

    private void handleMessage(Message message,int player){
        //TODO: try catch and send/handle errors
        if(message instanceof BuyResourcesMessage) {
            game.getMarketResources(player, ((BuyResourcesMessage) message).getRow(),
                    ((BuyResourcesMessage) message).getN(), ((BuyResourcesMessage) message).getRequestedWMConversion());

            clients.get(player).setMessageReady(false);
            clients.get(player).notifyAll();

            //after getting the resources, the user needs to say where he wants to deposit them
            boolean moveActionCorrect = false;
            do {
                while (!clients.get(player).getMessageReady()) {
                    try {
                        clients.get(player).wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Message userChoice = clients.get(player).getMessage();
                clients.get(player).setMessageReady(false);

                if (userChoice instanceof MoveActionMessage) {
                    game.moveAction(player, ((MoveActionMessage) userChoice).getUserChoice());
                    game.discardRemainingResources(player); //can't hold resources after getting them from the market
                    moveActionCorrect = true;
                } else {
                    clients.get(player).sendStandardMessage(StandardMessages.wrongObject);
                }
            }while(!moveActionCorrect);
        }

        clients.get(player).notifyAll();
    }
}
