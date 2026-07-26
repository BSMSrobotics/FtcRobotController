package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Testbench {

    private DigitalChannel touchSensor;

    public void init(HardwareMap hwMap){
        touchSensor = hwMap.get(DigitalChannel.class, "touch_sensor");
        touchSensor.setMode(DigitalChannel.Mode.INPUT);

    }
    public boolean isTouchSensorPressed(){
        return !touchSensor.getState();

    }
    public boolean isTouchSensorReleased(){
        return touchSensor.getState();

    }
}
