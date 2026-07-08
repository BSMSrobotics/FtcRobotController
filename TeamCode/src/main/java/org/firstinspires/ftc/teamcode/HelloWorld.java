package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Disabled
public class HelloWorld extends OpMode {

    @Override
    public void init() {
        telemetry.addData("Hello","Chris");
    }

    @Override
    public void loop() {

    }
    //single line comment

    /*
    block comment

     */
}
