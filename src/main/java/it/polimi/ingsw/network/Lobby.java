package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.Game;
import java.util.ArrayList;
import java.util.HashMap;

public class Lobby implements Runnable{
    private final Game game;
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

    }
}
