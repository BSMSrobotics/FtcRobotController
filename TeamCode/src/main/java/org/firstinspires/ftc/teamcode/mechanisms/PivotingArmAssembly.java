package org.firstinspires.ftc.teamcode.mechanisms;


import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@Configurable
public class PivotingArmAssembly {
    //arm encoder
    private double ENCODER_TICKS_PER_REV = 4000.0;

    //arm angle relative to ground plane at rest
    private double ANGLE_DEG_AT_REST = -15.0;

    private static int MIN_POS_TICKS = 0;
    private static int MAX_POS_TICKS = 1500; //TODO tune this later

    private static double kP= 0;
    private static double kI=0;
    private static double kD=0;
    private static double kGrav = 0;
    private static double MAX_POWER = 0.98;
    //anti-wind up
    private static double MAX_I_SUM = 3000;
    private DcMotorEx pivotMotor;
    private TouchSensor magneticLimitSwitch;
    private int targetPosTicks=0;
    private int currentPosTicks=0;
    private double lastError = 0;
    private double integralSum = 0;
    private boolean wasAtMagLimit = false;
    private double power = 0;
    private ElapsedTime timer = new ElapsedTime();

    public void init(HardwareMap hwMap){
        pivotMotor = hwMap.get(DcMotorEx.class,"pivot_motor");
        pivotMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        pivotMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        pivotMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        pivotMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        magneticLimitSwitch = hwMap.get(TouchSensor.class, "arm_mag");
        targetPosTicks = 0;
        currentPosTicks = 0;

        timer.reset();

    }
    public void start(){
        timer.reset();
    }
    public void update(){
        double deltaTime = timer.seconds();
        timer.reset();

        currentPosTicks = getCurPosTicks();
        double error = targetPosTicks-currentPosTicks;
        //PID controller
        double pTerm = error * kP;
        integralSum = Range.clip(integralSum +error * deltaTime, -MAX_I_SUM,MAX_I_SUM);
        double iTerm = integralSum * kI;
        double dTerm = 0;
        if (deltaTime >0){
            dTerm = ((error-lastError)/deltaTime)*kD;
        }
        lastError = error;

        //gravity feed forward
        double ffTerm = kGrav * Math.cos(estimateAngleRadians());

        //control the motor
        power = pTerm + iTerm + dTerm + ffTerm;
        power = Range.clip(power, -MAX_POWER,MAX_POWER);
        pivotMotor.setPower(power);
    }

    public int getCurPosTicks(){
        return pivotMotor.getCurrentPosition();
    }
    public double estimateAngleRadians(){
        double degreesPerTick = 360.0 / ENCODER_TICKS_PER_REV;
        double degreesFromZeroTicks = currentPosTicks * degreesPerTick;
        return Math.toRadians(ANGLE_DEG_AT_REST + degreesFromZeroTicks);
    }
}