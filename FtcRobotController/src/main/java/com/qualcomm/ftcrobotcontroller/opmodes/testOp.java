package com.qualcomm.ftcrobotcontroller.opmodes;

//import
import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Test operating mode for FTC team 6200
 * Created by john on 10/5/15.
 */
public class testOp extends OpMode {

    //abstraction layer (quick settings) (there might be a better way to do this)
    double left_drive_pwr() {return -gamepad1.left_stick_y;}
    double right_drive_pwr() {return -gamepad1.right_stick_y;}
    // vvv leave these as false because I didn't actually write anything for encoders
    boolean right_drive_enc = false;
    boolean left_drive_enc = false;
    //this is the exponential scaling for stick input, might change that algorithm
    double drive_scale_exp = 1.4;

    //motor declarations
    private DcMotor right_drive;
    private DcMotor left_drive;
    private DcMotor pseudo_left;
    private DcMotor pseudo_right;

    public testOp(){}
    @Override public void init(){
        right_drive = hardwareMap.dcMotor.get("right");
        left_drive = hardwareMap.dcMotor.get("left");
        pseudo_right = hardwareMap.dcMotor.get("psr");
        pseudo_left = hardwareMap.dcMotor.get("psl");

        //set encoder runmode for right drive
        if(right_drive != null) {
            if (!right_drive_enc) {
                if (left_drive.getChannelMode() == DcMotorController.RunMode.RESET_ENCODERS)
                    right_drive.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
                }
            } else {
                right_drive.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
            }

        //set encoder runmode for left drive
        if(left_drive != null) {
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
        set_right_drive_power(scale_power(right_drive_pwr()));
        set_left_drive_power(scale_power(left_drive_pwr()));

        //report power
        telemetry.addData("pwrLD", left_drive.getPower());
        telemetry.addData("pwrRD", right_drive.getPower());
    }

    void set_right_drive_power(double power){
        right_drive.setPower(power);
        pseudo_right.setPower(power);
    }

    void set_left_drive_power(double power){
        left_drive.setPower(power);
        pseudo_left.setPower(power);
    }

    public void stop(){
    }

    double scale_power(double input) {
        return scale_power(input, drive_scale_exp);
    }

    double scale_power(double input, double exponent) {
        if(input >= 0) {
            return Math.pow(input, exponent);
        }
        else {
            return 0.0 - Math.pow(Math.abs(input), exponent);
        }
    }

}
