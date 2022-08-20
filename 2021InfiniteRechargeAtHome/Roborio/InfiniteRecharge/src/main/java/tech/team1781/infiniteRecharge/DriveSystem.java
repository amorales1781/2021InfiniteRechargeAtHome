/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package tech.team1781.infiniteRecharge;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DoubleSolenoid;

import com.kauailabs.navx.frc.AHRS;

/**
 * Add your docs here.
 */
public class DriveSystem {
    CANEncoder front_right_Encoder, front_left_Encoder, back_right_Encoder, back_left_Encoder;
    CANSparkMax front_right, front_left, back_right, back_left;
    SpeedControllerGroup m_left, m_right;
    public DifferentialDrive robotDrive;
    DoubleSolenoid driveSolinoid;
    double rotateSpeed;

    Joystick driveStick;

    ShuffleboardTab driveSystemType = Shuffleboard.getTab("Drive System Type");
    ShuffleboardTab encoderValues = Shuffleboard.getTab("Encoder Values");
    ShuffleboardTab current = Shuffleboard.getTab("CurrentOutput");
    ShuffleboardTab NavX = Shuffleboard.getTab("NavX");
    ShuffleboardTab PID = Shuffleboard.getTab("PID");
    SimpleWidget PIDValue, currentValue, encoderAverage, distanceAway;
    SimpleWidget spark1, spark2, spark3, spark4, total, left, right;
    SimpleWidget fle, ble, fre, bre;
    SimpleWidget navP, navY, navR;

    NetworkTableEntry driveType = driveSystemType.add("Curvature  |  Arcade     ", false).getEntry();
    //float totalAmps;


    AHRS driveNavX;

    PIDController drivePID;
    double kp = 0.02;
    double ki = 0.01;
    double kd = 0;
    double currentSpeed = 0;
    double currentChange = 0.1f;
    double PIDRequestSpeed = 0;
    PIDController turnPID;
    double kpCurve = 0.01875; //255
    double kiCurve = 0;
    double kdCurve = 0.001;
    double rotate, straightAdjust, avgEncoderCount;
    double encoderConst = 1.348;//2.77777777;

    public boolean atLocation, atAngle;

    double ballLoc;
    NetworkTableEntry xLocation;


    public DriveSystem(Joystick _driveStick, AHRS _navX, NetworkTableEntry xLoc) {
        xLocation = xLoc;

        driveStick = _driveStick;
        driveNavX = _navX;

        front_right = new CANSparkMax(ConfigMap.DRIVE_FRONT_RIGHT, MotorType.kBrushless);
        back_right = new CANSparkMax(ConfigMap.DRIVE_BACK_RIGHT, MotorType.kBrushless);
        
        front_right.setIdleMode(IdleMode.kBrake);
        back_right.setIdleMode(IdleMode.kBrake);

        front_right.setOpenLoopRampRate(0.1);
        back_right.setOpenLoopRampRate(0.1);

        front_right.setInverted(false);
        back_right.setInverted(false);

        m_right = new SpeedControllerGroup(front_right, back_right);

        back_left = new CANSparkMax(ConfigMap.DRIVE_BACK_LEFT, MotorType.kBrushless);
        front_left = new CANSparkMax(ConfigMap.DRIVE_FRONT_LEFT, MotorType.kBrushless);

        front_left.setOpenLoopRampRate(0.1);
        back_left.setOpenLoopRampRate(0.1);

        front_left.setIdleMode(IdleMode.kBrake);
        back_left.setIdleMode(IdleMode.kBrake);

        front_left.setInverted(false);
        back_left.setInverted(false);


        m_left = new SpeedControllerGroup(front_left, back_left);

        robotDrive = new DifferentialDrive(m_left, m_right);

        driveSolinoid = new DoubleSolenoid(ConfigMap.PCM_CanID, ConfigMap.DriveSolenoidChannelForward,
                ConfigMap.DriveSolenoidChannelReverse);

        drivePID = new PIDController(kp, ki, kd);

        turnPID = new PIDController(kpCurve, kiCurve, kdCurve);

        front_left_Encoder = front_left.getEncoder();
        back_left_Encoder = back_left.getEncoder();
        front_right_Encoder = front_right.getEncoder();
        back_right_Encoder = back_right.getEncoder();

        fle = encoderValues.add("Front Left Encoders", 0.0);
        ble = encoderValues.add("Back Left Encoders", 0.0);
        fre = encoderValues.add("Front Right Encoders", 0.0);
        bre = encoderValues.add("Back Right Encoders", 0.0);

        navP = NavX.add("Pitch", driveNavX.getPitch());
        navY = NavX.add("Yaw", driveNavX.getYaw());
        navR = NavX.add("Roll", driveNavX.getRoll());

        spark1 = current.add("Spark 21 (FR)", 0.0);
        spark3 = current.add("Spark 23 (BR)", 0.0);
        left = current.add("Right Total Current", 0.0);

        spark2 = current.add("Spark 22 (FL)", 0.0);
        spark4 = current.add("Spark 24 (BL)", 0.0);
        right = current.add("Left Total Current", 0.0);

        total = current.add("Total Current", 0.0);

        PIDValue = PID.add("Request (PID) Speed", 0.0);
        currentValue = PID.add("Current Speed", 0.0);
        encoderAverage = PID.add("Average of all encoders", 0.0);
        distanceAway = PID.add("Distance Error", 0.0);

    }

    public void update() {
        // totalAmps = 0;
        // for (int i = 0; i < 16; i++) {
        //     totalAmps += drivePanel.getCurrent(i);
        // }
        // if (driveStick.getRawButton(ConfigMap.SWITCHGEAR)) {
        //     driveSolinoid.set(DoubleSolenoid.Value.kForward);
        // } else {
        //     driveSolinoid.set(DoubleSolenoid.Value.kReverse);
        // }
        
        robotDrive.arcadeDrive(driveStick.getY()*-1, driveStick.getZ());


        // if (driveStick.getY() < .2 && driveStick.getY() > -.2 && driveStick.getZ() < .2 && driveStick.getZ() > -.2) {
        //     driveNavX.zeroYaw();
        // }

        // if (driveStick.getRawButton(12)) {
        //     resetData();
        //     driveNavX.zeroYaw();
        // }
        //sendData();
    }

    public void switchToLowGear()
    {
        driveSolinoid.set(DoubleSolenoid.Value.kForward);
    }

    public void switchToHighGear()
    {
        driveSolinoid.set(DoubleSolenoid.Value.kReverse);
    }
    
    public void targetLockOn(double targetLocation) {
        if (targetLocation == -1) {
            System.out.println("No ball found, searching for targets!");
            robotDrive.arcadeDrive(0.5, 0.0);
        } else {
            System.out.println("Found a ball! at location:" + targetLocation);
            if (targetLocation > 350 / 2 - 30 && targetLocation < 350 / 2 + 30) {
                robotDrive.arcadeDrive(0.5, 0);
            } else if (targetLocation > 350 / 2 + 30) {
                robotDrive.arcadeDrive(0.5, calculateTurnSpeed(targetLocation)/10+0.3);
            } else if (targetLocation < 350 / 2 - 30) {
                robotDrive.arcadeDrive(0.5, calculateTurnSpeed(targetLocation)/10-0.3);
            }
            System.out.println((calculateTurnSpeed(targetLocation) / 10 - 0.3));
        }
    }

    public double calculateTurnSpeed(double x) {
        if (x > 175) {
            rotateSpeed = (x - 200) / 21;
        } else if (x < 175) {
            rotateSpeed = (x - 150) / 21;
        }
        rotateSpeed = constrain(rotateSpeed, 0.3, 0.6);
        return rotateSpeed;
    }
    

    public void getEncoderCount() {
        System.out.println("-------------------");
        System.out.println("FrontleftEncoder:");
        System.out.println(front_left_Encoder.getPosition());
        System.out.println("BackleftEncoder:");
        System.out.println(back_left_Encoder.getPosition());
        System.out.println("FrontRightEncoder:");
        System.out.println(front_right_Encoder.getPosition());
        System.out.println("BackRightEncoder:");
        System.out.println(back_right_Encoder.getPosition());
    }
    
    public void move(double distance, double speed) {
        // 1 revolution = 2.77777777 inches
        rotate = turnPID.calculate(driveNavX.getYaw(), 0);
        if (driveNavX.getYaw() < 3 && driveNavX.getYaw() > -3){
            straightAdjust = 0;
        }else if (driveNavX.getYaw() > 3){
            straightAdjust = -0.4;
        }else if(driveNavX.getYaw() < -3){
            straightAdjust = 0.4;
        }

        avgEncoderCount = calculateAverage(calculateAverage(front_left_Encoder.getPosition(), back_left_Encoder.getPosition()),
        calculateAverage(front_left_Encoder.getPosition(), back_left_Encoder.getPosition()));

        PIDRequestSpeed = drivePID.calculate(-(distance/encoderConst) - avgEncoderCount);

        setSpeed(PIDRequestSpeed);
        System.out.println("This is the value of rotate" + (straightAdjust) + " and this is the angle" + driveNavX.getYaw() + "this is the average encoder counts " + avgEncoderCount);
        robotDrive.arcadeDrive(speed, straightAdjust);
        distanceAway.getEntry()
                .setDouble(-(distance/encoderConst) - calculateAverage(
                        calculateAverage(front_left_Encoder.getPosition(), back_left_Encoder.getPosition()),
                        calculateAverage(front_left_Encoder.getPosition(), back_left_Encoder.getPosition())));

        if(Math.abs(avgEncoderCount) > ((distance*1.18)/encoderConst)){
            atLocation = true;
        }else atLocation = false;

    }


    public void moveWithCamera(double distance, double speed) {
        double location = xLocation.getDouble(-1);
        double rotation = 0;
        if (location == -1) {
            rotation = 0;
        } else if (location < 160 / 2 - 10) {
            rotation = -.3;
        } else if (location > 160 / 2 + 10) {
            rotation = .3;
        } else {
            rotation = 0;
        }

        avgEncoderCount = calculateAverage(
                calculateAverage(front_left_Encoder.getPosition(), back_left_Encoder.getPosition()),
                calculateAverage(front_left_Encoder.getPosition(), back_left_Encoder.getPosition()));

        if (Math.abs(avgEncoderCount) > ((distance * 1.18) / encoderConst)) {
            atLocation = true;
        } else {
            atLocation = false;
        }

        robotDrive.arcadeDrive(speed, rotation);

    }


    public void setSpeed(double requestSpeed) {
        // currentSpeed = front_left.get();
        if (currentSpeed < requestSpeed && currentSpeed < 1) {
            currentSpeed += currentChange;
            System.out.println(currentSpeed + "," + requestSpeed);
        } else if (currentSpeed > requestSpeed && currentSpeed > -1) {
            currentSpeed -= currentChange;
        } else if (requestSpeed == 0) {
            currentSpeed = 0;
        }
    }

    public double calculateAverage(double num1, double num2) {
        double sum = num1 + num2;
        return sum / 2;
    }

    public void resetData() {
        front_left_Encoder.setPosition(0);
        back_left_Encoder.setPosition(0);
        front_right_Encoder.setPosition(0);
        back_right_Encoder.setPosition(0);
        zeroNavX();
        currentSpeed = 0;
    }

    public void zeroNavX() {
        driveNavX.zeroYaw();
        atAngle = false;
    }

    public void sendData() {
        fle.getEntry().setDouble(front_left_Encoder.getPosition());
        ble.getEntry().setDouble(back_left_Encoder.getPosition());
        fre.getEntry().setDouble(front_right_Encoder.getPosition());
        bre.getEntry().setDouble(back_right_Encoder.getPosition());

        navP.getEntry().setDouble(driveNavX.getPitch());
        navY.getEntry().setDouble(driveNavX.getYaw());
        navR.getEntry().setDouble(driveNavX.getRoll());

        //total.getEntry().setDouble(totalAmps);

        PIDValue.getEntry().setDouble(PIDRequestSpeed);
        currentValue.getEntry().setDouble(currentSpeed);
        encoderAverage.getEntry()
                .setDouble(calculateAverage(
                        calculateAverage(front_left_Encoder.getPosition(), back_left_Encoder.getPosition()),
                        calculateAverage(front_left_Encoder.getPosition(), back_left_Encoder.getPosition())));
    }

    public double constrain(double value, double min,double max)
    {
        return (value > max ) ? max : (value < min ? min:value);
    }

    public double getEncoderPosition()
    {
        return back_left_Encoder.getPosition();
    }

    public void turnTo(float angle){
        rotate = turnPID.calculate(driveNavX.getYaw(), angle);
        System.out.println("This is the rotate value: " + rotate);
        if (rotate > 0){
            rotate = constrain(rotate, 0.3, 0.6);
        }else rotate = constrain(rotate, -0.6, -0.3);

        System.out.println("This is the constrained rotate value: " + rotate);

        if(driveNavX.getYaw() < angle+1 && driveNavX.getYaw() > angle-1){
            atAngle = true;
            robotDrive.arcadeDrive(0, 0);
            System.out.println("This is the atAngle: " + atAngle);
        }else {
            if (driveNavX.getYaw() > Math.abs(angle)-5){
                if(angle < 0){
                    robotDrive.arcadeDrive(0, -0.3);
                }else robotDrive.arcadeDrive(0, 0.3);
            }else robotDrive.arcadeDrive(0, rotate);
            atAngle = false;
        }
    }

    public boolean isAtAngle(){
        return atAngle;
    }

    public boolean isAtLocation(){
        return atLocation;
    }

    public float getYaw(){
        return driveNavX.getYaw();
    }


}
