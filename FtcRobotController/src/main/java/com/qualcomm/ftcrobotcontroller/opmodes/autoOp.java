package com.qualcomm.ftcrobotcontroller.opmodes;

//import
import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Test autonomous operating mode for FTC team 6200, blue team
 * Created by john on 12/12/15.
 */
public class autoOp extends OpMode {
    // goes straight.
    double time_current = 0;
    double time_last = 0;

    //leave these as false because I didn't actually write anything for encoders
    boolean right_drive_enc = false;
    boolean left_drive_enc = false;

    //motor declarations
    private DcMotor right_drive;
    private DcMotor left_drive;
    private DcMotor right_drive_front;
    private DcMotor left_drive_front;

    final static DcMotor.Direction right_direction = DcMotor.Direction.REVERSE;
    final static DcMotor.Direction right_front_direction = DcMotor.Direction.REVERSE;
    final static DcMotor.Direction left_direction = DcMotor.Direction.FORWARD;
    final static DcMotor.Direction left_front_direction = DcMotor.Direction.FORWARD;

    public autoOp(){}
    @Override public void init(){
        right_drive = hardwareMap.dcMotor.get("right");
        left_drive = hardwareMap.dcMotor.get("left");
        right_drive_front = hardwareMap.dcMotor.get("right_front");
        left_drive_front = hardwareMap.dcMotor.get("left_front");

        right_drive_front.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        left_drive_front.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

        right_drive.setDirection(right_direction);
        right_drive_front.setDirection(right_front_direction);
        left_drive.setDirection(left_direction);
        left_drive_front.setDirection(left_front_direction);

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
        time_last = time_current;
        time_current = this.time;

        update_offset();

        if (this.time <= 4.0) {
            right_drive.setPower(1.0);
            right_drive_front.setPower(1.0);
            left_drive.setPower(1.0);
            left_drive_front.setPower(1.0);
        } else {
            right_drive.setPower(0.0);
            right_drive_front.setPower(0.0);
            left_drive.setPower(0.0);
            left_drive_front.setPower(0.0);
        }
    }

    public void stop(){
    }

    public void update_offset() {

    }
}
