/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import edu.wpi.first.wpilibj.SerialPort;
import tech.team1781.infiniteRecharge.DriveSystem;
import tech.team1781.infiniteRecharge.Shooter;
import tech.team1781.infiniteRecharge.navigation.Circle;
import tech.team1781.infiniteRecharge.navigation.DriveDistance;
import tech.team1781.infiniteRecharge.navigation.DriveDistanceWithAssist;
import tech.team1781.infiniteRecharge.navigation.Idle;
import tech.team1781.infiniteRecharge.navigation.TurnTo;
import tech.team1781.infiniteRecharge.AutoFSM;
import tech.team1781.infiniteRecharge.Camera;
import tech.team1781.infiniteRecharge.Climber;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.VideoMode;
import edu.wpi.first.cameraserver.*;

import tech.team1781.infiniteRecharge.ControlPanel;
import tech.team1781.infiniteRecharge.Conveyor;

import com.kauailabs.navx.frc.AHRS;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  Joystick pilot, coPilot;
  DriveSystem drive;
  Compressor compress;
  Conveyor conveyor;
  Climber climb;
  ControlPanel controlPanel;
  UsbCamera camera;
  MjpegServer cameraServer;
  Camera limeLight;
  Shooter shoot;
  AHRS navX;
  AutoFSM auto;

  Timer autoTimer, teleTimer;

  ShuffleboardTab autonomous = Shuffleboard.getTab("Auto");
  NetworkTableInstance netInstance;
  NetworkTable table, limeTable;
  NetworkTableEntry xLoc, limeX, limeY, limeTargetArea, camMode, cellsCollected, turnPiOff, heartBeat; // location of
                                                                                                       // powercell
  double PowercellXLoc;
  SimpleWidget heartBeatCounter;
  NetworkTableEntry autoChoice1 = autonomous.add("Trench  |  Middle     ", false).getEntry();
  // NetworkTableEntry autoChoice2 = autonomous.add("False | True ",
  // false).getEntry();
  // NetworkTableEntry autoChoice3 = autonomous.add("False | True ",
  // false).getEntry();
  boolean auto1, auto2, auto3;

  int state = 0;

  String gameData;

  @Override
  public void robotInit() {
    limeLight = new Camera();
    navX = new AHRS(SerialPort.Port.kMXP);

    autoTimer = new Timer();

    autoTimer = new Timer();

    pilot = new Joystick(0);
    coPilot = new Joystick(1);

    netInstance = NetworkTableInstance.getDefault();
    table = netInstance.getTable("SmartDashboard");

    xLoc = table.getEntry("x");

    drive = new DriveSystem(pilot, navX, xLoc);
    climb = new Climber(coPilot);
    // controlPanel = new ControlPanel(coPilot);
    shoot = new Shooter(pilot, coPilot, limeLight);

    compress = new Compressor(50);
    compress.start();
    camera = CameraServer.getInstance().startAutomaticCapture();
    camera.setVideoMode(VideoMode.PixelFormat.kMJPEG, 160, 120, 20);
    camera.setExposureManual(10);
    turnPiOff = table.getEntry("piPower");
    heartBeatCounter = autonomous.add("Raspi Vision Counter", 0.0);

    auto = new AutoFSM();

    autoTimer.start();

    drive.resetData();
    drive.zeroNavX();

    drive.switchToLowGear();
  }

  @Override
  public void robotPeriodic() {
    turnPiOff = table.getEntry("piPower");
    heartBeatCounter.getEntry().setDouble(heartBeat.getDouble(0));
  }

  @Override
  public void disabledInit() {
    super.disabledInit();
    heartBeat = table.getEntry("heartBeat");
    heartBeatCounter.getEntry().setDouble(heartBeat.getDouble(0));
  }

  @Override
  public void autonomousInit() {
    limeLight.setCameraMode(0);
    autoTimer.reset();
    autoTimer.start();
    //shoot.tiltCollectorForward();
    drive.switchToLowGear();
    auto1 = autoChoice1.getBoolean(false);
    auto2 = autoChoice1.getBoolean(false);
    auto3 = autoChoice1.getBoolean(false);
    drive.zeroNavX();
    drive.resetData();
    //autoCircle();
    //barrelPath();
    //slalomPath();
    bouncePath();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    // if (auto1) {
    // centerAuto();
    // } else {
    // alliedTrenchAuto();
    // }
    auto.update();
    drive.sendData();
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopInit() {
    super.teleopInit();
    teleTimer = new Timer();
    teleTimer.start();
    turnPiOff.forceSetDouble(1.0);
    drive.switchToLowGear();
    limeLight.setCameraMode(0);
    // limeLight.turnLightsOff();
  }

  @Override
  public void teleopPeriodic() {
    drive.update();
    shoot.update();
    if (pilot.getRawButton(11)) {
      compress.clearAllPCMStickyFaults();
    }
    System.out.println("Limelight target:" + limeLight.getPortX());
    if (pilot.getRawButton(12)) {
      shoot.rotateTurretToAngle();
    }
    if (pilot.getRawButton(10)) {
      shoot.shootAtRPM();
    }

    // stop compresssor for climbing to reserve amperage for climbing
    if (teleTimer.get() > 120.0) {
      compress.stop();
      climb.update();
    }
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
    // drive.switchToLowGear();
    xLoc = table.getEntry("x");
    cellsCollected = table.getEntry("cells");
    // System.out.println(limeLight.getPortX());
    drive.update();
    drive.sendData();

    if (pilot.getRawButton(12)) {
      drive.turnTo(45);
    }

    if(pilot.getRawButton(11)){
      drive.zeroNavX();
    }
    System.out.println("This is the robotAngle: " + drive.getYaw());
    // shoot.lockOnPort(limeLight.getPortX());
    // System.out.println("===========" + xLoc.getDouble(-1));
    // System.out.println("-------" + cellsCollected.getDouble(0));
    // drive.targetLockOn(xLoc.getDouble(-1));
  }

  public void alliedTrenchAuto() {
    switch (state) {
      case 0:
        shoot.lockOnPort(limeLight.getPortX());
        if (shoot.lockedOn) {
          state = 1;
          shoot.resetTimer();
          shoot.startTimer();
        }
        break;
      case 1:
        drive.robotDrive.arcadeDrive(0, 0);
        shoot.shootPowercells();
        if (shoot.empty) {
          state = 2;
        }
        break;
      case 2:
        xLoc = table.getEntry("x");
        if (drive.getEncoderPosition() < 135) {
          drive.robotDrive.arcadeDrive(0.6, 0);
        } else {
          drive.robotDrive.arcadeDrive(0, 0);
          state = 3;
        }
        shoot.collect();
        shoot.stopUpperConvey();
        // shoot.stopHopper();
        if (shoot.collectedAll) {
          state = 3;
          shoot.dontCollect();
        }
        break;
      case 3:
        if (drive.getEncoderPosition() > 0) {
          drive.robotDrive.arcadeDrive(-0.6, 0);
        }
        if (drive.atLocation) {
          state = 3;
        }
        break;
    }
  }

  public void centerAuto() {
    switch (state) {
      case 0:
        drive.robotDrive.arcadeDrive(0, 0);
        shoot.shootPowercells();
        if (shoot.empty) {
          state = 1;
        }
        break;
      case 1:
        shoot.dontCollect();
        shoot.stopUpperConvey();
        // shoot.stopHopper();
        if (autoTimer.get() < 8) {
          drive.robotDrive.arcadeDrive(0.5, 0);
        } else
          drive.robotDrive.arcadeDrive(0, 0);
        break;
    }
  }

  public void oppositeTrench() {
    switch (state) {
      case 0: // Shoot all powercells
        shoot.shootPowercells();
        if (shoot.empty) {
          state = 1;
        }
        break;
      case 1: // Drive off autoLine
        drive.targetLockOn(xLoc.getDouble(-1));
        if (shoot.collectedAll) {
          state = 2;
          shoot.dontCollect();
        }
        break;
      case 2:
        //drive.move(-2); // move out of enemy trench
        break;
    }
  }

  public void bouncePath() {
    auto.addState(new DriveDistance(auto, drive, 45, 0.5));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new TurnTo(auto, drive, -90));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new DriveDistanceWithAssist(auto, drive, 40, 0.5));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new TurnTo(auto, drive, -20));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new DriveDistance(auto, drive, 100, -0.5));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new TurnTo(auto, drive, 111));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new DriveDistance(auto, drive, 45, 0.5));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new TurnTo(auto, drive, -90));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new DriveDistanceWithAssist(auto, drive, 125, 0.5));
    auto.addState(new Idle(auto, drive, 0.75));
    auto.addState(new DriveDistance(auto, drive, 125, -0.5));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new TurnTo(auto, drive, 90));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new DriveDistance(auto, drive, 90, 0.5));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new TurnTo(auto, drive, -90));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new DriveDistanceWithAssist(auto, drive, 125, 0.5));
    auto.addState(new Idle(auto, drive, 0.75));
    auto.addState(new DriveDistance(auto, drive, 65, -0.5));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new TurnTo(auto, drive, 90));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new DriveDistance(auto, drive, 60, 0.5));
    auto.addState(new Idle(auto, drive, 0.5));
  }

  public void slalomPath(){
    auto.addState(new DriveDistance(auto, drive, 45, 0.5));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new TurnTo(auto, drive, -90));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new DriveDistance(auto, drive, 65, 0.5));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new TurnTo(auto, drive, 86));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new DriveDistance(auto, drive, 195, 0.5));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new TurnTo(auto, drive, 88));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new DriveDistance(auto, drive, 35, 0.5));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new Circle(auto, drive, 0.75, -0.625));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new DriveDistance(auto, drive, 40, 0.5));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new TurnTo(auto, drive, 80));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new DriveDistance(auto, drive, 190, 0.5));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new TurnTo(auto, drive, 90));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new DriveDistance(auto, drive, 55, 0.5));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new TurnTo(auto, drive, -90));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new DriveDistance(auto, drive, 60, 0.5));
    auto.addState(new Idle(auto, drive, 0.5));
  }

  public void barrelPath(){
    auto.addState(new DriveDistance(auto, drive, 105, 0.5));
    auto.addState(new Idle(auto, drive, 0.5));
    //at D5, doing square around D5
    auto.addState(new Circle(auto, drive, 0.75, 0.625));
    auto.addState(new Idle(auto, drive, 0.5));
    //around D5, driving forward to B8
    auto.addState(new DriveDistance(auto, drive, 90, 0.5));
    auto.addState(new Idle(auto, drive, 0.5));
    //at B8, turning left to do square B8
    auto.addState(new TurnTo(auto, drive, -15));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new Circle(auto, drive, 0.75, -0.63));
    auto.addState(new Idle(auto, drive, 0.5));
    //driving to D10
    auto.addState(new TurnTo(auto, drive, 90));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new DriveDistance(auto, drive, 60, 0.5));
    auto.addState(new Idle(auto, drive, 0.5));
    //u-turn around D10
    auto.addState(new TurnTo(auto, drive, -90));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new DriveDistance(auto, drive, 90, 0.5));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new TurnTo(auto, drive, -90));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new DriveDistance(auto, drive, 55, 0.5));
    auto.addState(new Idle(auto, drive, 0.5));
    auto.addState(new TurnTo(auto, drive, -90));
    auto.addState(new Idle(auto, drive, 0.5));
    //driving to finish
    auto.addState(new DriveDistanceWithAssist(auto, drive, 285, 0.6));
    auto.addState(new Idle(auto, drive, 0.5));
  }

}
