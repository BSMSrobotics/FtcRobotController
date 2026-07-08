package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerImpl;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class VariablePractice extends OpMode {
    @Override
    public void init() {
        int teamNumber = 23914;
        double motorSpeed = 0.75;
        boolean clawClosed = true;
        String teamName = "Phoenix Prime";
        int motorAngle = 90;

        telemetry.addData("Team Number", teamNumber);
        telemetry.addData("motor speed", motorSpeed);
        telemetry.addData("claw state", clawClosed);
        telemetry.addData("team name", teamName);
        telemetry.addData("motor angle",motorAngle);


    }
    @Override
    public void loop(){
        /*
        1. change the string variable name to your team name
        2.create an int called "motor angle" and store an angle between 0-180, display
         */
    }
}
