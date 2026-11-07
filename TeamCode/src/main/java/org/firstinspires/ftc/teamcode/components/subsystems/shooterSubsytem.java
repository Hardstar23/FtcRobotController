package org.firstinspires.ftc.teamcode.components.subsystems;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class shooterSubsytem extends SubsystemBase {
    private final DcMotorEx RshooterMotor;
    private final DcMotorEx LshooterMotor;  // <-- Added second motor

    // --- goBILDA 5204 Yellow Jacket 3.7:1 Motor ---
    private static final double TICKS_PER_REV = 103.8; // 28 * 3.7 (from goBILDA specs)
    private static final double GEAR_RATIO = 1.0;      // Adjust if extra gearing added
    private static final double RPM_TO_TICKS_PER_SEC = (TICKS_PER_REV * GEAR_RATIO) / 60.0;

    private double targetRPM = 800.0;

    public shooterSubsytem(final HardwareMap hMap, final String shooterName, String sshooterMotor) {
        RshooterMotor = hMap.get(DcMotorEx.class, shooterName);
        this.LshooterMotor = hMap.get(DcMotorEx.class, "sshooterMotor"); // <-- Added second motor

        RshooterMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        this.LshooterMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        RshooterMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.LshooterMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        RshooterMotor.setPower(0);
        this.LshooterMotor.setPower(0);
    }

    /** Set shooter RPM using built-in velocity PID for both motors */
    public void setShooterRPM(double rpm) {
        targetRPM = rpm;
        double ticksPerSecond = rpm * RPM_TO_TICKS_PER_SEC;

        RshooterMotor.setVelocity(ticksPerSecond);
        LshooterMotor.setVelocity(ticksPerSecond);
    }

    /** Open-loop control (direct power) */
    public void setShooterPower(double power) {
        targetRPM = 0;

        RshooterMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        LshooterMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        RshooterMotor.setPower(0.99);
        LshooterMotor.setPower(-0.99);
    }

    /** Stop shooter motors */
    public void stopShooter() {
        RshooterMotor.setPower(0);
        LshooterMotor.setPower(0);
        targetRPM = 0;
    }

    /** Get current RPM (Primary motor only) */
    public double getCurrentRPM() {
        double ticksPerSecond = RshooterMotor.getVelocity();
        return (ticksPerSecond / TICKS_PER_REV) * 60.0;
    }

    public double getTargetRPM() {
        return targetRPM;
    }

    @Override
    public void periodic() {}

    public void runShooter(double power) {
        RshooterMotor.setPower(0.99);
        LshooterMotor.setPower(-0.99);
    }

    private void runLshooterPower(double v) {

    }

    public void initializeRShooter() {

    }

    public void initializeLshooter() {

    }

    public void runLshooter(double v) {
    }

    public void stopLshooter() {
    }
}
