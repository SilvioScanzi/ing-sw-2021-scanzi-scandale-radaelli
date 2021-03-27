package it.polimi.ingsw;
import java.util.*;

public class ActionStack {
    private Stack<Game.ActionToken> stack;

    public ActionStack(){

    }

    public void Shuffle(){

    }

    public Game.ActionToken Draw(){
        return stack.pop();
    }
}
