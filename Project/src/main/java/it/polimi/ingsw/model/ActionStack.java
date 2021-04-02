package it.polimi.ingsw.model;
import it.polimi.ingsw.Game;

import java.util.*;

public class ActionStack {
    private Stack<Game.ActionToken> stack;
    //per lo shuffle o inseriamo 2 attributi della classe actionstack (arraylist dei token) o chiamiamo il costruttore ogni volta che è necessario
    //oppure metodo CLEAR per svuotare lo stack e riinizializzarlo

    public ActionStack(){
        ArrayList<Game.ActionToken> tmp = new ArrayList<>();
        tmp.add(Game.ActionToken.Advance2);
        tmp.add(Game.ActionToken.Advance2);
        tmp.add(Game.ActionToken.AdvanceAndRefresh);
        tmp.add(Game.ActionToken.DeleteYellow);
        tmp.add(Game.ActionToken.DeleteBlue);
        tmp.add(Game.ActionToken.DeletePurple);
        tmp.add(Game.ActionToken.DeleteGreen);

        stack = new Stack<Game.ActionToken>();
        for(int i=0;i<7;i++){
            int index;
            index = (int) (Math.random() * (7-i));
            stack.push(tmp.remove(index));
        }
    }

    public Game.ActionToken Draw(){
        return stack.pop();
    }
}
