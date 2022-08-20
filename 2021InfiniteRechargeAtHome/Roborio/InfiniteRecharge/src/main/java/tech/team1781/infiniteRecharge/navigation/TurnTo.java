package tech.team1781.infiniteRecharge.navigation;

import tech.team1781.infiniteRecharge.AutoFSM;
import tech.team1781.infiniteRecharge.DriveSystem;
import edu.wpi.first.wpilibj.Timer;

public class TurnTo extends AutoStates{
    AutoFSM fsm;
    Timer timer;
    DriveSystem drive;
    double seconds;
    boolean started;
    float angle;

    public TurnTo(AutoFSM f,DriveSystem d, float _angle){
        super(f);
        timer = new Timer();
        fsm = f;
        drive = d;
        angle = _angle;
        started = false;
    }

    public void update(){
        if(!started){
            timer.reset();
            timer.start();
            drive.resetData();
            started = true;
        }

        if(drive.isAtAngle()){
            System.out.println("--------------This is the angle we turned to "+ drive.getYaw() + "----------------------");
            fsm.nextState();
        }else {
            drive.turnTo(angle);
        }

        System.out.println("Are we at the angle?: " + drive.isAtAngle() + " and this is our angle: " + drive.getYaw());
    }

}