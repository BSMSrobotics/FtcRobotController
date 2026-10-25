package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous
public class SampleActionsTutorial extends OpMode {
    private Follower follower;
    private Timer pathtimer, opModeTimer;

    public enum PathState {
        //start position_end position
        //drive > movement state
        // shoot > attempt to score the artifact

        DRIVE_STARTPOS_SHOOT_POS,
        SHOOT_PRELOAD,
        DRIVE_SHOOTPOS_ENDPOS

    }
    PathState pathState;
    private final Pose startPose = new Pose(10,103,Math.toRadians(0));
    private final Pose start_shoot_controled = new Pose(17,39.5,Math.toRadians(0));
    private final Pose shootPose = new Pose(56,11,Math.toRadians(90));
    private final Pose shoot_end_controled = new Pose(17,39.5,Math.toRadians(0));

    private final Pose endPose = new Pose(10,103,Math.toRadians(0));

    private PathChain driveStartPosShoot,driveShootPosEndPos;
    public void buildPath(){
        driveStartPosShoot = follower.pathBuilder()
                .addPath(new BezierCurve(startPose, start_shoot_controled,shootPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading())
                .build();
        driveShootPosEndPos = follower.pathBuilder()
                .addPath(new BezierCurve(shootPose,shoot_end_controled,endPose))
                .setLinearHeadingInterpolation(shootPose.getHeading(), endPose.getHeading())
                .build();

    }

    public void statePathUpdate() {
        switch (pathState) {
            case DRIVE_STARTPOS_SHOOT_POS:
                follower.followPath(driveStartPosShoot, true);
                setPathState(PathState.SHOOT_PRELOAD); //reset timer and make new state
                break;
            case SHOOT_PRELOAD:
                // check if follower is done with path
                //and check that 5 sec has elasped
                if (!follower.isBusy() && pathtimer.getElapsedTimeSeconds() > 5) {
                    follower.followPath(driveShootPosEndPos, true);
                    setPathState(PathState.DRIVE_SHOOTPOS_ENDPOS);
                }
                break;
            case DRIVE_SHOOTPOS_ENDPOS:
                //all done
                if(!follower.isBusy()){
                    telemetry.addLine("done all paths");
                }
            default:
                telemetry.addLine("no state commanded");
                break;

        }
    }

    public void setPathState(PathState newState){
        pathState = newState;
        pathtimer.resetTimer();

    }


    @Override
    public void init(){
        pathState = PathState.DRIVE_STARTPOS_SHOOT_POS;
        pathtimer = new Timer();
        opModeTimer = new Timer();
        follower = Constants.createFollower(hardwareMap);
        // TODO add in any other init mechinisms

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
        statePathUpdate();

        telemetry.addData("path state", pathState.toString());
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData( "heading", follower.getPose().getHeading());
        telemetry.addData("Path time", pathtimer.getElapsedTimeSeconds());


    }
}

