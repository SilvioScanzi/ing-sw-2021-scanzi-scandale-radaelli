package it.polimi.ingsw.model;

import java.util.*;

public class ActionStack {
    private Stack<ActionToken> stack;

    public ActionStack(){
        ArrayList<ActionToken> tmp = new ArrayList<>();
        tmp.add(ActionToken.Advance2);
        tmp.add(ActionToken.Advance2);
        tmp.add(ActionToken.AdvanceAndRefresh);
        tmp.add(ActionToken.DeleteYellow);
        tmp.add(ActionToken.DeleteBlue);
        tmp.add(ActionToken.DeletePurple);
        tmp.add(ActionToken.DeleteGreen);

        stack = new Stack<>();
        for(int i=0;i<7;i++){
            int index;
            index = (int) (Math.random() * (7-i));
            stack.push(tmp.remove(index));
        }
    }

    @Override
    public String toString(){
        return "La pila ha: "+stack.size()+" segnalini azione";
    }

    public ActionToken Draw(){
        return stack.pop();
    }
}
