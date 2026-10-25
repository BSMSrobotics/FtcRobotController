package org.firstinspires.ftc.teamcode.mechanisms;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Flywheellogic {

    private Servo gateServo;
    private DcMotorEx bigFlyWheel;
    private DcMotorEx smallFlyWheel;

    private ElapsedTime stateTimer = new ElapsedTime();

    private enum FlywheelState {
        IDLE,
        SPIN_UP,
        LAUNCH,
        RESET_GATE
    }
    private FlywheelState flywheelState;

    //---------------gate constants---------
    private double GATE_CLOSED_POS = 0.0;
    private double GATE_OPEN_POS = 0.5;
    private double GATE_OPEN_TIME = 2.0;
    private double GATE_CLOSE_TIME = 0.7;


    //---------------flywheel constants---------
    private int shotsRemaining = 0;
    private double bigActualVelocity = 0;
    private double smallActualVelocity = 0;
    private double BigMinVelocity = 1000;
    private double SmallMinVelocity = 900;
    private double bigTargetflyVelocity = 1360;
    private double smallTargetflyVelocity = 1100;
    private double FLYWHEEL_MAX_SPINUP_TIME = .7;

    public void init(HardwareMap hwMap) {
        bigFlyWheel = hwMap.get(DcMotorEx.class, "big_flywheel");
        smallFlyWheel = hwMap.get(DcMotorEx.class, "small_flywheel");
        gateServo = hwMap.get(Servo.class, "gate_servo");
        gateServo.scaleRange(0.0, 0.5);

        bigFlyWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        smallFlyWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        bigFlyWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        smallFlyWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        bigFlyWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        smallFlyWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        bigFlyWheel.setDirection(DcMotorSimple.Direction.REVERSE);
        smallFlyWheel.setDirection(DcMotorSimple.Direction.FORWARD);

        PIDFCoefficients bigPIDF = new PIDFCoefficients(39.0, 0, 0, 17);
        PIDFCoefficients smallPIDF = new PIDFCoefficients(21.0, 0, 0, 17.5
        );

        bigFlyWheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, bigPIDF);
        smallFlyWheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, smallPIDF);

        gateServo.setPosition(GATE_CLOSED_POS);
        gateServo.setDirection(Servo.Direction.REVERSE);

        flywheelState = FlywheelState.IDLE;
        gateServo.setPosition(GATE_CLOSED_POS);
        bigFlyWheel.setVelocity(0);
        smallFlyWheel.setVelocity(0);

    }

    public void update() {
        switch (flywheelState) {
            case IDLE:
                if (shotsRemaining > 0) {
                    gateServo.setPosition(GATE_CLOSED_POS);
                    bigFlyWheel.setVelocity(bigTargetflyVelocity);
                    smallFlyWheel.setVelocity(smallTargetflyVelocity);
                    flywheelState = FlywheelState.SPIN_UP;
                    stateTimer.reset();
                }
                break;
            case SPIN_UP:
                if (bigActualVelocity > BigMinVelocity || stateTimer.seconds() > FLYWHEEL_MAX_SPINUP_TIME) {
                    gateServo.setPosition(GATE_OPEN_POS);
                    flywheelState = FlywheelState.LAUNCH;
                    stateTimer.reset();
                }
                break;

            case LAUNCH:
                if (stateTimer.seconds() > GATE_OPEN_TIME) {
                    shotsRemaining --; //incriment by -1
                    gateServo.setPosition(GATE_CLOSED_POS);
                    flywheelState = FlywheelState.RESET_GATE;
                    stateTimer.reset();
                }
                break;

            case RESET_GATE:
                if (stateTimer.seconds() > GATE_CLOSE_TIME) {
                    if (shotsRemaining > 0) {
                        stateTimer.reset();
                        flywheelState = FlywheelState.SPIN_UP;
                    } else {
                        bigFlyWheel.setVelocity(0);
                        smallFlyWheel.setVelocity(0);
                        flywheelState = FlywheelState.IDLE;
                    }
                }
                break;

        }
    }

    public void fireshots(int numberOfShots){
        if(flywheelState == FlywheelState.IDLE){
            shotsRemaining = numberOfShots;

        }
    }
    public boolean isBusy(){
        return flywheelState != FlywheelState.IDLE;
    }

}
