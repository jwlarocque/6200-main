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
 * Created by john on 11/3/15.
 */
public class REDauto extends OpMode {
    // !!! There are NO curves in this autonomous, any behavior other than straight lines and
    // point turns (same power on each motor) will break the navigation
    // METERS are units (give


    // these are sample waypoints
    double[][] waypoints = {{1.0, 1.0}, {2.0, 2.0}};

    //this is stuff for navigation
    //this value is how far the robot goes when the encoder position increases by one
    //MEASURE PRECISELY
    //meters traveled with one increment of encoder value
    final static double linear_encoder_mult = 1;
    //degrees rotated with one increment of the encoder values
    final static double angular_encoder_vmult = 1;
    double offset_x = 0; // position is relative
    double offset_y = 0;
    double heading = 0; // heading is absolute, and initially 90
    double time_current = 0;
    double time_last = 0;

    //leave these as false because I didn't actually write anything for encoders
    boolean right_drive_enc = false;
    boolean left_drive_enc = false;

    //motor declarations
    private DcMotor right_drive;
    private DcMotor left_drive;

    public REDauto(){}
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
        time_last = time_current;
        time_current = this.time;

        update_offset();

        if (this.time <= 28.0) {
            navigate();
        }
    }

    public void stop(){
    }

    public void navigate() {
        // if
    }

    public void update_offset() {

    }
}
