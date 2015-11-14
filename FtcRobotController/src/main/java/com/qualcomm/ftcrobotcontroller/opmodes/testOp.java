package com.qualcomm.ftcrobotcontroller.opmodes;

//import
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Test operating mode for FTC team 6200
 * Created by John LaRocque on 10/5/15.
 * TODO implement navigation/awareness/sensor fusion
 */
public class testOp extends OpMode {

    //!!! abstraction layer (quick settings) !!! ---------------------------------------------------

    //control abstraction
    double left_drive_pwr() {return gamepad1.left_stick_y;}
    double right_drive_pwr() {return -gamepad1.right_stick_y;}
    boolean extend_left() {if (separate_servos) {return gamepad2.left_bumper;} return gamepad1.a;}
    boolean retract_left() {if (separate_servos) {return gamepad2.left_trigger > 0.0;} return gamepad1.b;}
    //first input is for seperate servo input, second is otherwise
    boolean extend_right() {if (separate_servos) {return gamepad2.right_bumper;} return gamepad1.a;}
    boolean retract_right() {if (separate_servos) {return gamepad2.right_trigger > 0.0;} return gamepad1.b;}
    boolean extend_latch() {return gamepad2.a;}
    boolean retract_latch() {return gamepad2.b;}


    //leave these as false because I didn't actually write anything for encoders
    boolean right_drive_enc = false;
    boolean left_drive_enc = false;
    //this is the exponential scaling for stick input, do not change this much
    final static double drive_scale_exp = 1.4;

    //motor declarations
    private DcMotor right_drive;
    private DcMotor left_drive;
    //names of above motors for identification
    //!!! THESE MUST MATCH THE NAMES IN THE ROBOT CONFIG FILE !!!
    final static String right_drive_name = "right";
    final static String left_drive_name = "left";

    //should we attempt to use servos? (currently: servo1 and servo2)
    boolean servos = true;
    //should we control the left and right servos separately? (see gamepad inputs above)
    boolean separate_servos = true;
    //should we attempt to use the latch servo?
    boolean has_latch = true;
    //starting positions of servos, with current hardware configuration, these should be the same
    double left_position = 0.8;
    double right_position = 0.8;
    //starting position of latch servo
    double latch_position = 0.1;
    //position of servos in deployed state (this value has no units, idk how it works)
    final static double servo_deploy = 0.0;
    //position of servos in retracted state
    final static double servo_retract = 1.0;
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


    public testOp(){}
    @Override public void init(){
        right_drive = hardwareMap.dcMotor.get(right_drive_name);
        left_drive = hardwareMap.dcMotor.get(left_drive_name);

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
    }

    public void start(){
    }

    @Override public void loop(){
        //set power
        set_drive_power();

        // if servos are enabled, set servo positions and report this
        if (servos) {
            if (extend_left()) {
                left_position = Range.clip(left_position - servo_increment, servo_deploy, servo_retract);
                telemetry.addData("servos", "extending");
            }
            else if (retract_left()) {
                left_position = Range.clip(left_position + servo_increment, servo_deploy, servo_retract);
                telemetry.addData("servos", "retracting");
            }
            if (extend_right()) {
                right_position = Range.clip(right_position - servo_increment, servo_deploy, servo_retract);
                telemetry.addData("servos", "extending");
            }
            else if (retract_right()) {
                right_position = Range.clip(right_position + servo_increment, servo_deploy, servo_retract);
                telemetry.addData("servos", "retracting");
            }
        }

        //if latch is enabled, set latch positions
        if (has_latch) {
            if (extend_latch()) {
                latch_position = Range.clip(latch_position - servo_increment, servo_deploy, servo_retract);
                telemetry.addData("latch", "extending");
            }
            else if (retract_latch()) {
                latch_position = Range.clip(latch_position + servo_increment, servo_deploy, servo_retract);
                telemetry.addData("latch", "retracting");
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
        right_drive.setPower(scale_power(right_drive_pwr()));
        left_drive.setPower(scale_power(left_drive_pwr()));
    }

    public void stop(){
    }

    double scale_power(double input) {
        return scale_power(input, drive_scale_exp);
    }

    double scale_power(double input, double exponent) {
        if (input >= 0) {
            return Math.pow(input, exponent);
        }
        else {
            return 0.0 - Math.pow(Math.abs(input), exponent);
        }
    }

}
