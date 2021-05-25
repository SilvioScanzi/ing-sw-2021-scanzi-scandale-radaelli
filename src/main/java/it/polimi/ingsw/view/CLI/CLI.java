package it.polimi.ingsw.view.CLI;
import it.polimi.ingsw.commons.*;

import it.polimi.ingsw.network.client.NetworkHandler;
import it.polimi.ingsw.network.messages.StandardMessages;
import it.polimi.ingsw.observers.ViewObservable;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.ViewState;
import it.polimi.ingsw.view.clientModel.ClientBoard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CLI extends ViewObservable implements View {
    private ViewState state = ViewState.start;
    private final Scanner scanner;

    private final boolean color;

    public static final String	BACKGROUND_BLACK	= "\u001B[40m";
    public static final String	BACKGROUND_RED		= "\u001B[41m";
    public static final String	BACKGROUND_YELLOW	= "\u001B[43m";
    public static final String	BACKGROUND_BLUE		= "\u001B[44m";
    public static final String	BACKGROUND_MAGENTA	= "\u001B[45m";
    public static final String	BACKGROUND_WHITE	= "\u001B[47m";
    public static final String  RESET               = "\u001B[0m";

    public CLI(boolean color) {
        this.color = color;
        NetworkHandler NH = new NetworkHandler(this);
        addObserver(NH);
        scanner = new Scanner(System.in);
    }

    public void start(){
        System.out.println("Inserisci Indirizzo IP e Porta del server a cui vuoi connetterti");
        while(!state.equals(ViewState.disconnected)){
            if(state.equals(ViewState.myTurn)) printYourTurn();
            String message = scanner.nextLine();
                if(state.equals(ViewState.start)){
                    try{
                        String[] s = message.split(" ");
                        int port = Integer.parseInt(s[1]);
                        notifyAddress(s[0],port);
                    }catch(NumberFormatException e){
                        System.out.println("Devi inserire una porta valida");
                    }
                }
                else if(state.equals(ViewState.chooseNickName)){
                    notifyNickname(message);
                }
                else if(state.equals(ViewState.choosePlayerNumber)) {
                    int i;
                    try {
                        i = Integer.parseInt(message);
                        if(i<1 || i>4){
                            System.out.println("Numero di giocatori non supportato");
                        }
                        else notifyPlayerNumber(i);
                    }catch(NumberFormatException e) { System.out.println("Devi inserire un numero valido"); }
                }
                else if(state.equals(ViewState.discardLeaderCard)){
                    int[] i = new int[2];
                    String[] s = message.split(" ");
                    if(s.length!=2){
                        System.out.println("Devi inserire due indici");
                    }
                    else{
                        try {
                            boolean flag = true;
                            for (int j = 0; j < 2; j++) {
                                i[j] = Integer.parseInt(s[j]);
                                if (i[j] < 1 || i[j] > 4) {
                                    System.out.println("Devi scegliere un indice tra 1 e 4");
                                    flag = false;
                                }
                            }
                            if(flag) {
                                notifySetupDiscardLC(i);
                            }
                        }catch(NumberFormatException e) {
                            System.out.println("Devi inserire un numero valido");
                        }
                    }
                }
                else if(state.equals(ViewState.finishSetupOneResource)){
                    if(!message.equals("SE") && !message.equals("MO") && !message.equals("SC") && !message.equals("PI"))
                        System.out.println("Scegli delle risorse esistenti");
                    else {
                        ArrayList<String> tmp = new ArrayList<>();
                        tmp.add(message);
                        notifyFinishSetup(tmp);
                    }
                }
                else if(state.equals(ViewState.finishSetupTwoResources)){
                    String[] s = message.split(" ");
                    if(s.length!=2) System.out.println("Devi scegliere due risorse");
                    else {
                        boolean flag = true;
                        for(int i=0;i<2;i++) {
                            if (!s[i].equals("SE") && !s[i].equals("MO") && !s[i].equals("SC") && !s[i].equals("PI")) {
                                System.out.println("Scegli delle risorse esistenti");
                                flag = false;
                            }
                        }
                        if(flag) {
                            ArrayList<String> tmp = new ArrayList<>();
                            tmp.add(s[0]);
                            tmp.add(s[1]);
                            notifyFinishSetup(tmp);
                        }
                    }
                }
                else if(state.equals(ViewState.myTurn)) {
                    if(message.equals("Plancia comune")||message.equals("G 1")||message.equals("G 2")||message.equals("G 3")||message.equals("G 4")){
                        notifyPrintRequest(message);
                    }
                    else{
                        try{
                            int userChoice = Integer.parseInt(message);
                            switch (userChoice) {
                                case 1: buyResources(); break;
                                case 2: buyDevelopmentCard(); break;
                                case 3: activateProduction(); break;
                                case 4: moveResources(); break;
                                case 5: LeaderCardAction(5); break;
                                case 6: LeaderCardAction(6); break;
                                case 0: notifyEndTurn(); break;
                            }
                        }catch(NumberFormatException e) {
                            System.out.println("Devi scegliere un'azione valida, con indice tra 0 e 6!");
                        }
                    }
                }
                else if(state.equals(ViewState.notMyTurn)){
                    if(message.equals("Plancia comune")||message.equals("G 1")||message.equals("G 2")||message.equals("G 3")||message.equals("G 4")){
                        notifyPrintRequest(message);
                    }
                    System.out.println("Non è il tuo turno");
                }
                else if(state.equals(ViewState.disconnected)){
                    System.out.println("Sei disconnesso dal server, riavvia l'applicazione per ritornare a giocare");
                    return;
                }
            else {
                System.out.println("Non puoi inviare dati in questo momento");
            }
        }
        System.out.println("L'applicazione sta terminando");
    }

    public void setState(ViewState state) {
        if(state.equals(ViewState.disconnected) && !this.state.equals(ViewState.disconnected))
            print("Digita una stringa qualsiasi per terminare l'applicazione.");

        this.state = state;
    }

    @Override
    public void printDisconnected(String name) {
        System.out.println("[NETWORK] Il giocatore "+name+" si è disconnesso");
    }

    private void buyResources(){
        System.out.println("Hai scelto di comprare le risorse dal mercato.");
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
        int n;
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
        System.out.println("Quali carte vuoi usare per le biglie bianche? Indice 1-2 della leader card; -1 se non vuoi usare nulla");
        String[] choice;
        boolean flag;
        do{
            flag = false;
            requestedWMConversion.clear();
            choice = scanner.nextLine().split(" ");
            if(choice.length<4) {
                for (String s : choice) {
                    try{
                        int userChoice = Integer.parseInt(s);
                        if(userChoice==1 || userChoice==2){
                            requestedWMConversion.add(userChoice);
                        }
                        else if(userChoice==-1){
                            break;
                        }
                        else {
                            flag=true;
                            System.out.println("Le leader card hanno come indici solo 1 e 2");
                        }
                    }catch(NumberFormatException e){
                        flag = true;
                        System.out.println("Inserisci un numero valido");
                    }
                }
            }
            else {
                System.out.println("Ci sono al massimo 4 biglie bianche!");
                flag = true;
            }
        }while(flag);

        notifyBuyResources(r,n,requestedWMConversion);
    }

    private void buyDevelopmentCard() {
        System.out.println("Hai deciso di comprare una carta sviluppo.");
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

        int level=0;
        do {
            System.out.println("Scegli il livello tra 1, 2 e 3: ");
            try{
                level = Integer.parseInt(scanner.nextLine());
                ok = level >= 1 && level <= 3;
            }catch(NumberFormatException e){ok=false;}
        } while (!ok);

        int slot=0;
        do {
            System.out.println("Scegli lo slot tra 1, 2 e 3: ");
            try{
                slot = Integer.parseInt(scanner.nextLine());
                ok = slot >= 1 && slot <= 3;
            }catch(NumberFormatException e){ok=false;}
        } while (!ok);

        System.out.println("Scegli che risorse vuoi usare e da dove le vuoi prendere.");
        System.out.println("Inserisci i valori a coppie (es: 2 SE)");
        System.out.println("1 - Deposito 1; 2 - Deposito 2; 3 - Deposito 3; 4 - Carta Leader 1; 5 - Carta Leader 2; 6 - Forziere; 7 - Fine");
        System.out.println("SE - Servitori; MO - Monete; SC - Scudi; PI - Pietre");
        ArrayList<Pair<String, Integer>> userChoice = new ArrayList<>();
        int n=0;
        getUserChoice(n, userChoice);

        notifyBuyDC(colour,level,slot,userChoice);
    }

    private void activateProduction() {
        System.out.println("Hai deciso di attivare la produzione. ");
        int index;
        //HashMap: production index, ArrayList: Resource and where they come from
        HashMap<Integer, ArrayList<Pair<String,Integer>>> userChoice = new HashMap<>();
        do {
            System.out.println("Seleziona la carta che desideri. Per chiudere la selezione digita 0");
            System.out.println("1, 2, 3 - Carte sviluppo negli slot");
            System.out.println("4, 5 - Carte leader");
            System.out.println("6 - Potere di base");
            index = Integer.parseInt(scanner.nextLine());
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
                    int n=0;
                    ArrayList<Pair<String, Integer>> resourceArray = new ArrayList<>();
                    System.out.println("Scegli che risorse vuoi usare e da dove le vuoi prendere.");
                    System.out.println("Inserisci i valori a coppie (es: 2 SE)");
                    System.out.println("Posti: 1 - Deposito 1; 2 - Deposito 2; 3 - Deposito 3; 4 - Carta Leader 1; 5 - Carta Leader 2; 6 - Forziere; 7 - Fine");
                    System.out.println("Risorse: SE - Servitori; MO - Monete; SC - Scudi; PI - Pietre");
                    getUserChoice(n, resourceArray);
                    userChoice.put(index,resourceArray);
                    if(index >= 4){
                        String s;
                        do{
                            System.out.println("Scegli la risorsa che vuoi ottenere dalla produzione\nRisorse: SE - Servitori; MO - Monete; SC - Scudi; PI - Pietre");
                            s = scanner.nextLine();
                        }while(!s.equals("SE") && !s.equals("MO") && !s.equals("SC") && !s.equals("PI"));
                        resourceArray.add(new Pair<>(s,-1));
                        userChoice.replace(index,resourceArray);
                    }
                }
            }
        }while(index!=0);

        notifyActivateProduction(userChoice);
    }

    private void getUserChoice(int n, ArrayList<Pair<String, Integer>> resourceArray) {
        do {
            String[] s = scanner.nextLine().split(" ");
            if(s.length==1){
                try{
                    n = Integer.parseInt(s[0]);
                    if(n!=7) System.out.println("Selezione errata");
                }catch(NumberFormatException e){
                    System.out.println("Devi inserire un numero");
                }
            }
            else if(s.length==2){
                try{
                    n = Integer.parseInt(s[0]);
                    if(!s[1].equals("SE") && !s[1].equals("MO") && !s[1].equals("SC") && !s[1].equals("PI"))
                        System.out.println("Scegli delle risorse esistenti");
                    else if(1<=n && n<=6) resourceArray.add(new Pair<>(s[1], n));
                }catch(NumberFormatException e){
                    System.out.println("Devi inserire un numero");
                }
            }
            else System.out.println("Selezione errata");
        }while(n != 7);
    }

    private void moveResources(){
        System.out.println("Hai deciso di spostare le risorse. ");
        System.out.println("Scegli la risorsa, il luogo da dove prendela e il luogo dove metterla.");
        System.out.println("La mano serve solo a fare scambi, quindi non lasciarci risorse!");
        System.out.println("Inserisci i valori a triplette (es: SE 2 3); per finire digita solo il numero richiesto.");
        System.out.println("Posti: 1 - Deposito 1; 2 - Deposito 2; 3 - Deposito 3; 4 - Carta Leader 1; 5 - Carta Leader 2; 0 - Mano; 7 - Fine");
        System.out.println("Risorse: SE - Servitori; MO - Monete; SC - Scudi; PI - Pietre");
        ArrayList<Triplet<String,Integer,Integer>> userChoice = new ArrayList<>();
        String[] s;
        do{
           s = scanner.nextLine().split(" ");
           if(s.length==1){
               try{
                   if(Integer.parseInt(s[0]) != 7) System.out.println("Inserisci dati validi");
               }catch(NumberFormatException e){
                   System.out.println("Inserisci dati validi");
               }
           }
           else if(s.length==3) {
               if (!s[0].equals("SE") && !s[0].equals("MO") && !s[0].equals("SC") && !s[0].equals("PI"))
                   System.out.println("Risorsa inestitente. Inserisci di nuovo la tripletta: ");
               else{
                   int[] tmp = new int[2];
                   try{
                       tmp[0] = Integer.parseInt(s[1]);
                       tmp[1] = Integer.parseInt(s[2]);
                       if(tmp[0]<0 || tmp[0]>6 || tmp[1]<0 || tmp[1]>6){
                           System.out.println("Posto inesistente. Inserisci di nuovo la tripletta:  ");
                       }
                       else userChoice.add(new Triplet<>(s[0],tmp[0],tmp[1]));
                   }catch(NumberFormatException e){
                       System.out.println("Inserisci dati validi");
                   }
               }
           }
           else System.out.println("Inserisci dati validi");

        }while(!s[0].equals("7"));

        notifyMoveResources(userChoice);
    }

    private void LeaderCardAction(int n){
        if (n==5){System.out.println("Hai deciso di attivare una carta leader.");}
        else if (n==6){System.out.println("Hai deciso di scartare una carta leader.");}
        boolean ok = false;
        int userChoice = 0;
        System.out.println("Seleziona la carta leader che vuoi scartare");
        do {
            int c = Integer.parseInt(scanner.nextLine());
            if (c == 1 || c == 2) {
                userChoice = c;
                ok = true;
            } else {System.out.println("Devi inserire un indice valido!");}
        }while(!ok);

        if (n==5){notifyActivateLC(userChoice);}
        else if (n==6){notifyDiscardLC(userChoice);}
    }

    @Override
    public void print(String string){
        System.out.println(string);
    }

    @Override
    public void printBoard(ClientBoard board) {
        String nickname = board.getNickname();
        printLeaderCardPlayed(board.getLeaderCardsPlayed(),nickname);
        for(int i=1;i<=3;i++){
            printSlot(i,board.getSlots(1).getKey(),board.getSlots(1).getValue(),nickname);
        }
        printFaithTrack(board.getFaithMarker(), board.getPopeFavor(), nickname);
        printStrongBox(board.getStrongBox(),nickname);
        printWarehouse(board.getWarehouse(),nickname);
    }

    @Override
    public void printStandardMessage(StandardMessages message){
        System.out.println(message.toString());
        if(message.equals(StandardMessages.yourTurn)) printYourTurn();
    }

    public void printYourTurn(){
        System.out.println("Scegli l'azione che vuoi compiere: ");
        System.out.println("1 - Compra Risorse dal mercato");
        System.out.println("2 - Compra una Carta sviluppo");
        System.out.println("3 - Attiva la produzione delle tue carte");
        System.out.println("Oppure scegli un'azione bonus: ");
        System.out.println("4 - Sposta le risorse dal magazzino");
        System.out.println("5 - Gioca una carta leader");
        System.out.println("6 - Scarta una carta leader");
        System.out.println("Plancia comune - Mostrare la plancia della partita");
        System.out.println("G n - Mostrare la plancia del giocatore n");
        System.out.println("0 - Fine turno");
    }

    @Override
    public void printNames(HashMap<String, Integer> names, int inkwell) {
        int i = inkwell;
        int j = 1;
        do{
            for(String S : names.keySet()){
                if(names.get(S) == i) System.out.println("Giocatore "+j+" - "+S);
            }
            i=(i+1)%(names.size());
            j++;
        }while(i!=inkwell);

        System.out.println("\n");
    }

    @Override
    public void printResourceMarket(Marbles[][] grid, Marbles remainingMarble) {
        StringBuilder tmp = new StringBuilder("MERCATO DELLE RISORSE:\n");
        tmp.append("  1  2  3  4 \n");
        if(color) {
            tmp.append(RESET + "╔══╦══╦══╦══╗\n");
        }
        else{
            tmp.append("╔══╦══╦══╦══╗\n");
        }
        for(int i=0;i<3;i++) {
            tmp.append("║");
            for (int j = 0; j < 4; j++) {
                if(color) {
                    if (grid[i][j].equals(Marbles.Yellow)) {
                        tmp.append(BACKGROUND_YELLOW);
                    } else if (grid[i][j].equals(Marbles.Blue)) {
                        tmp.append(BACKGROUND_BLUE);
                    } else if (grid[i][j].equals(Marbles.Red)) {
                        tmp.append(BACKGROUND_RED);
                    } else if (grid[i][j].equals(Marbles.Purple)) {
                        tmp.append(BACKGROUND_MAGENTA);
                    } else if (grid[i][j].equals(Marbles.Grey)) {
                        tmp.append(BACKGROUND_WHITE);
                    } else if (grid[i][j].equals(Marbles.White)) {
                        tmp.append(BACKGROUND_BLACK);
                    }
                    tmp.append(grid[i][j].abbreviation()).append(RESET).append("║");
                }
                else{
                    tmp.append(grid[i][j].abbreviation()).append("║");
                }
            }

            tmp.append(" ").append(i + 1);
            if (i != 2) tmp.append("\n╠══╬══╬══╬══╣\n");
            else tmp.append("\n╚══╩══╩══╩══╝\n");
        }
        tmp = new StringBuilder(tmp.toString().concat("La biglia rimanente è: " + remainingMarble.toString()));
        System.out.println(tmp);

        System.out.println("\n");
    }

    @Override
    public void printLeaderCardHand(ArrayList<Triplet<Resources, Integer, Integer>> LC) {
        LeaderCardParser LCP = new LeaderCardParser();
        System.out.println("CARTE LEADER");
        int i = 1;
        for(Triplet<Resources,Integer,Integer> t : LC){
            System.out.println(i + ")\n" + LCP.findCardByID(t.get_1(),t.get_2(),t.get_3()));
            i++;
        }

        System.out.println("\n");
    }

    @Override
    public void printLeaderCardPlayed(ArrayList<Triplet<Resources, Integer, Integer>> LC, String nickname) {
        if(LC.isEmpty()) System.out.println(nickname+" NON HA CARTE LEADER IN GIOCO");
        LeaderCardParser LCP = new LeaderCardParser();
        System.out.println("CARTE LEADER DI "+nickname+" IN GIOCO");
        int i = 1;
        for(Triplet<Resources,Integer,Integer> t : LC){
            System.out.println(i + ") " + LCP.findCardByID(t.get_1(),t.get_2(),t.get_3()));
            i++;
        }
    }

    @Override
    public void printResourceHand(ArrayList<Resources> H, String nickname) {
        if(H.size()==0){
            System.out.println("LA MANO DELLE RISORSE DI " + nickname +" È VUOTA");
        }
        else {
            System.out.println("MANO DELLE RISORSE DI " + nickname);
            HashMap<Resources, Integer> hand = new HashMap<>();
            for (Resources r : Resources.values()) {
                hand.put(r, H.stream().filter(x -> x.equals(r)).collect(Collectors.toList()).size());
                if (hand.get(r) <= 0) hand.remove(r);
            }
            for (Resources r : hand.keySet()) {
                System.out.println("Risorsa: " + r + ", quantità: " + hand.get(r));
            }

            System.out.println("\n");
        }
    }

    @Override
    public void printAT(ActionToken AT) {
        System.out.println("AZIONE DI LORENZO IL MAGNIFICO: " + AT.toString());

        System.out.println("\n");
    }

    @Override
    public void printBlackCross(int BC) {
        System.out.print("POSIZIONE DELLA CROCE NERA DI LORENZO IL MAGNIFICO: "+ BC);

        System.out.println("\n");
    }

    @Override
    public void printCardMarket(HashMap<Pair<Colours, Integer>, Integer> CM) {
        System.out.println("MERCATO DELLE CARTE");
        DevelopmentCardParser DCP = new DevelopmentCardParser();
        System.out.println(DCP.findMarketByID(CM,color));
    }

    @Override
    public void printFaithTrack(int FM, boolean[] PF, String nickname) {
        System.out.println("FAITH TRACK DI " + nickname);
        System.out.println("Posizione dell'indicatore fede: " + FM);
        System.out.print("Tessere di favore papale attivate: ");
        for(int i = 0; i<PF.length; i++){
            if(PF[i]) System.out.println((i+1) + ", ");
        }

        System.out.println("\n\n");
    }

    @Override
    public void printSlot(int I, Colours C, int VP, String nickname) {
        if(VP==-1){
            System.out.println("LO SLOT " + I + " DELLA PLANCIA DI " + nickname+" È VUOTO");
        }
        else {
            System.out.println("SLOT " + I + " DELLA PLANCIA DI " + nickname);
            DevelopmentCardParser DCP = new DevelopmentCardParser();
            System.out.println(DCP.findCardByID(C, VP, color));
        }
    }

    @Override
    public void printStrongBox(HashMap<Resources, Integer> SB, String nickname) {
        if(SB.isEmpty()) System.out.println("IL FORZIERE DI " + nickname +" È VUOTO");
        System.out.println("STRONGBOX DI " + nickname);
        for(Resources r : SB.keySet()){
            System.out.println(r.toString() + ": " + SB.get(r));
        }

        System.out.println("\n");
    }

    @Override
    public void printWarehouse(HashMap<Integer, Pair<Resources, Integer>> WH, String nickname) {
        System.out.println("WAREHOUSE DI " + nickname);
        for(Integer i : WH.keySet()){
            System.out.println("Deposito " + i + ": " + "risorsa: " + WH.get(i).getKey().toString() + ", quantità: " + WH.get(i).getValue());
        }

        System.out.println("\n");
    }
}
