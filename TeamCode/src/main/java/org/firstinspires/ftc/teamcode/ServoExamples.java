package org.firstinspires.ftc.teamcode;

import androidx.core.widget.TextViewCompat;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Mechanisms.TestBenchServo;

@TeleOp
public class ServoExamples extends OpMode {
    TestBenchServo bench = new TestBenchServo();
    double leftTrigger, rightTrigger;

    @Override
    public void init(){
        bench.init(hardwareMap);
        leftTrigger = 0.0;
        rightTrigger = 0.0;
    }

    @Override
    public void loop() {
        leftTrigger = gamepad1.left_trigger;
        rightTrigger = gamepad1.right_trigger;

        bench.setServoRot(rightTrigger);
        bench.setServoPos(leftTrigger);

    }
}
