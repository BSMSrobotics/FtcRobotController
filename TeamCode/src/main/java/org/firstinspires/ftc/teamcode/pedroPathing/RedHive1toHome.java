package org.firstinspires.ftc.teamcode;


import android.os.Looper;
import android.os.Handler;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.mechanisms.Flywheellogic;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous
public class RedHive1toHome extends OpMode {
    private Follower follower;
    private Timer pathtimer, opModeTimer;
    private DcMotor intake = null;
    double intakePower;

    //-----flywheel logic-----
    private Flywheellogic shooter = new Flywheellogic();
    private boolean shotsTriggered = false;

    public enum PathState {
        //start position_end position
        //drive > movement state
        // shoot > attempt to score the artifact

        PAUSE,
        DRIVE_TO_TARGET,
        SHOOT_PRELOAD,
        DRIVE_SHOOTPOS_ENDPOS



    }
    PathState pathState;
    private final Pose startPose = new Pose(59,144,Math.toRadians(-90));
    private final Pose start_shoot_controled = new Pose(59,120,Math.toRadians(0));
    private final Pose shootPose = new Pose(12,134,Math.toRadians(90));
    private final Pose shoot_end_controled = new Pose(17,39.5,Math.toRadians(0));
    private final Pose endPose = new Pose(10,101,Math.toRadians(90));




    private PathChain driveStartPosShoot,driveShootPosEndPos,driveHomePos;
    public void buildPath(){
        driveStartPosShoot = follower.pathBuilder()
                .addPath(new BezierCurve(startPose,start_shoot_controled ,shootPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading())
                .build();
        driveShootPosEndPos = follower.pathBuilder()
                .addPath(new BezierLine(shootPose,endPose))
                .setLinearHeadingInterpolation(shootPose.getHeading(), endPose.getHeading())
                .build();


    }

    public void statePathUpdate() {


        switch (pathState) {
            case PAUSE:new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 10000);

            /*case PAUSE:

                if (pathtimer.getElapsedTime() >= 10000)
                    setPathState(PathState.DRIVE_TO_TARGET);
                break;*/

            case DRIVE_TO_TARGET:
                follower.followPath(driveStartPosShoot, true);
                setPathState(PathState.SHOOT_PRELOAD); //reset timer and make new state
                break;

            case SHOOT_PRELOAD:
                // check if follower is done with path
                if (!follower.isBusy()) {
                    //requested shots yet?
                    if (!shotsTriggered) {
                        shooter.fireshots(4);
                        shotsTriggered = true;
                    } else if (shotsTriggered && !shooter.isBusy()) {
                        //shots are done free to transition
                        follower.followPath(driveShootPosEndPos, true);
                        setPathState(PathState.DRIVE_SHOOTPOS_ENDPOS);
                    }
                }
                break;

            case DRIVE_SHOOTPOS_ENDPOS:
                //all done
                if (!follower.isBusy()) {
                    telemetry.addLine("done all paths");
                }
                break;

            default:
                telemetry.addLine("no state commanded");
                break;
        }
    }

    public void setPathState(PathState newState){
        pathState = newState;
        pathtimer.resetTimer();

        shotsTriggered = false;

    }


    @Override
    public void init(){
        intake = hardwareMap.get(DcMotorEx.class,"intake");
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setDirection(DcMotorSimple.Direction.REVERSE);
        pathState = PathState.DRIVE_TO_TARGET;
        pathtimer = new Timer();
        opModeTimer = new Timer();
        follower = Constants.createFollower(hardwareMap);
        // TODO add in any other init mechinisms
        shooter.init(hardwareMap);

        buildPath();
        follower.setPose(startPose);


    }

    public void start(){
        opModeTimer.resetTimer();
        setPathState(pathState);

    }
    @Override
    public void loop(){
        follower.update();
        shooter.update();
        statePathUpdate();

        telemetry.addData("path state", pathState.toString());
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData( "heading", follower.getPose().getHeading());
        telemetry.addData("Path time", pathtimer.getElapsedTimeSeconds());


    }
}