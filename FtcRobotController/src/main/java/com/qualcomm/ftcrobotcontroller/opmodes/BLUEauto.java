package com.qualcomm.ftcrobotcontroller.opmodes;

// import
import java.lang.Math;
//import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

/**
 * Autonomous operating mode for FTC team 6200, blue team
 * Created by John LaRocque on 11/3/15.
 * TODO: set higher final waypoint precision?
 */
public class BLUEauto extends OpMode {
    /**
     * !!! There are NO curves in this autonomous, any behavior other than straight lines and
     *     point turns (same power on each motor) will break the navigation, deal with it
     * METERS are units
     * DEGREES are units (not that it matters)
     * left drive encoder is inverse to intuitive, because gears.  reversing the motor direction
     *     with setDirection changes the motor direction (obviously) but does not change the encoder
     *     direction.
     * waypoint position is RELATIVE, because I'm not a magician
     */

    // these are sample waypoints, feel free to set your own and comment stuff out
    // TODO set waypoints
    double[][] waypoints = {{30.0, 30.0}, {0.0, 0.0}};
    final static double waypoint_precision = 10.0;
    // this is stuff for navigation
    // this value is how far the robot goes when the encoder position increases by one
    // MEASURE PRECISELY
    // meters traveled with one increment of encoder value (and one decrement, ofc)
    // TODO set linear encoder multiplier
    final static double linear_encoder_mult = 0.005;
    // degrees rotated with one increment of the encoder values
    //TODO set angular encoder multiplier
    final static double angular_encoder_mult = 0.035;
    // look at all these variables to keep track of position...
    double offset_x = 0.0; // position is relative
    double offset_y = 0.0;
    double heading = 90.0; // heading is absolute
    double target_heading = 0.0;
    int current_waypoint = 0;
    double left_encoder_last = 0.0;
    double left_encoder_current = 0.0;
    double right_encoder_last = 0.0;
    double right_encoder_current = 0.0;
    boolean go = true;
    double turn_power  = 0.0;

    // motor declarations
    private DcMotor right_drive;
    private DcMotor left_drive;
    private DcMotor right_drive_front;
    private DcMotor left_drive_front;

    final static DcMotor.Direction right_direction = DcMotor.Direction.REVERSE;
    final static DcMotor.Direction right_front_direction = DcMotor.Direction.REVERSE;
    final static DcMotor.Direction left_direction = DcMotor.Direction.FORWARD;
    final static DcMotor.Direction left_front_direction = DcMotor.Direction.FORWARD;

    final static String right_drive_name = "right";
    final static String left_drive_name = "left";
    final static String right_front_name = "right_front";
    final static String left_front_name = "left_front";

    public BLUEauto(){}
    @Override public void init(){
        right_drive = hardwareMap.dcMotor.get(right_drive_name);
        left_drive = hardwareMap.dcMotor.get(left_drive_name);
        right_drive_front = hardwareMap.dcMotor.get(right_front_name);
        left_drive_front = hardwareMap.dcMotor.get(left_front_name);

        // set encoder runmode for right drive
        if (right_drive != null) {
            right_drive.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        }

        // set encoder runmode for left drive
        if (left_drive != null) {
            left_drive.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        }

        if (right_drive_front != null) {
            right_drive_front.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        }

        if (left_drive_front != null) {
            left_drive_front.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        }

        right_drive.setDirection(right_direction);
        right_drive_front.setDirection(right_front_direction);
        left_drive.setDirection(left_direction);
        left_drive_front.setDirection(left_front_direction);
    }

    public void start(){
    }

    @Override public void loop(){
        left_encoder_last = left_encoder_current;
        right_encoder_last = right_encoder_current;

        left_encoder_current = left_drive.getCurrentPosition();
        right_encoder_current = right_drive.getCurrentPosition();

        update_offset();

        if (this.time <= 28.0 && go) {
            navigate();
        }
        else {
            left_drive.setPower(0.0);
            left_drive_front.setPower(0.0);
            right_drive.setPower(0.0);
            right_drive_front.setPower(0.0);
        }
    }

    public void stop(){
    }

    public void navigate() {
        // if we're within waypoint_precision of the current waypoint, move to the next waypoint
        if (Math.pow(waypoints[current_waypoint][0] - offset_x, 2) + Math.pow(waypoints[current_waypoint][1] - offset_y, 2) < Math.pow(waypoint_precision, 2)) {
            if (current_waypoint >= waypoints.length - 1) {
                go = false;
                return;
            }
            current_waypoint++;
        }

        // calculate the heading necessary to point in the direction of the current waypoint
        if (waypoints[current_waypoint][0] > offset_x) {
            target_heading = Math.toDegrees(Math.atan((waypoints[current_waypoint][1] - offset_y) / (waypoints[current_waypoint][0] - offset_x))) % 360.0;
        }
        else {
            target_heading = (Math.toDegrees(Math.atan((waypoints[current_waypoint][1] - offset_y) / (waypoints[current_waypoint][0] - offset_x))) + 180.0) % 360.0;
        }

        telemetry.addData("Target Heading", target_heading);

        turn_power = 1.0;
        telemetry.addData("Turn Power", turn_power);

        // if heading isn't close, turn toward that waypoint
        if (Math.abs(heading - target_heading) > 20.0) {
            if (Math.abs(heading - target_heading) <= 180.0) {
                //turn right
                left_drive.setPower(turn_power);
                left_drive_front.setPower(turn_power);
                right_drive.setPower(-turn_power);
                right_drive_front.setPower(-turn_power);
            } else {
                //turn left
                left_drive.setPower(-turn_power);
                left_drive_front.setPower(-turn_power);
                right_drive.setPower(turn_power);
                right_drive_front.setPower(turn_power);
            }
        } else {
            // if heading is pretty close, we move toward the waypoint
            left_drive.setPower(1.0);
            left_drive_front.setPower(1.0);
            right_drive.setPower(1.0);
            right_drive_front.setPower(1.0);
        }
    }

    public void update_offset() {
        if (left_encoder_current > left_encoder_last && right_encoder_current > right_encoder_last) {
            // we went forwards
            offset_x += Math.cos(Math.toRadians(heading)) * ((left_encoder_current - left_encoder_last) + (right_encoder_current - right_encoder_last)) / 2 * linear_encoder_mult;
            offset_y += Math.sin(Math.toRadians(heading)) * ((left_encoder_current - left_encoder_last) + (right_encoder_current - right_encoder_last)) / 2 * linear_encoder_mult;
        } else if (left_encoder_current < left_encoder_last && right_encoder_current < right_encoder_last) {
            // we went backwards
            offset_x += Math.cos(Math.toRadians(heading)) * ((left_encoder_current - left_encoder_last) + (right_encoder_current - right_encoder_last)) / 2 * linear_encoder_mult;
            offset_y += Math.sin(Math.toRadians(heading)) * ((left_encoder_current - left_encoder_last) + (right_encoder_current - right_encoder_last)) / 2 * linear_encoder_mult;
        } else if (left_encoder_current < left_encoder_last && right_encoder_current > right_encoder_last) {
            // we turned left
            heading += (Math.abs(left_encoder_current - left_encoder_last) + Math.abs(right_encoder_current - right_encoder_last)) / 2 * angular_encoder_mult;
            heading = heading % 360.0;
        } else if (left_encoder_current > left_encoder_last && right_encoder_current < right_encoder_last) {
            // we turned right
            heading -= (Math.abs(left_encoder_current - left_encoder_last) + Math.abs(right_encoder_current - right_encoder_last)) / 2 * angular_encoder_mult;
            heading = heading % 360.0;
        }

        telemetry.addData("Heading", heading);
        telemetry.addData("Offset_X", offset_x);
        telemetry.addData("Offset_Y", offset_y);
        telemetry.addData("Left Encoder", left_encoder_current);
        telemetry.addData("Right Encoder", right_encoder_current);
    }

    public double turn_power() {
        if (Math.abs(heading - target_heading) <= 180.0) {
            return Math.abs(heading - target_heading) / 180;
        } else {
            return (180 - Math.abs(heading - target_heading - 180)) / 180;
        }
    }
}
