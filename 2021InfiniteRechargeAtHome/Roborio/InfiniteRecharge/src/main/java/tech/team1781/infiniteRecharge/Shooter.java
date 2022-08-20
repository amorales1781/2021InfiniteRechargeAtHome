/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package tech.team1781.infiniteRecharge;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Joystick;

public class Shooter {
    WPI_TalonSRX hopper;

    Collector collect;
    Turret turret;
    Conveyor convey;
    public boolean lockedOn, empty, collectedAll;
    Joystick pilot, coPilot;
    Camera limeLight;

    public Shooter(Joystick _pilot, Joystick _coPilot, Camera _cam) {

        pilot = _pilot;
        coPilot = _coPilot;
        limeLight = _cam;

        hopper = new WPI_TalonSRX(ConfigMap.HOPPER);

        collect = new Collector(pilot);

        turret = new Turret(coPilot);
        convey = new Conveyor();
    }

    public void update() {
        turret.sendData();
        if (pilot.getRawButton(ConfigMap.DISPENSE)) {
            collect.outtake();
            convey.outtake();
        } else if (coPilot.getRawButton(ConfigMap.FEED_TURRET)) {
            convey.upperFeed();
            convey.lowerintake();
        } else if (pilot.getRawButton(ConfigMap.COLLECT)) {
            collect.intake();
            convey.lowerintake();
        } else {
            convey.lowerStop();
            convey.upperStop();
            collect.stopMotors();
        }
        if (pilot.getRawButton(ConfigMap.TILT)) {
            collect.tiltForward();
        } else {
            collect.tiltBack();
        }

        if (coPilot.getRawButton(ConfigMap.REVERSE_TURRET)) {
            turret.reverseHopper();
            turret.reverseShooting();
        } else if (coPilot.getRawButton(ConfigMap.SHOOT)) {
            turret.shoot();
            hopper.set(.8);
        } else {
            turret.stopShooting();
            hopper.set(0);
        }

        if (coPilot.getRawButton(ConfigMap.AIM)) {
            
            if (coPilot.getZ() > .1) {
                turret.aimRight(coPilot.getZ());
            } else if (coPilot.getZ() < -.1) {
            turret.aimLeft(coPilot.getZ());
        } else lockOnPort(limeLight.getPortX());
    }else turret.stopTurret();
    
    turret.sendData();

    }

    public void lockOnPort(double portTargetX) {
        limeLight.turnLightsOn();
        turret.autoAim(portTargetX);
        if (portTargetX < 1.5 && portTargetX > -1.5) {
            turret.stopTurret();
            lockedOn = true;
        } else lockedOn = false;
    }

    public void collect() {
        collect.tiltForward();
        collect.intake();
        convey.lowerintake();
    }

    public void dontCollect() {
        collect.tiltBack();
        collect.stopMotors();
    }

    public void shootPowercells() {
        turret.setShootingSpeed(2);
        if (turret.shooterAtSpeed) {
            convey.lowerintake();
            convey.upperFeed();
            turret.loadShooter();
        }
        if (turret.shootingTimer.get() >= 4) {
            empty = true;
            turret.shootingTimer.stop();
            turret.stopShooting();
        }
    }

    public void stopUpperConvey() {
        convey.upperStop();
    }

    public void stopHopper() {
        turret.stopHopper();
    }

    public void tiltCollectorForward() {
        collect.tiltForward();
    }

    public void startTimer() {
        turret.shootingTimer.start();
    }

    public void resetTimer() {
        turret.shootingTimer.reset();
    }

    public void rotateTurretToAngle(){
        turret.rotateToAngle(180);
    }
    public void shootAtRPM(){
        turret.setShootingSpeed(-250000);
    }
}
