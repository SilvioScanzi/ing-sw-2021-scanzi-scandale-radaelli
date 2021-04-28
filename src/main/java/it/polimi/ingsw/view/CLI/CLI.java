package it.polimi.ingsw.view.CLI;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.client.NetworkHandler;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.StandardMessages;
import it.polimi.ingsw.utils.LeaderCardParser;
import it.polimi.ingsw.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
//ASKPROF: va bene avere un attributo networkHandler come attributo o meglio fare altro?
//ASKPROF: salviamo una classe model (lato client) con LC e poco altro per fare controlli e mandare meno messaggi

public class CLI implements View, Runnable {
    private String message;
    private boolean messageReady = false;
    private boolean canInput = false;
    private Scanner scanner;
    private boolean yourTurn = false;
    private NetworkHandler networkHandler;

    public CLI(NetworkHandler NH) {
        scanner = new Scanner(System.in);
        this.networkHandler = NH;
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
            else if(!yourTurn){
                System.out.println(StandardMessages.waitALittleMore.toString());
            }
            else {
                message = tmpMessage;
                playerTurn();
            }
        }
    }

    public void playerTurn() {
        //TODO: actionDone da implementare nel caso delle prime 3 azioni
        if(!messageReady) {
            yourTurnPrint();
        }
        else messageReady = false;
        int userChoice = getChoice();
        while(userChoice < 0 || userChoice > 6){
            System.out.println("Devi scegliere un'azione valida, con indice tra 0 e 6!");
            message = scanner.nextLine();
            userChoice = getChoice();
        }

        switch (userChoice) {
            case 1: buyResources(); break;
            case 2: buyDevelopmentCard(); break;
            case 3: activateProduction(); break;
            //case 4: moveResources(); break;
            case 5: playLeaderCard(); break;
            case 6: discardLeaderCard(); break;
            case 0: networkHandler.buildEndTurnMessage(); break;
        }
    }

    public boolean getMessageReady() {
        return messageReady;
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

    public void setYourTurn(boolean yourTurn) {
        this.yourTurn = yourTurn;
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

    public void yourTurnPrint(){
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
        //TODO: server manda le cose necessarie: board e market
    }

    private int getChoice(){
        int n;
        try{
            n = Integer.parseInt(message);
        }catch(NumberFormatException e){return -1;}

        return n;
    }

    private void buyResources(){
        System.out.println("Hai scelto di comprare le risorse dal mercato");
        System.out.println("Seleziona una riga o una colonna (R per riga e C per colonna): ");
        String RC;
        boolean r = false;
        do {
            RC = scanner.nextLine();
            if (RC.equals("R")) {
                r = true;
            } else if (!RC.equals("C")) {
                System.out.println("Inserisci un valore valido");
            }
        }while(!RC.equals("R") && !RC.equals("C"));

        System.out.println("Indica il numero di " + ((RC.equals("R"))?"riga" : "colonna"));
        String num;
        boolean ok = false;
        int n = -1;
        do {
            num = scanner.nextLine();
            n = Integer.parseInt(num);
            if (r && (n>=1 && n<=3)) {
                ok=true;
            } else if (!r && (n>=1 && n<=4)) {
                ok=true;
            } else {
                System.out.println("Inserisci un valore valido");
            }
        }while(!ok);

        //conversion of white marbles
        ArrayList<Integer> requestedWMConversion = new ArrayList<>();
        System.out.println("Quali carte vuoi usare per le biglie bianche? (indice 1-2 della leader card)");
        String[] choice = scanner.nextLine().split(" ");
        if(choice.length!=0) {
            for (String s : choice) {
                message = s;
                requestedWMConversion.add(getChoice());
            }
        }

        networkHandler.buildBuyResources(r,n,requestedWMConversion);
    }

    private void buyDevelopmentCard() {
        System.out.println("Hai deciso di comprare una carta sviluppo. ");
        Colours colour = null;
        boolean ok = false;
        do {
            System.out.println("Quale carta vuoi prendere?");
            System.out.println("Scegli il colore: ");
            System.out.println("BL - Blu\nVE - Verde\nGI - Giallo\nVI - Viola ");
            String c = scanner.nextLine();
            if (c.equals("BL") || c.equals("VE") || c.equals("GI") || c.equals("VI")) {
                colour = Colours.getColourFromString(c);
                ok = true;
            }
        } while (!ok);

        int level;
        do {
            System.out.println("Scegli il livello tra 1, 2 e 3: ");
            message = scanner.nextLine();
            level = getChoice();
            if (level == 1 || level == 2 || level == 3) {
                ok = true;
            } else ok = false;
        } while (!ok);

        int slot;
        do {
            System.out.println("Scegli lo slot tra 1, 2 e 3: ");
            message = scanner.nextLine();
            slot = getChoice();
            if (slot == 1 || slot == 2 || slot == 3) {
                ok = true;
            } else ok = false;
        } while (!ok);

        System.out.println("Scegli che risorse vuoi usare e da dove le vuoi prendere.");
        System.out.println("Inserisci i valori a coppie (es: 2 SE)");
        System.out.println("1 - Deposito 1; 2 - Deposito 2; 3 - Deposito 3; 4 - Carta Leader 1; 5 - Carta Leader 2; 6 - Fine");
        System.out.println("SE - Servitori; MO - Monete; SC - Scudi; PI - Pietre");
        ArrayList<Pair<String, Integer>> userChoice = new ArrayList<>();
        int n;
        do {
            String[] s = scanner.nextLine().split(" ");
            message = s[0];
            n = getChoice();
            if(1<=n && n<=5) userChoice.add(new Pair<>(s[1], n));
            else if(n!=6) System.out.println("Devi inserire dati validi!");
        } while (n != 6);

        networkHandler.buildBuyDC(colour,level,slot,userChoice);
    }

    private void activateProduction() {
        System.out.println("Hai deciso di attivare la produzione. ");
        int index=-1;
        HashMap<Integer, ArrayList<Pair<String,Integer>>> userChoice = new HashMap<>();
        do {
            System.out.println("Seleziona la carta che desideri. Per chiudere la selezione digita 0");
            System.out.println("1, 2, 3 - Carte sviluppo negli slot");
            System.out.println("4 - Potere di base");
            System.out.println("5, 6 - Carte leader");  //needs update: show only for leader cards actually available
            message = scanner.nextLine();
            index = getChoice();
            boolean leap = false;
            if(1<=index && index<=6){
                if(userChoice.get(index) != null){
                    System.out.println("Hai già attivato questa produzione, vuoi sostituire la scelta effettuata in precedenza? [Y/N]");
                    String m;
                    do{
                        m = scanner.nextLine();
                    }while(!m.equals("Y") && !m.equals("N"));
                    if(m.equals("N")) leap = true;
                }
                if(!leap){
                    int n;
                    ArrayList<Pair<String, Integer>> resourceArray = new ArrayList<>();
                    System.out.println("Scegli che risorse vuoi usare e da dove le vuoi prendere.");
                    System.out.println("Inserisci i valori a coppie (es: 2 SE)");
                    System.out.println("1 - Deposito 1; 2 - Deposito 2; 3 - Deposito 3; 4 - Carta Leader 1; 5 - Carta Leader 2; 6 - Fine");
                    System.out.println("SE - Servitori; MO - Monete; SC - Scudi; PI - Pietre");
                    do {
                        String[] s = scanner.nextLine().split(" ");
                        message = s[0];
                        n = getChoice();
                        if(1<=n && n<=5) resourceArray.add(new Pair<>(s[1], n));
                        else if(n!=6) System.out.println("Devi inserire dati validi!");
                    }while(n != 6);
                    userChoice.put(index,resourceArray);
                }
            }
        }while(index!=0);
    }

    //needs update: show only for leader cards actually available
    private void playLeaderCard(){
        System.out.println("Hai deciso di attivare una carta leader");
        boolean ok = false;
        int userChoice = -1;
        System.out.println("Seleziona la carta leader che vuoi attivare");
        do {
            int c = Integer.parseInt(scanner.nextLine());
            if (c == 1 || c == 2) {
                userChoice = c;
                ok = true;
            } else {System.out.println("Devi inserire un indice valido!");}
        }while(!ok);

        networkHandler.buildActivateLC(userChoice);
    }

    private void discardLeaderCard(){
        System.out.println("Hai deciso di scartare una carta leader");
        boolean ok = false;
        int userChoice = -1;
        System.out.println("Seleziona la carta leader che vuoi scartare");
        do {
            int c = Integer.parseInt(scanner.nextLine());
            if (c == 1 || c == 2) {
                userChoice = c;
                ok = true;
            } else {System.out.println("Devi inserire un indice valido!");}
        }while(!ok);

        networkHandler.buildDiscardLC(userChoice);
    }

    /*e se invece unissimo le due funzioni cosi?
    private void LeaderCardAction(n){
        if (n==5){System.out.println("Hai deciso di attivare una carta leader");}
        else if (n==6){System.out.println("Hai deciso di scartare una carta leader");}
        boolean ok = false;
        int userChoice = -1;
        System.out.println("Seleziona la carta leader che vuoi scartare");
        do {
            int c = Integer.parseInt(scanner.nextLine());
            if (c == 1 || c == 2) {
                userChoice = c;
                ok = true;
            } else {System.out.println("Devi inserire un indice valido!");}
        }while(!ok);

        if (n==5){networkHandler.buildActivateLC(userChoice);}
        else if (n==6){networkHandler.buildDiscardLC(userChoice);}
    }*/
}
