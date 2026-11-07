package org.firstinspires.ftc.teamcode.components.opmodes.teleops;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.arcrobotics.ftclib.command.Command;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.teamcode.components.commands.indexCommand;
import org.firstinspires.ftc.teamcode.components.commands.intakeCommand;
import org.firstinspires.ftc.teamcode.components.commands.shooterCommand;
import org.firstinspires.ftc.teamcode.components.subsystems.driveSubsytem;
import org.firstinspires.ftc.teamcode.components.subsystems.indexSubsystem;
import org.firstinspires.ftc.teamcode.components.subsystems.intakeSubsytem;
import org.firstinspires.ftc.teamcode.components.subsystems.shooterSubsytem;

@TeleOp(name = "Tele Op")
public class teleop extends CommandOpMode{

    private GamepadEx driveController;
    private GamepadEx manipulatorController;

    private driveSubsytem driveSubsystem;
    private IMU imu;
    private intakeSubsytem intakeSubsystem;
    private indexSubsystem indexSubsystem;
    private shooterSubsytem shooterSubsystem;


    private indexCommand indexCommand;
    private indexCommand indexReverseCommand;

    private shooterCommand shooterRPMCommand;
    private shooterCommand shooterPowerCommand;


    private intakeCommand intakeForwardCommand;
    private intakeCommand intakeReverseCommand;



    private boolean indexRunningForward = false;
    private boolean indexRunningReverse = false;
    private boolean shooterRunning = false;
    private boolean sshooterRunning = false;
    private boolean intakeRunning = false;
    private boolean intakeForward = true; // true = forward, false = reverse
    private boolean useRPMControl = true;

    private double targetRPM = 300;   // Default shooting speed
    private double shooterPower = -0.99; // Backup open-loop power
    private double sshooterPower = -0.99;
    private double intakePower = 1.0;  // Default intake power
    private boolean intakeReverse = false;
    private boolean autoAimEnabled = false;
    private boolean lastRightTriggerState = false;
    private String LshooterMotorName;
    private Command LshooterPowerCommand;


    @Override
    public void initialize() {

        CommandScheduler.getInstance().reset();

        driveController = new GamepadEx(gamepad1);
        manipulatorController = new GamepadEx(gamepad2);

        // Initialize subsystems
        driveSubsystem = new driveSubsytem(hardwareMap, "FLDrive", "BLDrive", "FRDrive", "BRDrive", "imu");
        driveSubsystem.initializeDrive();

        imu = hardwareMap.get(IMU.class, "imu");
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.FORWARD,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT
        ));
        imu.initialize(parameters);

        intakeSubsystem = new intakeSubsytem(hardwareMap, "intake");
        indexSubsystem = new indexSubsystem(hardwareMap, "index");
        shooterSubsystem = new shooterSubsytem(hardwareMap, "Rshooter", "Lshooter");



        // Index commands
        indexCommand = new indexCommand(indexSubsystem, 1.0);
        indexReverseCommand = new indexCommand(indexSubsystem, -1.0);

        // Shooter commands
        shooterRPMCommand = new shooterCommand(shooterSubsystem, targetRPM);
        shooterPowerCommand = new shooterCommand(shooterSubsystem, shooterPower, false);

        shooterCommand LshooterRPMCommand = new shooterCommand(shooterSubsystem, targetRPM);
        shooterCommand LshooterPowerCommand = new shooterCommand(shooterSubsystem, shooterPower, false);


        // Intake commands
        intakeForwardCommand = new intakeCommand(intakeSubsystem, -intakePower);
        intakeReverseCommand = new intakeCommand(intakeSubsystem, intakePower);





    }

    @Override
    public void run() {
        CommandScheduler.getInstance().run();

        // --- FIELD-CENTRIC DRIVE ---
        if (gamepad1.start) driveSubsystem.resetYaw(); // re-zero orientation

        double x = driveController.getLeftX();
        double y = driveController.getLeftY();
        double rx = driveController.getRightX();


        driveSubsystem.fieldCentricDrive(x, y, rx);

        boolean triggerPressed = gamepad1.right_trigger > 0.5;

// toggle on/off

        lastRightTriggerState = triggerPressed;


        // --- Index Controls ---
        if (gamepad2.a && !indexRunningForward) {
            CommandScheduler.getInstance().schedule(indexCommand);
            indexRunningForward = true;
            indexRunningReverse = false;
        } else if (gamepad2.y && !indexRunningReverse) {
            CommandScheduler.getInstance().schedule (indexReverseCommand);
            indexRunningReverse = true;
            indexRunningForward = false;
        } else if (!gamepad2.a && !gamepad2.y) {
            if (indexCommand.isScheduled()) indexCommand.cancel();
            if (indexReverseCommand.isScheduled()) indexReverseCommand.cancel();
            indexSubsystem.stopIndex();
            indexRunningForward = false;
            indexRunningReverse = false;
        }

        // ---------------- BOTH SHOOTERS TOGGLE WITH TRIGGERS -------------------

        double rt = gamepad2.right_trigger;
        double lt = gamepad2.left_trigger;

// --- TURN BOTH ON (Right Trigger) ---
        if (rt > 0.5 && !shooterRunning && !sshooterRunning) {

            // MAIN SHOOTER
            if (useRPMControl) {
                shooterRPMCommand = new shooterCommand(shooterSubsystem, targetRPM);
                CommandScheduler.getInstance().schedule(shooterRPMCommand);
            } else {
                CommandScheduler.getInstance().schedule(shooterPowerCommand);
            }

            // SECONDARY SHOOTER
            if (useRPMControl) {
                shooterCommand sshooterRPMCommand = new shooterCommand(shooterSubsystem, targetRPM);
                CommandScheduler.getInstance().schedule(sshooterRPMCommand);
            } else {
                CommandScheduler.getInstance().schedule(LshooterPowerCommand);
            }


            shooterRunning = true;
            sshooterRunning = true;
        }

// --- TURN BOTH OFF (Left Trigger) ---
        if (lt > 0.5 && shooterRunning && sshooterRunning) {

            if (shooterRPMCommand.isScheduled()) shooterRPMCommand.cancel();
            if (shooterPowerCommand.isScheduled()) shooterPowerCommand.cancel();
            shooterSubsystem.stopShooter();



            shooterRunning = false;
            sshooterRunning = false;
        }


        if (gamepad2.x) {
            useRPMControl = !useRPMControl;
            sleep(300);
        }

        if (gamepad2.dpad_up) {
            targetRPM += 200;
            shooterRPMCommand = new shooterCommand(shooterSubsystem, targetRPM);
            sleep(200);
        } else if (gamepad2.dpad_down) {
            targetRPM = Math.max(1620, targetRPM - 250);
            shooterRPMCommand = new shooterCommand(shooterSubsystem, targetRPM);
            sleep(200);
        } else if (gamepad2.b) {
            targetRPM = 1620;
            shooterRPMCommand = new shooterCommand(shooterSubsystem, targetRPM);
            sleep(300);
        }

        // --- Intake Controls ---
        boolean rightBumper = gamepad2.right_bumper;
        boolean leftBumper = gamepad2.left_bumper;

        if (rightBumper && !intakeRunning) {
            CommandScheduler.getInstance().schedule(intakeForwardCommand);
            intakeRunning = true;
            intakeForward = true;
        } else if (leftBumper && !intakeRunning) {
            CommandScheduler.getInstance().schedule(intakeReverseCommand);
            intakeRunning = true;
            intakeReverse = false;
        } else if (!rightBumper && !leftBumper && intakeRunning) {
            if (intakeForwardCommand.isScheduled()) intakeForwardCommand.cancel();
            if (intakeReverseCommand.isScheduled()) intakeReverseCommand.cancel();
            intakeSubsystem.setIntake(0);
            intakeRunning = false;
        }


        // --- Turret Presets ---
//        if (gamepad1.y) {
//            schedule(new TurretSetAngleCommand(turretSubsystem, 0.0));
//        } else if (gamepad1.x) {
//            schedule(new TurretSetAngleCommand(turretSubsystem, -90.0));
//        } else if (gamepad1.b) {
//            schedule(new TurretSetAngleCommand(turretSubsystem, 90.0));
//        } else if (gamepad1.a) {
//            schedule(new TurretSetAngleCommand(turretSubsystem, 180.0));
//        }
//
//        if (gamepad1.back) {
//            turretSubsystem.zeroEncoder();
//        }

        // --- Telemetry ---


        telemetry.addData("Shooter Running", shooterRunning);
        telemetry.addData("Lshooter Running", sshooterRunning);
        telemetry.addData("Target RPM", targetRPM);
        telemetry.addData("Using RPM Control", useRPMControl);
        telemetry.addData("Current RPM", shooterSubsystem.getCurrentRPM());
        telemetry.addData("Index Forward", indexRunningForward);
        telemetry.addData("Index Reverse", indexRunningReverse);
        telemetry.addData("Intake Running", intakeRunning);
        telemetry.addData("Intake Direction", intakeForward ? "Forward" : "Reverse");
        telemetry.addData("Drive Mode", "FIELD-CENTRIC"); // always field-centric

        telemetry.addData("Auto Aim", autoAimEnabled);
        telemetry.update();
    }

    public double getSshooterPower() {
        return sshooterPower;
    }

    public void setSshooterPower(double sshooterPower) {
        this.sshooterPower = sshooterPower;
    }
}
