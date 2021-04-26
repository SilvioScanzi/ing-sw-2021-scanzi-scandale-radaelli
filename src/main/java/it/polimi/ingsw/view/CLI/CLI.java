package it.polimi.ingsw.view.CLI;
import it.polimi.ingsw.model.Marbles;
import it.polimi.ingsw.model.Resources;
import it.polimi.ingsw.model.Triplet;
import it.polimi.ingsw.network.messages.StandardMessages;
import it.polimi.ingsw.utils.LeaderCardParser;
import it.polimi.ingsw.view.View;

import java.util.ArrayList;
import java.util.Scanner;
/*TODO: network handler sempre in ascolto, CLI scrive usando il metodo sendObject, usiamo una sorta di buffer per stampare a schermo
*  quando finisce l'azione (se non sono errori). IDEA MACCHINA A STATI*/
public class CLI implements View, Runnable {
    private String message;
    private boolean messageReady = false;
    private boolean canInput = false;
    private Scanner scanner;

    public CLI() {
        scanner = new Scanner(System.in);
    }

    @Override
    public void run(){
        while(true){
            String tmpMessage = scanner.nextLine();
            if(canInput && !messageReady){
                synchronized (this){
                    message = tmpMessage;
                    messageReady = true;
                    canInput = false;
                    this.notifyAll();
                }
            }
            else {
                System.out.println(StandardMessages.waitALittleMore.toString());
            }
        }
    }

    public void yourTurn(){
        System.out.println("È arrivato il tuo turno! ");
        System.out.println("Scegli l'azione che vuoi compiere: ");
        System.out.println("1 - Compra Risorse dal mercato");
        System.out.println("2 - Compra una Carta sviluppo");
        System.out.println("3 - Attiva la produzione delle tue carte");
        System.out.println("Oppure scegli un'azione bonus: ");
        System.out.println("4 - Sposta le risorse dal magazzino");
        System.out.println("5 - Gioca una carta leader");
        System.out.println("6 - Scarta una carta leader");
        System.out.println("0 - Fine turno");

        synchronized (this){
            int userChoice = -1;
            do{
                canInput = true;
                while(!messageReady){
                    try{
                        this.wait();
                    }catch(InterruptedException e){e.printStackTrace();}
                }
                userChoice = getChoice();
                messageReady = false;
                canInput = false;
            }while(userChoice<0 || userChoice>6);
        }



    }

    private int getChoice(){
        int n;
        try{
            n = Integer.parseInt(message);
        }catch(NumberFormatException e){return -1;}

        return n;
    }

    public boolean getMessageReady() {
        return messageReady;
    }

    public boolean getCanInput() {
        return canInput;
    }

    public String getMessage() {
        return message;
    }

    public void setMessageReady(boolean messageReady) {
        this.messageReady = messageReady;
    }

    public void setCanInput(boolean canInput) {
        this.canInput = canInput;
    }

    public void printMarket(Marbles[][] grid, Marbles remainingMarble){
        String tmp = "Mercato delle risorse:\n";
        for(int i=0;i<3;i++){
            for(int j=0;j<4;j++){
                tmp=tmp.concat(grid[i][j].abbreviation() + " ");
            }
            tmp=tmp.concat("\n");
        }
        tmp=tmp.concat("La biglia rimanente è: "+remainingMarble.toString());
        System.out.println(tmp);
    }

    public void printLC(ArrayList<Triplet<Resources,Integer,Integer>> leaderCards){
        LeaderCardParser LCP = new LeaderCardParser("");
        for(Triplet<Resources,Integer,Integer> t : leaderCards){
            System.out.println(LCP.findCardByID(t.get_1(),t.get_2(),t.get_3()));
        }
    }
}
