package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.ActionToken;

import java.util.*;

public class ActionStack {
    private final Stack<ActionToken> stack;

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

    /**
     * method used for testing, it initialize an Action stack with a standard configuration
     * @param Arandom is just used to overload the method
     */
    public ActionStack(int Arandom){
        ArrayList<ActionToken> tmp = new ArrayList<>();

        tmp.add(ActionToken.AdvanceAndRefresh);
        tmp.add(ActionToken.Advance2);
        tmp.add(ActionToken.Advance2);
        tmp.add(ActionToken.DeleteYellow);
        tmp.add(ActionToken.DeleteBlue);
        tmp.add(ActionToken.DeletePurple);
        tmp.add(ActionToken.DeleteGreen);

        stack = new Stack<>();
        for(int i=0;i<7;i++){
            stack.push(tmp.remove(0));
        }
    }

    public Stack<ActionToken> getStack() {
        return stack;
    }

    public ActionToken draw(){
        return stack.pop();
    }
}
