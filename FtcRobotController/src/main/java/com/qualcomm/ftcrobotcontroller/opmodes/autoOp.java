package com.qualcomm.ftcrobotcontroller.opmodes;

//import
import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Test autonomous operating mode for FTC team 6200
 * Created by john on 11/3/15.
 */
public class autoOp extends OpMode {
    // vvv leave these as false because I didn't actually write anything for encoders
    boolean right_drive_enc = false;
    boolean left_drive_enc = false;
    //this is the exponential scaling for stick input, might change that algorithm
    double drive_scale_exp = 1.4;

    //motor declarations
    private DcMotor right_drive;
    private DcMotor left_drive;

    public autoOp(){}
    @Override public void init(){
        right_drive = hardwareMap.dcMotor.get("right");
        left_drive = hardwareMap.dcMotor.get("left");

        //set encoder runmode for right drive
        if (right_drive != null) {
            if (!right_drive_enc) {
                if (left_drive.getChannelMode() == DcMotorController.RunMode.RESET_ENCODERS)
                    right_drive.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            }
        } else {
            right_drive.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
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

        if (this.time <= 1) {
            right_drive.setPower(.15);
            left_drive.setPower(.15);
        }
        else if (this.time > 1 && this.time <= 2) {
            right_drive.setPower(.2);
            left_drive.setPower(-.2);
        }
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
