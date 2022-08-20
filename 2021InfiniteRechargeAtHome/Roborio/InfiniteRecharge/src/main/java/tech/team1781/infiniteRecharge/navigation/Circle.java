package tech.team1781.infiniteRecharge.navigation;

import tech.team1781.infiniteRecharge.AutoFSM;
import tech.team1781.infiniteRecharge.DriveSystem;
import edu.wpi.first.wpilibj.Timer;

public class Circle extends AutoStates {
    AutoFSM fsm;
    Timer timer;
    DriveSystem drive;
    double seconds, ySpeed, zTwist;
    boolean started;

    public Circle(AutoFSM f,DriveSystem d, double y, double z){
        super(f);
        timer = new Timer();
        fsm = f;
        drive = d;
        started = false;
        ySpeed = y;
        zTwist = z;
    }

    public void update(){
        if(!started){
            timer.reset();
            timer.start();
            drive.zeroNavX();
            started = true;
        }
        System.out.println("This is the navx get yaw: " + drive.getYaw());

        if(timer.get() > 2 && drive.getYaw() >= -3 && drive.getYaw() <= 3){
            System.out.println("************IN THE THRESHOLD************************: " + drive.getYaw());
            //drive.robotDrive.arcadeDrive(0,0);
            fsm.nextState();
        }else {
            if (timer.get() > 2 && Math.abs(drive.getYaw()) < 35){
                System.out.println("&&&&&&&&& WE ARE SLOWING DOWN &&&&&&&&&");
                drive.robotDrive.arcadeDrive(ySpeed/2, zTwist/2);
            }else {
                drive.robotDrive.arcadeDrive(ySpeed,zTwist);
                System.out.println("&&&&&&&&& WE ARE CIRCLING AT NORMAL SPEED &&&&&&&&&");
            }
        }
    }
    
}
