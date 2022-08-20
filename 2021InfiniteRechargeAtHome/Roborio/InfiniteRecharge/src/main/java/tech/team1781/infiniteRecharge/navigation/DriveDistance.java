package tech.team1781.infiniteRecharge.navigation;

import tech.team1781.infiniteRecharge.AutoFSM;
import tech.team1781.infiniteRecharge.DriveSystem;
import edu.wpi.first.wpilibj.Timer;

public class DriveDistance extends AutoStates{
    AutoFSM fsm;
    Timer timer;
    DriveSystem drive;
    double distance, speed;
    boolean started;

    public DriveDistance(AutoFSM f,DriveSystem d, double _dist, double _speed){
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
        System.out.println("******This is the distance: " + distance + "atLocation boolean: " + drive.isAtLocation() + "*******");
        if(drive.isAtLocation()){
            fsm.nextState();
        }else {
            drive.move(distance, speed);
            System.out.println("*****DRIVING!!!!!!***********");
        }
    }

}