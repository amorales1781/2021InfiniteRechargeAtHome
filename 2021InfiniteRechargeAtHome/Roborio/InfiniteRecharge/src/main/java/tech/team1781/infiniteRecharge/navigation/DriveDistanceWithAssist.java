package tech.team1781.infiniteRecharge.navigation;

import tech.team1781.infiniteRecharge.AutoFSM;
import tech.team1781.infiniteRecharge.DriveSystem;
import edu.wpi.first.wpilibj.Timer;

public class DriveDistanceWithAssist extends AutoStates{
    AutoFSM fsm;
    Timer timer;
    DriveSystem drive;
    double distance, speed;
    boolean started;

    public DriveDistanceWithAssist(AutoFSM f,DriveSystem d, double _dist, double _speed){
        super(f);
        timer = new Timer();
        fsm = f;
        drive = d;
        distance = _dist;
        speed = _speed;
        started = false;
    }

    public void update(){
        if(!started){
            timer.reset();
            timer.start();
            drive.resetData();
            drive.atLocation = false;
            started = true;
        }
        if(drive.isAtLocation()){
            fsm.nextState();
            drive.resetData();
            drive.atLocation = false;
        }else {
            drive.moveWithCamera(distance, speed);
        }
    }

}
