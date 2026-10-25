package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive;

@TeleOp
public class MecanumFeildOrientatedOpmode extends OpMode {
    MecanumDrive drive = new MecanumDrive();
    double forward, strafe, rotate;

    private DcMotor intake = null;
    double intakePower;

    private DcMotorEx bigFlyWheel;
    private DcMotorEx smallFlyWheel;
    double bigflyVelocity = 1360;
    double smallflyVelocity = 1100;
    private Servo gateServo;
    private Servo intakeServo;


    // Velocity error tolerance to consider the flywheel "at speed"
    private final double VELOCITY_TOLERANCE = 80.0;

    // Servo Positions (Adjust these 0.0 to 1.0 values based on your mechanical setup)
    private final double GATE_CLOSED_POS = 0.0;
    private final double GATE_OPEN_POS = 0.5;

    private final double INTAKE_CLOSED_POS = 0.0;
    private final double INTAKE_OPEN_POS = 0.5;




    @Override
    public void init() {
        drive.init(hardwareMap);
        intake = hardwareMap.get(DcMotorEx.class, "intake");
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setDirection(DcMotorSimple.Direction.REVERSE);

        bigFlyWheel = hardwareMap.get(DcMotorEx.class, "big_flywheel");
        smallFlyWheel = hardwareMap.get(DcMotorEx.class, "small_flywheel");
        gateServo = hardwareMap.get(Servo.class, "gate_servo");
        intakeServo = hardwareMap.get(Servo.class, "intake_servo");

        gateServo.scaleRange(0.0, 0.5);
        gateServo.setPosition(GATE_CLOSED_POS);
        gateServo.setDirection(Servo.Direction.REVERSE);

        intakeServo.scaleRange(0.0, 0.5);
        intakeServo.setPosition(INTAKE_CLOSED_POS);
        intakeServo.setDirection(Servo.Direction.REVERSE);


        bigFlyWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        smallFlyWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        bigFlyWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        smallFlyWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        bigFlyWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        smallFlyWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        bigFlyWheel.setDirection(DcMotorSimple.Direction.REVERSE);
        smallFlyWheel.setDirection(DcMotorSimple.Direction.FORWARD);

        PIDFCoefficients bigPIDF = new PIDFCoefficients(27.0, 0, 0, 12);
        PIDFCoefficients smallPIDF = new PIDFCoefficients(21.0, 0, 0, 17.5

        );

        bigFlyWheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, bigPIDF);
        smallFlyWheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, smallPIDF);




        telemetry.addData("Status", "Initialized. Ready to start.");
        telemetry.update();

    }

    @Override
    public void loop() {
        forward = -gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;

        drive.driveFieldRelative(forward, strafe, rotate);

        intakePower = gamepad1.right_trigger - gamepad1.left_trigger;
        intake.setPower(intakePower);

        if (gamepad1.right_trigger_pressed) {
            intakeServo.setPosition(INTAKE_OPEN_POS);
        } else {
            intakeServo.setPosition(INTAKE_CLOSED_POS);
        }


        if (gamepad1.x) {
            // Spin motors up to target
            bigFlyWheel.setVelocity(bigflyVelocity);
            smallFlyWheel.setVelocity(smallflyVelocity);

            // Fetch current actual velocities
            double currentSmallVel = smallFlyWheel.getVelocity();
            double currentBigVel = bigFlyWheel.getVelocity();
            // Calculate absolute error from target
            double bigError = Math.abs(bigflyVelocity - currentBigVel);
            double smallError = Math.abs(smallflyVelocity - currentSmallVel);

            // Open gate ONLY if both flywheels are stable within the tolerance window
            if (bigError <= VELOCITY_TOLERANCE) {
                gateServo.setPosition(GATE_OPEN_POS);
            } else {
                // Keep closed while revving up or recovering from a shot
                gateServo.setPosition(GATE_CLOSED_POS);
            }

        } else {
            // X is released: immediately command motors to 0 and close the gate
            bigFlyWheel.setVelocity(0);
            smallFlyWheel.setVelocity(0);
            gateServo.setPosition(GATE_CLOSED_POS);

        }
    }
}