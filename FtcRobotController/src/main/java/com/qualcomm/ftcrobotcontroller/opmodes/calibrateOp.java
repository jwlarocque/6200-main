package com.qualcomm.ftcrobotcontroller.opmodes;

//import
import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * FTC team 6200
 * Opmode for calibrating the motor encoders (it's not magic you have to measure)
 * Created by john on 11/3/15.
 */
public class calibrateOp extends OpMode {
    final static double final_encoder_position = 1.0;

    //leave these as false because I didn't actually write anything for encoders
    boolean right_drive_enc = false;
    boolean left_drive_enc = false;

    //motor declarations
    private DcMotor right_drive;
    private DcMotor left_drive;

    public calibrateOp(){}
    @Override public void init() {
        right_drive = hardwareMap.dcMotor.get("right");
        left_drive = hardwareMap.dcMotor.get("left");

        right_drive.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        left_drive.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
    }

    public void start(){
    }

    @Override public void loop(){
        if (right_drive.getCurrentPosition() < final_encoder_position && left_drive.getCurrentPosition() < final_encoder_position) {
            right_drive.setPower(1.0);
            left_drive.setPower(1.0);
        }
        else {
            right_drive.setPower(0.0);
            left_drive.setPower(0.0);
        }
        /*
        if (right_drive .getCurrentPosition() > -final_encoder_position && left_drive.getCurrentPosition() < final_encoder_position) {
            right_drive.setPower(-1.0);
            left_drive.setPower(1.0);
        }
        else {
            right_drive.setPower(0.0);
            left_drive.setPower(0.0);
        }
         */
        telemetry.addData("left_position", left_drive.getCurrentPosition());
        telemetry.addData("right_position", right_drive.getCurrentPosition());
    }

    public void stop(){
    }
}
