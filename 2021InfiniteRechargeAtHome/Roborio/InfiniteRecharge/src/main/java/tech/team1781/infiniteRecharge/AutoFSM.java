package tech.team1781.infiniteRecharge;

import java.util.ArrayList;

import tech.team1781.infiniteRecharge.navigation.AutoStates;

public class AutoFSM{

    ArrayList <AutoStates> states = new ArrayList<AutoStates>();
    AutoStates currentState;
    int currentID;
    boolean done;

    public AutoFSM(){
        currentState = null;
        currentID = -1;
        done = false;
    }

    public void addState(AutoStates newState){
        states.add(newState);
        currentID = 0;
    }

    public void update(){
        if(states.size() == 0 || currentID == -1){
            currentState = null;
        }else if (!done){
            currentState = states.get(currentID);
        }else currentState = null;

        if(currentState != null){
            currentState.update();
        }

        System.out.println("Are we done?: " + done);
    }

    public void nextState(){
        if (states.size() > currentID + 1){
            currentID +=1;
        }else done = true;
    }


}