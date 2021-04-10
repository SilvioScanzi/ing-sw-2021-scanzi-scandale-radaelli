package it.polimi.ingsw.view;
import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;

import java.lang.reflect.Array;
import java.util.*;

public class GameHandler{
    private Game game;
    private Scanner scanner = new Scanner(System.in);

    //tecnicamente da fare in rete, tramite gestione delle lobby
    public void init(){
        game = new Game();
        System.out.println("Benvenuti nel gioco 'Maestri del rinascimento' ");
        System.out.println("Inserisci i nomi dei giocatori: ");
        System.out.println("Scrivi DONE per terminare l'inserimento");
        String tmp = "";
        ArrayList<String> s = new ArrayList<>();
        for(int i=0;i<4 && !tmp.equals("DONE");i++){
            tmp=(scanner.nextLine());
            s.add(tmp);
        }
        s.remove("DONE");
        game.setup(s);

        //stampa le leader card di ogni giocatore
        int[] discardedLC = new int[2];
        for(int i=0;i<s.size();i++){
            for(int k=0;k<2;k++) {
                System.out.println("Queste sono le carte leader del giocatore: "+game.getPlayers(i)+"\n");
                for(int j=0;j<4-k;j++){
                    System.out.println((j+1) +" - "+game.getBoard(i).getLeadercards().get(j));
                }
                int index;
                System.out.println("Scegli una carte da scartare, tra 1 e " + (4-k));
                do {
                    index=Integer.parseInt(scanner.nextLine());
                } while (index<0 || index>(4-k));
                discardedLC[k] = index;
            }
            game.discardSelectedLC(i,discardedLC);
        }

        if(s.size()==1){
            playingSolo();
        }
        else {
            playing(s.size());
        }
    }

    //TODO: need to check actionDone in controller, in view is not totally accountable
    private void playing(int players){
        boolean endGame=false;
        int person = 0;
        for(int i=game.getInkwell();!endGame;i=(i+1)%players){
            person = i; //saves the last player; used when finishin the game
            boolean actionDone = false;
            boolean turnDone = false;
            while(!turnDone) {
                System.out.println("Giocatore Attivo: " + game.getPlayers(i));
                System.out.println("Scegli l'azione che vuoi compiere: ");
                System.out.println("1 - Compra Risorse dal mercato");
                System.out.println("2 - Compra una Carta sviluppo");
                System.out.println("3 - Attiva la produzione delle tue carte");
                System.out.println("Oppure scegli un'azione bonus: ");
                System.out.println("4 - Sposta le risorse dal magazzino");
                System.out.println("5 - Gioca una carta leader");
                System.out.println("6 - Scarta una carta leader");
                System.out.println("0 - Fine turno");
                String choice = scanner.nextLine();
                int c = Integer.parseInt(choice);
                if (1 <= c && c <= 3 && !actionDone) {
                    try {
                        actionDone = choiceMenu(c, i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if(1 <= c && c <= 3 && actionDone){
                    System.out.println("Hai già effettuato un'azione per questo turno!");
                }
                else if(4<=c && c<=6){
                    try{
                        choiceMenu(c,i);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
                else if(c==0 && !actionDone){
                    System.out.println("Devi prima fare un'azione!");
                }
                else if(c==0){
                    turnDone=true;
                }
                else{
                    System.out.println("Inserisci una scelta valida!");
                }
            }
            endGame=game.checkEndGame(i);
        }
        //TODO: ritorna tutti i victory points e vede chi ha vinto. Prima va però finito il giro dei turni
        //person...
    }

    //TODO: check next time
    private void playingSolo(){
        while(!game.checkLorenzoWin() && !game.checkEndGame(0)) {
            boolean actionDone = false;
            boolean turnDone = false;
            while(!turnDone) {
                System.out.println("E' il tuo turno");
                System.out.println("Scegli l'azione che vuoi compiere: ");
                System.out.println("1 - Compra Risorse dal mercato");
                System.out.println("2 - Compra una Carta sviluppo");
                System.out.println("3 - Attiva la produzione delle tue carte");
                System.out.println("Oppure scegli un'azione bonus: ");
                System.out.println("4 - Sposta le risorse dal magazzino");
                System.out.println("5 - Gioca una carta leader");
                System.out.println("6 - Scarta una carta leader");
                System.out.println("0 - Fine turno");
                String choice = scanner.nextLine();
                int c = Integer.parseInt(choice);
                if (1 <= c && c <= 3 && !actionDone) {
                    try {
                        actionDone = choiceMenu(c, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if(1 <= c && c <= 3 && actionDone){
                    System.out.println("Hai già effettuato un'azione per questo turno!");
                }
                else if (4 <= c && c <= 6) {
                    try {
                        choiceMenu(c, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if(c==0 && !actionDone){
                    System.out.println("Devi prima fare un'azione!");
                }
                else if(c==0){
                    turnDone=true;
                }
                else{
                    System.out.println("Inserisci una scelta valida!");
                }
            }
            System.out.println("E' il turno di Lorenzo il Magnifico.");
            ActionToken AT = game.activatedToken();
            System.out.println(AT.toString());
        }
    }

    private boolean choiceMenu(int c, int player) {
        switch (c) {
            case 1:  marketAction(player); break;  //sarebbe un messaggio da mandare al controller
            case 2:  return buyDevelopmentAction(player);
            case 3:  productionAction(player); break;
            case 4:  moveAction(player); return false;
            case 5:  playLeaderCardAction(player); return false;
            case 6:  discardLeaderCardAction(player); return false;
        }
        return true;
    }

    //da finire (commentini alla fine)
    private void marketAction(int player) {
        System.out.println("Hai scelto di comprare le risorse dal mercato");
        System.out.println(game.getMarket().toString());        //tecnicamente nun se po fa
        System.out.println("Riga o colonna? R per riga e C per colonna");
        String RC;
        boolean r=false;
        do {
            RC = scanner.nextLine();
            if (RC.equals("R")) {
                r = true;
            } else if (RC.equals("C")) {
                r = false;
            } else {
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

        //hypothetically already having a client-side (mini) model with market, board and leader cards
        ArrayList<Integer> requestedWMConversion = new ArrayList<>();
        Board playerBoard = game.getBoard(player);
        if(playerBoard.getLeadercardsplayed().stream().filter(LC -> LC.getAbility().doConvert()).count() > 0){
            for(int i=0;i<game.getMarket().getWhiteMarbles(r,n);i++){
                boolean okConversion = false;
                do {
                    System.out.println("Quale leader vuoi utilizzare per convertire la biglia bianca?");
                    int l = Integer.parseInt(scanner.nextLine());
                    if(1==l || l==2){
                        requestedWMConversion.add(l);
                        okConversion = true;
                    }
                    else System.out.println("Inserisci un indice valido (1 o 2)");
                    /*try {
                        game.leaderCardConversion(player, game.getBoard(player).getLeadercardsplayed().get(l - 1));
                        requestedWMConversion.add(l);
                        okConversion = true;
                    } catch (Exception E) {
                        System.out.println("La carta scelta non è del tipo corretto");
                    }*/
                }while(!okConversion);
            }
        }

        game.getMarketResources(player,r,n,requestedWMConversion);
        moveAction(player); //TODO
        game.discardRemainingResources(player); //fattibile magari all'interno di un metodo moveAction nel game
    }

    //view checking ok
    private boolean buyDevelopmentAction(int player) {
        while(true){
            System.out.println(game.getDevelopmentcardmarket().toString());
            System.out.println(game.getBoard(player).slottoString());
            System.out.println("Se non puoi comprare nessuna carta o hai sbagliato a scegliere, digita N");
            String s = scanner.nextLine();
            if(s.equals("N")) return false;

            Colours colour = Colours.Purple;
            boolean ok;
            do {
                System.out.println("Quale carta vuoi prendere?");
                System.out.println("Scegli il colore: ");
                System.out.println("BL - Blu\nVE - Verde\nGI - Giallo\nVI - Viola ");
                String c = scanner.nextLine();
                ok = true;
                switch (c) {
                    case "BL":
                        colour = Colours.Blue; break;
                    case "VE":
                        colour = Colours.Green; break;
                    case "GI":
                        colour = Colours.Yellow; break;
                    case "VI":
                        colour = Colours.Purple; break;
                    default: {
                        System.out.println("Colore non valido");
                        ok = false;
                        break;
                    }
                }
            } while (!ok);

            int level = 0;
            do {
                System.out.println("Scegli il livello tra 1, 2 e 3: ");
                level = Integer.parseInt(scanner.nextLine());
                if (level == 1 || level == 2 || level == 3) {
                    ok = true;
                } else ok = false;
            } while (!ok);

            int slot = 0;
            do {
                System.out.println("Scegli lo slot tra 1, 2 e 3: ");
                slot = Integer.parseInt(scanner.nextLine());
                if (slot == 1 || slot == 2 || slot == 3) {
                    ok = true;
                } else ok = false;
            } while (!ok);

            try {
                game.getDevelopmentCard(colour, level, player, slot);
                System.out.println(game.getBoard(player).slottoString());   //tecnicamente nun se po fa
                return true;
            } catch (InvalidPlacementException e){ System.out.println("Hai abbastanza risorse, ma non puoi mettere la carta su questo slot!");}
            catch (Exception e) { System.out.println("Non hai abbastanza risorse per comprare la carta!"); }
        }
    }

    private void productionAction(int player){
        Board playerBoard = game.getBoard(player);
        System.out.println("Hai scelto di attivare un potere di produzione.");
        System.out.println("Carte disponibili negli slot: ");
        System.out.println(playerBoard.slottoString());   //prints available slots
        System.out.println("\nLeader card con potere di produzione: "); //printing only the ones with correct production power
        for(LeaderCard LC : playerBoard.getLeadercardsplayed()){
            if(LC.getAbility().doActivate())
                System.out.println(LC);
        }

        boolean[] pProductions = new boolean[] {false,false,false,false,false,false};
        int tmp = -1;
        while(tmp!=0){
            System.out.println("\nSeleziona la carta che desideri; per chiudere la selezione digita 0");
            System.out.println("1, 2, 3 - Carte sviluppo negli slot");
            System.out.println("4 - Potere di base");
            System.out.println("5, 6 - Carte leader");  //needs update: show only for leader cards actually available
            tmp = Integer.parseInt(scanner.nextLine());
            if(tmp>0 && tmp<6){
                if(pProductions[tmp-1]) {
                    System.out.println("Devi selezionare un potere di produzione valido.");
                }
                else{
                    if(1<=tmp && tmp<=3){
                        //check if card exists is done in Board
                        try{
                            game.activateDevelopmentCardProduction(player,tmp);
                            pProductions[tmp-1] = true;
                        }catch(Exception e){ e.printStackTrace(); }
                    }
                    else if(tmp==4){
                        System.out.println("Hai selezionato il potere di produzione di base.\nEcco il magazzino e il forziere: ");
                        System.out.println(playerBoard.getWarehouse().toString());
                        System.out.println(playerBoard.getStrongbox().toString());
                        System.out.println("Che risorse vuoi usare? (Due risorse)");
                        ArrayList<Resources> usedResources = new ArrayList<>();
                        Resources gotResources = null;  //PROF: sappiamo che il valore lo trova, però a 339 fa errori di compilazione se non
                        //lo inizializzo. Brutta pratica?

                        //asking for required resources
                        while(usedResources.size()<2){
                            System.out.println("MO - Monete");
                            System.out.println("PI - Pietre");
                            System.out.println("SC - Scudi");
                            System.out.println("SE - Servitori");
                            try{
                                usedResources.add(Resources.getResourceFromString(scanner.nextLine())); //PROF: meglio un metodo così static o solito switch case?
                            }catch(IllegalArgumentException e){
                                System.out.println("Inserisci una risorsa valida");
                            }
                        }

                        //asking for produced resources
                        boolean flag = true;
                        while(flag){
                            System.out.println("Che risorse vuoi ricevere? (Una risorsa)");
                            System.out.println("MO - Monete");
                            System.out.println("PI - Pietre");
                            System.out.println("SC - Scudi");
                            System.out.println("SE - Servitori");
                            try{
                                gotResources = (Resources.getResourceFromString(scanner.nextLine()));
                                flag = false;
                            }catch (IllegalArgumentException e) {
                                System.out.println("Inserisci una risorsa valida");
                            }
                        }

                        try{
                            game.activateBaseProduction(player, usedResources, gotResources);
                            pProductions[tmp-1] = true;
                        }catch (IllegalArgumentException e) {
                            System.out.println("Hai già attivato il potere di produzione");
                        }
                    }
                    else {
                        //already checks in Board if leader cards have the right ability
                        boolean flag = true;
                        Resources gotResource = null;
                        while(flag){
                            System.out.println("Che risorse vuoi ricevere? (Una risorsa)");
                            System.out.println("MO - Monete");
                            System.out.println("PI - Pietre");
                            System.out.println("SC - Scudi");
                            System.out.println("SE - Servitori");
                            try{
                                gotResource = (Resources.getResourceFromString(scanner.nextLine()));
                                flag = false;
                            }catch (IllegalArgumentException e) {
                                System.out.println("Inserisci una risorsa valida");
                            }
                        }
                        try{
                            game.activateLeaderCardProduction(player,tmp-4,gotResource);
                            pProductions[tmp-1] = true;
                        }catch(IllegalArgumentException e){
                            System.out.println("Non è andato a buon fine, rifai.");
                        }
                    }
                }
            }
            else if(tmp==0) ;
            else System.out.println("Selezione non valida.");
        }

        playerBoard.dumpHandIntoStrongbox();
    }

    private void moveAction(int player){
        Board playerBoard = game.getBoard(player);
        boolean done = false;
        while(!done) {
            System.out.println(playerBoard.getWarehouse().toString());
            System.out.println((playerBoard.getHand().size()>0)?"La tua mano di risorse da distribuire nel deposito è: \n"+playerBoard.handtoString()+"\nCome vuoi distribuirle?\n1 - Deposito 1\n2 - Deposito 2\n3 - Deposito 3\n4 - Carta Leader 1\n5 - Carta Leader 2":"Non hai nessuna risorsa in mano, vuoi ridistribuire qualcosa?");
            System.out.println("6 - Ridistribuisci le risorse già presenti in un deposito");
            System.out.println("7 - Reset di tutte le risorse");
            System.out.println("0 - Finisci la distribuzione");
            int choice = Integer.parseInt(scanner.nextLine());
            if(1<= choice && choice <=3) {
                try {
                    game.setResourcesToDepot(player,playerBoard.getHand().get(0),choice);
                    playerBoard.getHand().remove(0);
                }catch(Exception e) {System.out.println("Non va bene");}
            }
            else if(choice == 4 || choice == 5) {
                try {
                    game.setResourcesToLeaderCard(player,playerBoard.getHand().get(0),1,playerBoard.getLeadercardsplayed().get(choice-4));
                    playerBoard.getHand().remove(0);
                }catch(Exception e) {System.out.println("Non va bene");}
            }
            else if(choice == 6){
                System.out.println("Che deposito vuoi svuotare?");
                int depot = Integer.parseInt(scanner.nextLine());
                playerBoard.clearDepot(depot);
            }
            else if(choice == 7){
                playerBoard.clearWarehouse();
            }
            else if(choice == 0){
                done = true;
            }
            else System.out.println("Inserisci un valore valido");
        }
    }

    private void playLeaderCardAction(int player){
        Board playerBoard = game.getBoard(player);
        if(playerBoard.getLeadercards().isEmpty()){
            System.out.println("Non hai nessuna carta leader da giocare");
            return;
        }
        System.out.println("Queste sono le tue carte leader:\n");
        for(int i=0;i<playerBoard.getLeadercards().size();i++){
            System.out.println((i+1)+":\n"+playerBoard.getLeadercards().get(i).toString());
        }
        int index = 0;
        do {
            System.out.println("Quale carta vuoi giocare?");
            index = Integer.parseInt(scanner.nextLine());
            if(index<1 || index>(playerBoard.getLeadercards().size())) System.out.println("Indice non valido");
        }while(index<1 || index>(playerBoard.getLeadercards().size()));
        try {
            game.playLeaderCard(player,index);
        }catch (Exception E) {System.out.println("Non soddisfi i requisiti");}
    }

    private void discardLeaderCardAction(int player) {
        Board playerBoard = game.getBoard(player);
        if(playerBoard.getLeadercards().isEmpty()){
            System.out.println("Non hai nessuna carta leader da scartare");
            return;
        }
        int i;
        System.out.println("Queste sono le tue carte leader:\n");
        for(i=0;i<playerBoard.getLeadercards().size();i++){
            System.out.println((i+1)+":\n"+playerBoard.getLeadercards().get(i).toString());
        }
        int index;
        do {
            System.out.println("Quale carta vuoi scartare?");
            index = Integer.parseInt(scanner.nextLine());
            if(index<1 || index>(playerBoard.getLeadercards().size())) System.out.println("Indice non valido");
        }while(index<1 || index>(playerBoard.getLeadercards().size()));
            game.discardLeaderCard(player,index);
    }
}
