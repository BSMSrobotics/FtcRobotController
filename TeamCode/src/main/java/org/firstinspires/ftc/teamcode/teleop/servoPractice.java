package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.servo;

@TeleOp
public class servoPractice extends OpMode {
    servo gate = new servo();
    @Override
    public void init() {
        gate.init(hardwareMap);
    }

    @Override
    public void loop() {
        if (gamepad1.a) {
        gate.setServoPos(0);
        }
        else {
            gate.setServoPos(1);
        }

    }
}
