package tech.team1781.infiniteRecharge;

import tech.team1781.infiniteRecharge.DriveSystem;
import edu.wpi.first.wpilibj.Timer;

public class GalacticSearchPaths {

    int path;
    Timer timer;

    DriveSystem drive;

    public GalacticSearchPaths(DriveSystem _drive, int _path) {
        path = _path;
        drive = _drive;
        timer = new Timer();
    }

    void move() {
        timer.start();
        switch (path) {
        case 1:
            redPath1();
            break;
        case 2:
            redPath2();
            break;
        case 3:
            bluePath1();
            break;
        case 4:
            bluePath2();
            break;
        }
    }

    void redPath1() {
        if (timer.get() < 1) {// Move Straight

        } else if (timer.get() < 1.5 && timer.get() > 1) {// Turn Right 26.5 degree

        } else if (timer.get() < 2.5 && timer.get() > 1.5) {// Move Straight

        } else if (timer.get() < 3 && timer.get() > 2.5) {// Turn Left 26.5+71.5 degree

        } else if (timer.get() < 4 && timer.get() > 3) {// Move Straight

        } else if (timer.get() < 4.5 && timer.get() > 4) {// Turn Right 73 degrees

        } else if (timer.get() < 7.5 && timer.get() > 4.5) {// Move Straight

        }
    }

    void redPath2() {
        if (timer.get() < .5) {// Turn Left 26.5 degree

        } else if (timer.get() < 2.5 && timer.get() > 1.5) {// Move Straight

        } else if (timer.get() < 2.5 && timer.get() > 1.5) {// Turn Right

        } else if (timer.get() < 2.5 && timer.get() > 1.5) {// Move Straight

        } else if (timer.get() < 2.5 && timer.get() > 1.5) {// Turn Left

        } else if (timer.get() < 2.5 && timer.get() > 1.5) {// Move Straight

        } else if (timer.get() < 2.5 && timer.get() > 1.5) {// Turn Right

        } else if (timer.get() < 2.5 && timer.get() > 1.5) {// Move Straight

        }
    }

    void bluePath1() {
        if (timer.get() < .5) {// Turn Right

        } else if (timer.get() < 2.5 && timer.get() > 1.5) {// Move Straight

        } else if (timer.get() < 2.5 && timer.get() > 1.5) {// Turn Left 71.5 degree

        } else if (timer.get() < 2.5 && timer.get() > 1.5) {// Move Straight

        } else if (timer.get() < 2.5 && timer.get() > 1.5) {// Turn Right 26.5 degree

        } else if (timer.get() < 2.5 && timer.get() > 1.5) {// Move Straight

        }

    }

    void bluePath2() {
        if (timer.get() < .5) {// Turn Right

        } else if (timer.get() < 2.5 && timer.get() > 1.5) {// Move Straight

        } else if (timer.get() < 2.5 && timer.get() > 1.5) {// Turn Left

        } else if (timer.get() < 2.5 && timer.get() > 1.5) {// Move Straight

        } else if (timer.get() < 2.5 && timer.get() > 1.5) {// Turn Right

        } else if (timer.get() < 2.5 && timer.get() > 1.5) {// Move Straight

        }

    }
}