package it.polimi.ingsw.controller;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;
import java.util.*;

public class GameHandler{
    private Game game;
    private Scanner scanner = new Scanner(System.in);

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
        if(s.size()==1){
            playingSolo();
        }
        else {
            playing(s.size());
        }
    }

    private void playing(int players){
        boolean endGame=false;
        for(int i=game.getInkwell();!endGame;i=(i+1)%players){
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
                else if(c==0){
                    turnDone=true;
                }
                else{
                    System.out.println("Inserisci una scelta valida!");
                }
            }
            endGame=game.checkEndGame(i);
        }
    }

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
                } else if (c == 0) {
                    turnDone = true;
                } else {
                    System.out.println("Inserisci una scelta valida!");
                }
            }
            System.out.println("E' il turno di LorEnZoTHEMagNifICenT");
            ActionToken AT = game.activatedToken();
            System.out.println(AT.toString());
        }
    }


    private boolean choiceMenu(int c, int player) {
        switch (c) {
            case 1:  marketAction(player); break;
            case 2:  return buyDevelopmentAction(player);
            case 3:  productionAction(player); break;
            case 4:  moveAction(player); return false;
            case 5:  playLeaderCardAction(player); return false;
            case 6:  discardLeaderCardAction(player); return false;
        }
        return true;
    }

    private void marketAction(int player) {
        System.out.println("Hai scelto di comprare le risorse dal mercato");
        System.out.println(game.getMarket().toString());
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
        System.out.println("Indica il numero di" + ((RC.equals("R"))?"Riga" : "Colonna"));
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

        Board playerBoard = game.getBoard(player);
        if(playerBoard.getLeadercardsplayed().stream().anyMatch(LC -> LC.getAbility().getType().equals(Ability.AbilityType.WhiteMarbleAbility))){
            for(int i=0;i<game.getMarket().getWhiteMarbles(r,n);i++){
                boolean okConversion = false;
                do {
                    System.out.println("Quale leader vuoi utilizzare per convertire la biglia bianca?");
                    int l = Integer.parseInt(scanner.nextLine());
                    try {
                        game.leaderCardConversion(player, game.getBoard(player).getLeadercardsplayed().get(l - 1));
                        okConversion = true;
                    } catch (Exception E) {
                        System.out.println("La carta scelta non è del tipo corretto");
                    }
                }while(!okConversion);
            }
        }
        game.getMarketResources(player,r,n);

        boolean done = false;
        while(!done) {
            System.out.println("La tua mano di risorse da distribuire nel deposito è: ");
            System.out.println(playerBoard.handtoString());
            System.out.println("Come vuoi distribuirle?");
            System.out.println("1 - Deposito 1");
            System.out.println("2 - Deposito 2");
            System.out.println("3 - Deposito 3");
            System.out.println("4 - Carta Leader 1");
            System.out.println("5 - Carta Leader 2");
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

        game.discardRemainingResources(player);
    }

    private boolean buyDevelopmentAction(int player) {
        while(true){
            System.out.println(game.getDevelopmentcardmarket().toString());
            System.out.println("Se non puoi comprare nessuna carta o hai sbagliato a scegliere, digita N");
            String s = scanner.nextLine();
            if(s.equals("N")) return false;

            Colours colour = Colours.Purple; //Otherwise compiler breaks the balls
            boolean ok;
            do {
                System.out.println("Quale carta vuoi prendere?");
                System.out.println("Scegli il colore: ");
                System.out.println("BL - Blu\nVE - Verde\nGI - Giallo\nVI - Viola ");
                String c = scanner.nextLine();
                ok = true;
                switch (c) {
                    case "BL":
                        colour = Colours.Blue;
                    case "VE":
                        colour = Colours.Green;
                    case "GI":
                        colour = Colours.Yellow;
                    case "VI":
                        colour = Colours.Purple;
                    default: {
                        System.out.println("Colore non valido");
                        ok = false;
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
                System.out.println("Scegli il livello tra 1, 2 e 3: ");
                slot = Integer.parseInt(scanner.nextLine());
                if (slot == 1 || slot == 2 || slot == 3) {
                    ok = true;
                } else ok = false;
            } while (!ok);

            try {
                game.getDevelopmentCard(colour, level, player, slot);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void productionAction(int player){
        //MANCA: CONTROLLO SULL'ATTIVAZIONE DI RISORSE GIA' ATTIVATE
    }

    private void moveAction(int player){

    }

    private void playLeaderCardAction(int player){

    }

    private void discardLeaderCardAction(int player){

    }

    private void countVictoryPoints(int player){

    }
}
