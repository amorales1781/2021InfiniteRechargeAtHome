package tech.team1781.infiniteRecharge.navigation;

import tech.team1781.infiniteRecharge.AutoFSM;
import tech.team1781.infiniteRecharge.DriveSystem;
import edu.wpi.first.wpilibj.Timer;

public class Idle extends AutoStates{
    AutoFSM fsm;
    Timer timer;
    DriveSystem drive;
    double seconds;
    boolean started;

    public Idle(AutoFSM f,DriveSystem d, double s ){
        super(f);
        timer = new Timer();
        fsm = f;
        drive = d;
        seconds = s;
        started = false;
    }

    public void update(){
        if(!started){
            timer.reset();
            timer.start();
            drive.resetData();
            started = true;
        }

        if(timer.get() > seconds){
            fsm.nextState();
        }else {
            drive.robotDrive.arcadeDrive(0,0);
        }
    }

}