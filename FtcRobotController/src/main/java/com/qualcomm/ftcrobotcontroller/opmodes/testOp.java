package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by john on 10/5/15.
 */
public class testOp extends OpMode {

    public testOp(){}
    @Override public void init(){

        v_motor_left_drive = hardwareMap.dcMotor.get("left");
        v_motor_right_drive = hardwareMap.dcMotor.get("right");

        v_motor_left_drive.setChannelMode
                ( DcMotorController.RunMode.RUN_WITHOUT_ENCODERS
                );
        v_motor_right_drive.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
    }

    public void start(){
    }
    @Override public void loop(){
        set_left_drive_power (-gamepad1.left_stick_y);
        set_right_drive_power (-gamepad1.right_stick_y);
        update_telemetry ();
    }

    double a_left_drive_power ()
    {
        double l_return = 0.0;

        if (v_motor_left_drive != null)
        {
            l_return = v_motor_left_drive.getPower () + 0.01;
        }

        return l_return;
    }
    void set_left_drive_power(double power){
        v_motor_left_drive.setPower(power);
    }
    void set_right_drive_power(double power){
        v_motor_right_drive.setPower(power);
    }

    public void update_telemetry ()

    {
        telemetry.addData
                ( "01"
                        , "Left Drive: "
                                + a_left_drive_power ()
                                + ", "
                );
        telemetry.addData("02", "connection: " + v_motor_left_drive.getConnectionInfo() + " controller: " + v_motor_left_drive.getController() + " port: " + v_motor_left_drive.getPortNumber() + " name: " + v_motor_left_drive.getDeviceName());
    }

    public void stop(){
    }

    private DcMotor v_motor_left_drive;
    private DcMotor v_motor_right_drive;

}
