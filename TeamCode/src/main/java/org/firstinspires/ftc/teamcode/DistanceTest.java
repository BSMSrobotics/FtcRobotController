package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Mechanisms.Testbenchdistance;

@TeleOp
public class DistanceTest extends OpMode {
    Testbenchdistance bench = new Testbenchdistance();
    double distance;
    @Override
    public void init(){
        bench.init(hardwareMap);

    }

    @Override
    public void loop(){
        distance = bench.getDistance();

        if (distance <10){
            telemetry.addLine( "too close!");

        }
        telemetry.addData("Distance", bench.getDistance());

    }

}
/*
print "too close" if object is less than 10cm away
 */