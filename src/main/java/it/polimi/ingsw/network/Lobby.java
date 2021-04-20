package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.messages.StandardMessages;
import it.polimi.ingsw.model.Resources;

import java.util.ArrayList;
import java.util.HashMap;

public class Lobby implements Runnable {
    private final Game game;
    private boolean endGame = false;
    private int inkwell;
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

        inkwell = game.setup(playersName);
        for(int i=0;i<playerNumber;i++){
            //richiedi le leader card che vuole scartare
            int[] LCChoice = new int[2];
            game.discardSelectedLC(i,LCChoice);
        }

        if (playerNumber == 1) playingSolo();
        else playingMultiplayer();
    }

    private void playingSolo(){

    }

    public void playingMultiplayer(){
        for(int i=0;i<playerNumber;i++){
            if(i%inkwell > 0){
                //richiedi le risorse extra che vuole ricevere
                ArrayList<Resources> userChoice = new ArrayList<>();
                game.finishingSetup(i,userChoice);
            }
        }

        if(playerNumber==1) playingSolo();
        else playingMultiplayer();
    }
}
