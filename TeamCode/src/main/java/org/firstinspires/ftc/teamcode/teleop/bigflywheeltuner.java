package org.firstinspires.ftc.teamcode.teleop;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@TeleOp
public class bigflywheeltuner extends OpMode {
    public DcMotorEx bigflywheelmotor;

    public double lowVelocity = 900;
    public double highVelocity = 1360;

    double curTargetVelocity = highVelocity;

    double F = 0;
    double P = 0;
    double[] stepSizes = {10.0,1.0,0.1,0.001,0.0001};

    int stepIndex = 1;


    @Override
    public void init(){
        bigflywheelmotor = hardwareMap.get(DcMotorEx.class,"big_flywheel");
        bigflywheelmotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bigflywheelmotor.setDirection(DcMotorSimple.Direction.REVERSE);
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P,0,0,F);
        bigflywheelmotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        telemetry.addLine("init complete");
    }

    @Override
    public void loop(){
//get all gamepad commands
        // set target velocity
        // update telemetry

        if(gamepad1.yWasPressed()){
            if (curTargetVelocity == highVelocity) {
                curTargetVelocity = lowVelocity;
            }else {curTargetVelocity = highVelocity;}
        }
        if (gamepad1.bWasPressed()){
            stepIndex = (stepIndex +1) % stepSizes.length;
        }

        if (gamepad1.dpadLeftWasPressed()){
            F -= stepSizes[stepIndex];
        }
        if (gamepad1.dpadRightWasPressed()){
            F += stepSizes[stepIndex];
        }

        if (gamepad1.dpadUpWasPressed()){
            P += stepSizes[stepIndex];
        }
        if (gamepad1.dpadDownWasPressed()){
            P -= stepSizes[stepIndex];
        }
        //set new PIDF coef
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P,0,0,F);
        bigflywheelmotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        //set velocity
        bigflywheelmotor.setVelocity(curTargetVelocity);
        double currentvel = bigflywheelmotor.getVelocity();
        double error = curTargetVelocity - currentvel;

        telemetry.addData("target velocity", curTargetVelocity);
        telemetry.addData("current velocity","%.2f", currentvel );
        telemetry.addData("error", "%.2f",error);
        telemetry.addLine("--------------------");
        telemetry.addData("tuning P", "%.4f",P);
        telemetry.addData("tuning F", "%.4f",F);
        telemetry.addData("step size", "%.4f", stepSizes[stepIndex]);

    }
}
