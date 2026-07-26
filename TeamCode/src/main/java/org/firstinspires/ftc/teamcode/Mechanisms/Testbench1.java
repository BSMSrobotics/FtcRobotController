package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Testbench1 {
    private DigitalChannel touchSensor;
    private DcMotor motor; // FrontLeftDriveMotor
    private double ticksPerRev;

    public void init(HardwareMap hwMap) {
        //touch sensor
        touchSensor = hwMap.get(DigitalChannel.class, "touch_sensor");
        touchSensor.setMode(DigitalChannel.Mode.INPUT);
        //dc motor

        motor = hwMap.get(DcMotor.class, "motor");
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ticksPerRev = motor.getMotorType().getTicksPerRev();
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setDirection(DcMotorSimple.Direction.REVERSE);


    }

    // touch sensor
    public boolean isTouchSensorPressed(){
        return !touchSensor.getState();
    }
    public boolean isTouchSensorReleased() {
        return touchSensor.getState();
    }
    //dc motor
    public void setMotorSpeed(double speed) {
        //accepts -1.0 to 1.0
       motor.setPower(speed);
    }
    public double getMotorRevs(){
        return motor.getCurrentPosition() / ticksPerRev; //normalizing ticks to revolutions 2:1

    }
    public void setMotorZeroBehavior(DcMotor.ZeroPowerBehavior zeroBehavior){
        motor.setZeroPowerBehavior(zeroBehavior);
    }
}
