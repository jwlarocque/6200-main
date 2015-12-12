package com.qualcomm.ftcrobotcontroller.opmodes;

// import
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

/**
 * Driver controlled operating mode for calibration of motor encoders, for FTC team 6200
 * Created by John LaRocque on 12/8/15.
 *
 * This is the companion to BLUE and REDauto, and it is somewhat interesting.
 * The purpose of his opMode is twofold.  First, it is intended to simply allow calibration of
 * angular and linear_encoder_mult, to allow theoretical pathing with waypoint based only upon
 * measurement of the field.  However, it is also possible (once angular_encoder_mult is correctly
 * and precisely defined) to directly create waypoint lists with this opMode, by recording the
 * offsets it provides.  Probably.
 */
public class teleCalibrateOp extends OpMode {

    private DcMotor right_drive;
    private DcMotor left_drive;
    private DcMotor right_drive_front;
    private DcMotor left_drive_front;

    final static String right_drive_name = "right";
    final static String left_drive_name = "left";
    final static String right_front_name = "right_front";
    final static String left_front_name = "left_front";

    // TODO set linear encoder multiplier
    final static double linear_encoder_mult = 1;
    // degrees rotated with one increment of the encoder values
    //TODO set angular encoder multiplier
    final static double angular_encoder_mult = 1;

    double offset_x = 0.0; // position is relative
    double offset_y = 0.0;
    double heading = 90.0; // heading is absolute

    double left_encoder_last = 0.0;
    double left_encoder_current = 0.0;
    double right_encoder_last = 0.0;
    double right_encoder_current = 0.0;

    public teleCalibrateOp() {}
    @Override public void init() {
        right_drive = hardwareMap.dcMotor.get(right_drive_name);
        left_drive = hardwareMap.dcMotor.get(left_drive_name);
        right_drive_front = hardwareMap.dcMotor.get(right_front_name);
        left_drive_front = hardwareMap.dcMotor.get(left_front_name);

        right_drive.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        right_drive_front.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        left_drive.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        left_drive_front.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

        right_drive.setDirection(DcMotor.Direction.REVERSE);
        right_drive_front.setDirection(DcMotor.Direction.REVERSE);
        left_drive.setDirection(DcMotor.Direction.FORWARD);
        left_drive_front.setDirection(DcMotor.Direction.FORWARD);
    }

    public void start() {
    }

    @Override public void loop() {
        // set power
        left_encoder_last = left_encoder_current;
        right_encoder_last = right_encoder_current;

        left_encoder_current = left_drive.getCurrentPosition();
        right_encoder_current = right_drive.getCurrentPosition();

        update_offset();

        if (gamepad1.dpad_up) {
            left_drive.setPower(1.0);
            left_drive_front.setPower(1.0);
            right_drive.setPower(1.0);
            right_drive_front.setPower(1.0);
        } else if (gamepad1.dpad_right) {
            left_drive.setPower(1.0);
            left_drive_front.setPower(1.0);
            right_drive.setPower(-1.0);
            right_drive_front.setPower(-1.0);
        } else if (gamepad1.dpad_left) {
            left_drive.setPower(-1.0);
            left_drive_front.setPower(-1.0);
            right_drive.setPower(1.0);
            right_drive_front.setPower(1.0);
        } else if (gamepad1.dpad_left) {
            left_drive.setPower(-1.0);
            left_drive_front.setPower(-1.0);
            right_drive.setPower(-1.0);
            right_drive_front.setPower(-1.0);
        } else {
            left_drive.setPower(0.0);
            left_drive_front.setPower(0.0);
            right_drive.setPower(0.0);
            right_drive_front.setPower(0.0);
        }

        // report encoder positions and power
        telemetry.addData("left_position", -left_drive.getCurrentPosition());
        telemetry.addData("right_position", right_drive.getCurrentPosition());
        telemetry.addData("pwrLD", left_drive.getPower());
        telemetry.addData("pwrRD", right_drive.getPower());
    }

    public void update_offset() {
        if (left_encoder_current > left_encoder_last && right_encoder_current < right_encoder_last) {
            // we went forwards
            offset_x += Math.cos(heading) * ((left_encoder_current - left_encoder_last) + (right_encoder_current - right_encoder_last)) / 2 * linear_encoder_mult;
            offset_y += Math.sin(heading) * ((left_encoder_current - left_encoder_last) + (right_encoder_current - right_encoder_last)) / 2 * linear_encoder_mult;
        } else if (left_encoder_current < left_encoder_last && right_encoder_current > right_encoder_last) {
            // we went backwards
            offset_x += Math.cos(heading) * ((left_encoder_current - left_encoder_last) + (right_encoder_current - right_encoder_last)) / 2 * linear_encoder_mult;
            offset_y += Math.sin(heading) * ((left_encoder_current - left_encoder_last) + (right_encoder_current - right_encoder_last)) / 2 * linear_encoder_mult;
        } else if (left_encoder_current < left_encoder_last && right_encoder_current < right_encoder_last) {
            // we turned left
            heading -= ((left_encoder_current - left_encoder_last) + (right_encoder_current - right_encoder_last)) / 2 * angular_encoder_mult;
            heading = heading % 360.0;
        } else if (left_encoder_current > left_encoder_last && right_encoder_current > right_encoder_last) {
            // we turned right
            heading -= ((left_encoder_current - left_encoder_last) + (right_encoder_current - right_encoder_last)) / 2 * angular_encoder_mult;
            heading = heading % 360.0;
        }

        telemetry.addData("Heading", heading);
        telemetry.addData("Offset_X", offset_x);
        telemetry.addData("Offset_Y", offset_y);
    }
}
