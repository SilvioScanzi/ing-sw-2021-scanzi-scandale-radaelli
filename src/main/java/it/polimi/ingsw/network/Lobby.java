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
    private HashMap<String, ClientHandler> clientNames = new HashMap<>();
    private ArrayList<ClientHandler> clients = new ArrayList<>();
    private ArrayList<String> playersName = new ArrayList<>();

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
        CH.setNickname(false);
        while (playersName.contains(CH.getNickname())) {
            CH.setNickname(true);
        }
        clientNames.put(CH.getNickname(), CH);
        playersName.add(CH.getNickname());
        clients.add(CH);
    }

    @Override
    public void run(){
        //starting client handlers for each player, using their sockets
        for(ClientHandler CH : clients){
            Thread thread = new Thread(CH);
            thread.start();
        }

        //DAFARE: messaggi di benvenuto
        inkwell = game.setup(playersName);

        for(int i=0;i<playerNumber;i++){
            if(i%inkwell > 0){
                //richiedi le risorse extra che vuole ricevere
                ArrayList<Resources> userChoice = new ArrayList<>();
                game.finishingSetup(i,userChoice);
            }
            //richiedi le leader card che vuole scartare
            int[] LCChoice = new int[2];
            game.discardSelectedLC(i,LCChoice);
        }

        if(playerNumber==1) playingSolo();
        else playingMultiplayer();
    }

    private void playingSolo(){

    }

    private void playingMultiplayer(){
        //Benvenuti...

        for(int i=game.getInkwell();!endGame;i=(i+1)%playerNumber){

        }
    }
}
