package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Mechanisms.Testbench1;
@TeleOp
public class DcMotorPractice extends OpMode {

    Testbench1 bench = new Testbench1();

    @Override
    public void init(){
        bench.init(hardwareMap);
    }
    @Override
    public void loop(){
        double motorSpeed = gamepad1.left_stick_y;
        bench.setMotorSpeed(motorSpeed);

        if(gamepad1.a){
            bench.setMotorZeroBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
        else if (gamepad1.b){
            bench.setMotorZeroBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
        telemetry.addData("motor revs", bench.getMotorRevs());
    }

}
/*
1. add method on bench to change brake behavior on opmode. a pressed set brake, b pressed set float
 */