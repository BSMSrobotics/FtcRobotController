package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class sensors {

    private DigitalChannel intakeTouchSensor;

    public void init(HardwareMap hwMap){
        intakeTouchSensor = hwMap.get(DigitalChannel.class,"intake_touch_sensor");
        intakeTouchSensor.setMode(DigitalChannel.Mode.INPUT);


    }

    public boolean getIntakeTouchSensorState(){
        return intakeTouchSensor.getState();
    }

}
