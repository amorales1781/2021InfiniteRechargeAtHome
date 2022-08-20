package tech.team1781.infiniteRecharge.navigation;

import tech.team1781.infiniteRecharge.AutoFSM;

public abstract class AutoStates {

    AutoFSM fsm;
    AutoStates(AutoFSM f){
        fsm = f;
    }

    public void update(){

    }
    
}