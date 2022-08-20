/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package tech.team1781.infiniteRecharge;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

/**
 * Add your docs here.
 */
public class Conveyor {
    WPI_TalonSRX lower_conveyor, upper_conveyor;
    double lower = 0.6;
    double upper = 0.7;

    public Conveyor() {
        lower_conveyor = new WPI_TalonSRX(ConfigMap.LOWER_CONVEYER);
        upper_conveyor = new WPI_TalonSRX(ConfigMap.UPPER_CONVEYOR);

    }

    public void lowerintake() {
        lower_conveyor.set(-lower);
    }

    public void upperFeed() {
        upper_conveyor.set(upper);
    }

    public void outtake() {
        lower_conveyor.set(lower);
        upper_conveyor.set(-upper);
    }

    public void lowerStop() {
        lower_conveyor.set(0);
    }
    public void upperStop(){
        upper_conveyor.set(0);
    }
    public void stopMotors(){
        lower_conveyor.set(0);
        upper_conveyor.set(0);
    }

    // public void setConveyorSpeed(double _speed) {
    //     if (_speed > 0) {
    //         speed = _speed;
    //     } else if (speed < 0) {
    //         speed = _speed * -1;
    //     } else {
    //         speed = 0;
    //     }
    // }
}
