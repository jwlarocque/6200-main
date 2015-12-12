package com.qualcomm.ftcrobotcontroller.opmodes;

//import
import android.graphics.Path;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Test driver controlled operating mode for FTC team 6200
 * Created by John LaRocque on 10/5/15.
 * TODO implement navigation/awareness/sensor fusion
 */
public class testOp extends OpMode {

    //!!! abstraction layer (quick settings) !!! ---------------------------------------------------

    //control abstraction
    double left_drive_pwr() {return gamepad1.left_stick_y;}
    double right_drive_pwr() {return gamepad1.right_stick_y;}
    double lift_pwr() {return gamepad2.left_stick_y;}
    //if the driver (gamepad1) is pressing any shoulder bumper or trigger, we enable fine drive controls
    //I've used trigger > .1 because I don't want it to be triggered accidentally (not sure how the sensor works)
    boolean fine_drive() {return (gamepad1.right_bumper || gamepad1.left_bumper);}
    boolean reverse_button() {return (gamepad1.x);}
    boolean extend_left() {if (separate_servos) {return gamepad2.left_bumper;} return gamepad1.a;}
    boolean retract_left() {if (separate_servos) {return gamepad2.left_trigger > 0.0;} return gamepad1.b;}
    //first input is for separate servo input, second is otherwise
    boolean extend_right() {if (separate_servos) {return gamepad2.right_bumper;} return gamepad1.a;}
    boolean retract_right() {if (separate_servos) {return gamepad2.right_trigger > 0.0;} return gamepad1.b;}
    boolean extend_latch() {return gamepad2.a;}
    boolean retract_latch() {return gamepad2.b;}


    //leave these as false because I didn't actually write anything for encoders
    boolean right_drive_enc = false;
    boolean left_drive_enc = false;

    //keeps track of whether we're in reverse drive mode or not (toggle)
    boolean reverse = false;
    boolean last_reverse = false;

    //this is the exponential scaling for stick input, do not change this much
    final static double drive_scale_exp = 1.4;
    //under fine drive control, this exponent is used instead, feel free to adjust, greater values give finer control, must be greater than 1
    final static double fine_drive_scale_exp = 2.0;
    //under fine drive control, full throttle sets this motor power, feel free to adjust, between 0 and 1
    final static double fine_drive_max_power = .3;

    final static DcMotor.Direction right_direction = DcMotor.Direction.FORWARD;
    final static DcMotor.Direction right_front_direction = DcMotor.Direction.FORWARD;
    final static DcMotor.Direction left_direction = DcMotor.Direction.REVERSE;
    final static DcMotor.Direction left_front_direction = DcMotor.Direction.REVERSE;

    //motor declarations
    private DcMotor right_drive;
    private DcMotor left_drive;
    private DcMotor right_drive_front;
    private DcMotor left_drive_front;
    private DcMotor lift;
    //names of above motors for identification
    //!!! THESE MUST MATCH THE NAMES IN THE ROBOT CONFIG FILE !!!
    final static String right_drive_name = "right";
    final static String left_drive_name = "left";
    final static String right_front_name = "right_front";
    final static String left_front_name = "left_front";
    final static String lift_name = "lift";

    //should we attempt to use servos? (currently: servo1 and servo2)
    final static boolean has_servos = true;
    //should we control the left and right servos separately? (see gamepad inputs above)
    boolean separate_servos = true;
    //should we attempt to use the latch servo?
    final static boolean has_latch = true;
    //starting positions of servos, with current hardware configuration, these should be the same
    double left_position = 0.9;
    double right_position = 0.9;
    //starting position of latch servo
    double latch_position = 1.0;
    //position of servos in deployed state (this value has no units, idk how it works)
    final static double servo_deploy = 0.0;
    //position of servos in retracted state
    final static double servo_retract = 0.9;
    //amount to increment/decrement servo position, affects speed at which they move
    final static double servo_increment = .02;

    //servo declarations
    private Servo servo1;
    private Servo servo2;
    private Servo latch;
    //names of above servos for identification
    //!!! THESE MUST MATCH THE NAMES IN THE ROBOT CONFIG FILE !!!
    final static String servo1_name = "servo1";
    final static String servo2_name = "servo2";
    final static String latch_name = "latch";

    // End abstraction -----------------------------------------------------------------------------


    public testOp() {}
    @Override public void init() {
        //we let the whole thing crash if any drive motor doesn't work, because they are absolutely essential
        right_drive = hardwareMap.dcMotor.get(right_drive_name);
        left_drive = hardwareMap.dcMotor.get(left_drive_name);
        right_drive_front = hardwareMap.dcMotor.get(right_front_name);
        left_drive_front = hardwareMap.dcMotor.get(left_front_name);
        //however, the lift fails gracefully (maybe) because:
        //I expect that motor controller to disconnect randomly
        //We can run without it
        try {
            lift = hardwareMap.dcMotor.get(lift_name);
        } catch (Exception p_exception) {
            lift = null;
        }

        servo1 = hardwareMap.servo.get(servo1_name);
        servo2 = hardwareMap.servo.get(servo2_name);
        latch = hardwareMap.servo.get(latch_name);

        //set encoder runmode for right drive
        if (right_drive != null) {
            if (!right_drive_enc) {
                if (left_drive.getChannelMode() == DcMotorController.RunMode.RESET_ENCODERS)
                    right_drive.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            } else {
                right_drive.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
            }
        }

        if (right_drive_front != null) {
            right_drive_front.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        }

        //set encoder runmode for left drive
        if (left_drive != null) {
            if (!left_drive_enc) {
                if (left_drive.getChannelMode () == DcMotorController.RunMode.RESET_ENCODERS) {
                    left_drive.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
                }
            } else {
                left_drive.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
            }
        }

        if (left_drive_front != null) {
            left_drive_front.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        }

        if (lift != null) {
            lift.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        }

        right_drive.setDirection(right_direction);
        right_drive_front.setDirection(right_front_direction);
        left_drive.setDirection(left_direction);
        left_drive_front.setDirection(left_front_direction);

    }

    public void start() {
    }

    @Override public void loop() {
        //toggle switch for reversed drive mode
        if (reverse_button() && !last_reverse) {
            reverse = !reverse;
        }

        //set power
        set_drive_power();

        if (lift != null) {
            try {
                lift.setPower(scale_power(lift_pwr(), drive_scale_exp));
                telemetry.addData("lift_status", "running");
            } catch (Exception p_exception) {
                telemetry.addData("lift_status", "failed");
            }
        }

        //if servos are enabled, set servo positions and report this
        //servo stuff isn't quite as clean as everything else, maybe TODO
        if (has_servos) {
            if (extend_left()) {
                left_position = Range.clip(left_position + servo_increment, servo_deploy, servo_retract);
                telemetry.addData("left servo", "extending: " + left_position);
            } else if (retract_left()) {
                left_position = Range.clip(left_position - servo_increment, servo_deploy, servo_retract);
                telemetry.addData("left servo", "retracting: " + left_position);
            }

            if (extend_right()) {
                right_position = Range.clip(right_position + servo_increment, servo_deploy, servo_retract);
                telemetry.addData("right servo", "extending: " + right_position);
            } else if (retract_right()) {
                right_position = Range.clip(right_position - servo_increment, servo_deploy, servo_retract);
                telemetry.addData("right servo", "retracting: " + right_position);
            }
        }

        //if latch is enabled, set latch positions and report them
        if (has_latch) {
            if (extend_latch()) {
                latch_position = Range.clip(latch_position - servo_increment, servo_deploy, servo_retract);
                telemetry.addData("latch", "extending: " + latch_position);
            } else if (retract_latch()) {
                latch_position = Range.clip(latch_position + servo_increment, servo_deploy, servo_retract);
                telemetry.addData("latch", "retracting: " + latch_position);
            }
        }

        //set servo positions
        servo1.setPosition(left_position);
        servo2.setPosition(right_position);
        latch.setPosition(latch_position);

        //report power
        telemetry.addData("pwrLD", left_drive.getPower());
        telemetry.addData("pwrRD", right_drive.getPower());
    }

    void set_drive_power(){
        //note that the right drive power is negative because on our robot the motors are placed in opposite directions

        if (reverse) {
            right_drive.setPower(-scale_power(left_drive_pwr(), drive_scale_exp));
            left_drive.setPower(-scale_power(right_drive_pwr(), drive_scale_exp));
            right_drive_front.setPower(-scale_power(left_drive_pwr(), drive_scale_exp));
            left_drive_front.setPower(-scale_power(right_drive_pwr(), drive_scale_exp));
        } else if (fine_drive()) {
            right_drive.setPower(scale_power(right_drive_pwr(), fine_drive_scale_exp) * fine_drive_max_power);
            left_drive.setPower(scale_power(left_drive_pwr(), fine_drive_scale_exp) * fine_drive_max_power);
            right_drive_front.setPower(scale_power(right_drive_pwr(), fine_drive_scale_exp) * fine_drive_max_power);
            left_drive_front.setPower(scale_power(left_drive_pwr(), fine_drive_scale_exp) * fine_drive_max_power);
        } else {
            right_drive.setPower(scale_power(right_drive_pwr(), drive_scale_exp));
            left_drive.setPower(scale_power(left_drive_pwr(), drive_scale_exp));
            right_drive_front.setPower(scale_power(right_drive_pwr(), drive_scale_exp));
            left_drive_front.setPower(scale_power(left_drive_pwr(), drive_scale_exp));
        }
    }

    public void stop(){
    }

    double scale_power(double input, double exponent) {
        if (input >= 0) {
            return Math.pow(input, exponent);
        } else {
            return 0.0 - Math.pow(Math.abs(input), exponent);
        }
    }
}
