import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.Mechanisms.MecanumDrive;

@Autonomous
public class LimeLightTest extends OpMode {

    // hardware configuration
    private Limelight3A limelight3A;
    MecanumDrive drive = new MecanumDrive();
    double forward, strafe, rotate;

    //variables
    private DcMotor intake = null;
    double intakePower;

    // GAIN TUNING CONSTANTS
    private final double Kp_STEER = 0.025;     // Adjusts angle alignment rotation
    private final double Kp_DRIVE = 0.04;      // Adjusts forward/backward speed

    // DISTANCE CRITERIA (Using Target Area 'ta' as % of camera frame)
    // 0% = undetected/far away, 100% = occupies entire frame
    private final double DESIRED_TARGET_AREA = 12.0;
    private final double AREA_THRESHOLD = 0.5;
    private final double STEER_THRESHOLD = 1.0;

    // MOTOR LIMITER
    private final double MAX_AUTO_POWER = 0.6;

    @Override
    public void init() {
        limelight3A = hardwareMap.get(Limelight3A.class, "limelight");
        limelight3A.pipelineSwitch(7); //7 is yellow ball
        drive.init(hardwareMap);
        intake = hardwareMap.get(DcMotorEx.class,"intake");
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setDirection(DcMotorSimple.Direction.REVERSE);

    }

    @Override
    public void start() {
        limelight3A.start();
    }

    @Override
    public void loop() {
        forward = -gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;

        LLResult llResult = limelight3A.getLatestResult();
        if (gamepad1.a && llResult != limelight3A.getLatestResult()){
            telemetry.addData("target x offset", llResult.getTx());
            telemetry.addData("target y offset", llResult.getTy());
            telemetry.addData("target area offset",llResult.getTa());
            double tx = llResult.getTx(); // Horizontal error (-30 to 30 deg)
            double ta = llResult.getTa(); // Area error (0% to 100% of image)

            // 1. Calculate Auto-Steer (Rotation)
            if (Math.abs(tx) > STEER_THRESHOLD) {
                rotate = tx * Kp_STEER;
            } else {
                rotate = 0;
            }
            // 2. Calculate Auto-Drive (Distance Control via Target Area)
            // If area is too small -> robot drives forward. If too large -> backs up.
            double areaError = DESIRED_TARGET_AREA - ta;
            if (Math.abs(areaError) > AREA_THRESHOLD) {
                forward = areaError * Kp_DRIVE;
            } else {
                forward = 0;
            }
            // Keep camera movements safe and predictable
            rotate = Math.max(-MAX_AUTO_POWER, Math.min(rotate, MAX_AUTO_POWER));
            forward  = Math.max(-MAX_AUTO_POWER, Math.min(forward, MAX_AUTO_POWER));

            // Keep strafe zeroed during auto-tracking so it drives directly at it
            strafe = 0;

            telemetry.addData("Limelight", "Tracking Active");
            telemetry.addData("Tx / Ta Errors", "Deg: %.1f | Area Diff: %.1f", tx, areaError);
        } else {
            telemetry.addData("Limelight", "Manual Control");

        }

        limelight3A.stop();
    }
}
