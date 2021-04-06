package it.polimi.ingsw.controller;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;
import java.util.*;

public class GameHandlerMultiple {
    private Game game;
    private Scanner scanner = new Scanner(System.in);

    public void init(){
        game = new Game();
        System.out.println("Benvenuti al gioco 'Maestri del rinascimento' ");
        System.out.println("Inserisci i nomi dei giocatori: ");
        System.out.println("Scrivi DONE per terminare l'inserimento");
        String tmp = "";
        ArrayList<String> s = new ArrayList<>();
        for(int i=0;i<4 && !tmp.equals("DONE");i++){
            tmp=(scanner.nextLine());
            s.add(tmp);
        }
        game.setup(s);
        playing();
        countVictoryPoints();
    }

    private void playing(){
        boolean endGame=false;
        for(int i=game.getInkwell();!endGame;i=(i+1)%4){
            int c=0;
            while(c==0) {
                System.out.println("Giocatore Attivo: " + game.getPlayers(i));
                System.out.println("Scegli l'azione che vuoi compiere: ");
                System.out.println("1 - Compra Risorse dal mercato");
                System.out.println("2 - Compra una Carta sviluppo");
                System.out.println("3 - Attiva la produzione delle tue carte");
                System.out.println("Oppure scegli un'azione bonus: ");
                System.out.println("4 - Sposta le risorse dal magazzino");
                System.out.println("5 - Gioca una carta leader");
                System.out.println("6 - Scarta una carta leader");
                String choice = scanner.nextLine();
                c = Integer.parseInt(choice);
                switch (c) {
                    case 1:
                        ;
                    case 2:
                        ;
                    case 3:
                        ;
                    case 4:{
                        c=0;
                    };
                    case 5:{
                        c=0;
                    };
                    case 6:{
                        c=0;
                    };
                }
            }
            endGame=game.checkEndGame(i);
        }
    }

    private void marketAction(){

    }

    private void buyDevelopmentAction(){

    }

    private void productionAction(){

    }

    private void moveAction(){

    }

    private void playLeaderCardAction(){

    }

    private void discardLeaderCardAction(){

    }

    private void countVictoryPoints(){

    }

}
