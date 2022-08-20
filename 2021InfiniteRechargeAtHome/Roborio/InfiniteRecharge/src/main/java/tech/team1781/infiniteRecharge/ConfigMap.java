/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package tech.team1781.infiniteRecharge;

/**
 * Add your docs here.
 */
public class ConfigMap {

    // Drivetrain CAN ID's
    public final static int DRIVE_FRONT_RIGHT = 21;
    public final static int DRIVE_BACK_RIGHT = 23;

    public final static int DRIVE_BACK_LEFT = 24;
    public final static int DRIVE_FRONT_LEFT = 22;

    //Phnumatics
    public final static int DriveSolenoidChannelForward = 0;
    public final static int DriveSolenoidChannelReverse = 1;
    public final static int CollectSolenoidChannelReverse1 = 3;
    public final static int CollectSolenoidChannelForward1 = 2;
    public final static int CollectSolenoidChannelReverse2 = 5;
    public final static int CollectSolenoidChannelForward2 = 4;

    // Turret CAN id's
    public final static int TURRET_SHOOTING_RIGHT = 1;
    public final static int TURRET_SHOOTING_LEFT = 2;
    public final static int TURRET_ROTATION = 3;
    public final static int LOWER_CONVEYER = 4;
    public final static int UPPER_CONVEYOR = 5;
    public final static int HOPPER = 6;

    // Collector CAN ID's
    public final static int COLLECTOR_MOTOR_CENTER = 7;
    public final static int COLLECTOR_MOTOR_RIGHT = 8;
    public final static int COLLECTOR_MOTOR_LEFT = 9;

    //climber CAN ID's
    public final static int CLIMB_MOTOR = 25;
    public final static int HOOK_DEPLOY = 10;

    // Control Panel CAN ID's
    public final static int PANEL_MOTOR = 11;

    // Current
    public final static int DRIVE_MOTOR_MAX_CURRENT = 35;

    // Other CAN ID's
    public final static int PCM_CanID = 50;
    public final static int PDP_CanID = 0;

    // DIO Ports
    public final static int SHOOTER_ENCODER_DIO1 = 0;
    public final static int SHOOTER_ENCODER_DIO2 = 1;
    public final static int LEFT_SHOOT_LIMIT_SWITCH = 2;
    public final static int RIGHT_SHOOT_LIMIT_SWITCH = 3;
    public final static int TURRET_ENCODER_DIO1 = 4;
    public final static int TURRET_ENCODER_DIO2 = 5;
    public final static int CONTROL_PANEL_ENCODER_DIO1 = 6;
    public final static int CONTROL_PANEL_ENCODER_DIO2 = 7;


    // Pilot Button Map
    public final static int COLLECT = 7;
    public final static int TILT = 1;
    public final static int DISPENSE = 8;

    public final static int SWITCHGEAR = 3;

    // Co-Pilot ButtonMap
    public final static int SHOOT = 1;
    public final static int FEED_TURRET = 2;
    public final static int PANEL_SPIN_LEFT = 3;
    public final static int PANEL_SPIN_RIGHT = 4;
    public final static int REVERSE_TURRET = 5; 
    public final static int AIM = 8;
    public final static int COMPLETE_ROTATION_CONTROL = 7;
    public final static int DEPLOY= 10;
    public final static int RETRACT = 9;
    public final static int CLIMB = 12;
    public final static int DESCEND = 11;

}
