package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class ArmLogic {
    sensors intakeSensor = new sensors();
    private DcMotorEx armLift;
    private DcMotorEx armExtend;
    private DcMotor intake = null;

    private ElapsedTime stateTimer = new ElapsedTime();

    private enum ArmState {
        IDLE,
        LIFT_EXTEND,
        RELEASE,
        RESET_LIFT

    }
    private ArmState armState;

    //-------arm constants---------
  private double ARM_HOME_ANGLE = 0;
  private double ARM_ACTUAL_ANGLE = 0;
  private double ARM_TARGET_ANGLE = 45;
  private double ARM_HOME_LENGTH = 0;
  private double ARM_TARGET_LENGTH = 800;
  private double EXTEND_TIME = 3;
  private double RETRACT_TIME = 3;

  //-----intake constants------
    private boolean pollenPresent;
    private double intakePower = 0;

    private void init(HardwareMap hwMap){
        intakeSensor.init(hwMap);
        armLift = hwMap.get(DcMotorEx.class, "pivot_motor");
        armExtend = hwMap.get(DcMotorEx.class,"arm_extend_motor");

        armLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        armExtend.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        intake = hwMap.get(DcMotorEx.class,"intake");
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setDirection(DcMotorSimple.Direction.REVERSE);

        //tune your PIDF
        armState = ArmState.IDLE;
        pollenPresent = intakeSensor.getIntakeTouchSensorState();

    }

    public void update(){
        switch (armState){
            case IDLE:
                if (pollenPresent = true){
                    intakePower = 0;
                    //raise and extend arm
                    stateTimer.reset();
                    armState= ArmState.LIFT_EXTEND;
                }
                break;
            case LIFT_EXTEND:
                //raise and extend arm
                if (ARM_ACTUAL_ANGLE >= ARM_TARGET_ANGLE || stateTimer.seconds() > EXTEND_TIME ){
                    intakePower = -1;
                    stateTimer.reset();

                    armState = ArmState.RELEASE;
                }
                break;
            case RELEASE:
                if (pollenPresent = false){
                    intakePower = 0;
                    //lower and retract arm
                    stateTimer.reset();

                    armState = ArmState.RESET_LIFT;
                }
                break;
            case RESET_LIFT:
                if (ARM_ACTUAL_ANGLE <= ARM_HOME_ANGLE || stateTimer.seconds() > RETRACT_TIME){

                    stateTimer.reset();
                    armState = ArmState.IDLE;
                }
                break;
        }
    }
    public boolean isBusy(){
        return armState != ArmState.IDLE;
    }





}
