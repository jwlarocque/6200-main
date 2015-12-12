package com.qualcomm.ftcrobotcontroller.opmodes;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
// Created by  John LaRocque on 11/19/15.
// A demonstration opmode using only the drive motors
public class simpleOp extends OpMode {
    //control abstraction
    double left_drive_pwr() {return gamepad1.left_stick_y;}
    double right_drive_pwr() {return -gamepad1.right_stick_y;}
    //this is the exponent by which stick input is scaled
    final static double drive_scale_exp = 1.4;
    //motor declarations
    private DcMotor right_drive;
    private DcMotor left_drive;
    private DcMotor right_drive_front;
    private DcMotor left_drive_front;
    //names of above motors for identification
    //!!! THESE MUST MATCH THE NAMES IN THE ROBOT CONFIG FILE !!!
    final static String right_drive_name = "right";
    final static String left_drive_name = "left";
    final static String right_front_name = "right_front";
    final static String left_front_name = "left_front";
    @Override
    public void init() {
        right_drive = hardwareMap.dcMotor.get(right_drive_name);
        left_drive = hardwareMap.dcMotor.get(left_drive_name);
        right_drive_front = hardwareMap.dcMotor.get(right_front_name);
        left_drive_front = hardwareMap.dcMotor.get(left_front_name);
        right_drive.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        left_drive.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
    }
    @Override
    public void loop() {
        right_drive.setPower(scale_power(right_drive_pwr(), drive_scale_exp));
        left_drive.setPower(scale_power(left_drive_pwr(), drive_scale_exp));
        right_drive_front.setPower(scale_power(right_drive_pwr(), drive_scale_exp));
        left_drive_front.setPower(scale_power(left_drive_pwr(), drive_scale_exp));
        telemetry.addData("pwrLD", left_drive.getPower());
        telemetry.addData("pwrRD", right_drive.getPower());
    }
    double scale_power(double input, double exponent) {
        if (input >= 0)
            return Math.pow(input, exponent);
        else
            return 0.0 - Math.pow(Math.abs(input), exponent);
    }
}
