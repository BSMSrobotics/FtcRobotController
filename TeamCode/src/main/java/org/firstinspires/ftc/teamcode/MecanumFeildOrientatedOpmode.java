package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.Mechanisms.MecanumDrive;

@TeleOp
public class MecanumFeildOrientatedOpmode extends OpMode {
    MecanumDrive drive = new MecanumDrive();
    double forward, strafe, rotate;

    private DcMotor intake = null;
    double intakePower;


    @Override
    public void init(){
       drive.init(hardwareMap);
       intake = hardwareMap.get(DcMotorEx.class,"intake");
       intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
       intake.setDirection(DcMotorSimple.Direction.REVERSE);

    }

    @Override
    public void loop(){
       forward = -gamepad1.left_stick_y;
       strafe = gamepad1.left_stick_x;
       rotate = gamepad1.right_stick_x;

       drive.driveFieldRelative(forward,strafe,rotate);
       intakePower = gamepad1.right_trigger - gamepad1.left_trigger;
       intake.setPower(intakePower);

    }
}
