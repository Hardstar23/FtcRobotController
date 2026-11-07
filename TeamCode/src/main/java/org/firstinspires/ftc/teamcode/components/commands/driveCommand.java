package org.firstinspires.ftc.teamcode.components.commands;

import com.arcrobotics.ftclib.command.CommandBase;

import org.firstinspires.ftc.teamcode.components.subsystems.driveSubsytem;

public class driveCommand extends CommandBase {

    private final driveSubsytem m_subsystem;
    private double inputX, inputY, inputRX;
    private boolean fieldCentric;

    // For PID auto drive
    private double point;

    public driveCommand(driveSubsytem subsystem, double setpoint) {
        this.m_subsystem = subsystem;
        this.point = setpoint;
        addRequirements(subsystem);
    }

    // Overloaded constructor for field-centric teleop control
    public driveCommand(driveSubsytem subsystem, double x, double y, double rx, boolean fieldCentric) {
        this.m_subsystem = subsystem;
        this.inputX = x;
        this.inputY = y;
        this.inputRX = rx;
        this.fieldCentric = fieldCentric;
       addRequirements(subsystem);
    }

    //@Override
    public void execute() {
        if (fieldCentric) {
            m_subsystem.fieldCentricDrive(inputX, inputY, inputRX);
        } else {
            // Robot-centric fallback
            m_subsystem.Drive(inputY + inputRX, inputY - inputRX, inputY - inputRX, inputY + inputRX);
        }
    }

    @Override
    public void end(boolean interrupted) {
        m_subsystem.stop();
    }

    @Override
    public boolean isFinished() {
        return false; // Continuous command for teleop
    }
}
