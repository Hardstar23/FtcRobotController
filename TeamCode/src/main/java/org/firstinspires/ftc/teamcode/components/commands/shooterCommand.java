package org.firstinspires.ftc.teamcode.components.commands;

import com.arcrobotics.ftclib.command.CommandBase;

import org.firstinspires.ftc.teamcode.components.subsystems.shooterSubsytem;

public class shooterCommand extends CommandBase {

    private final shooterSubsytem shooterSubsystem;
    private final double targetRPM;
    private final double RshooterPower;
    private double LshooterPower;
    private final boolean useRPMControl;

    // Closed-loop RPM control
    public shooterCommand(shooterSubsytem subsystem, double targetRPM) {
        shooterSubsystem = subsystem;
        this.targetRPM = targetRPM;
        this.RshooterPower = 0.99;
        this.LshooterPower = -0.99;
        this.useRPMControl = true;

        addRequirements(subsystem);
    }

    // Open-loop power control
    public shooterCommand(shooterSubsytem subsystem, double shooterPower, boolean useRPMControl) {
        shooterSubsystem = subsystem;
        this.RshooterPower = shooterPower;
        this.targetRPM = 1620; // default nominal RPM
        this.useRPMControl = useRPMControl;

        addRequirements(subsystem);
    }

    @Override
    public void initialize() {
        if (useRPMControl) {
            shooterSubsystem.setShooterRPM(targetRPM);
        } else {
            shooterSubsystem.setShooterPower(RshooterPower);
        }
    }


    @Override
    public void end(boolean interrupted) {
        shooterSubsystem.stopShooter();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    public double getLshooterPower() {
        return LshooterPower;
    }

    public void setLshooterPower(double LshooterPower) {
        this.LshooterPower = LshooterPower;
    }
}
