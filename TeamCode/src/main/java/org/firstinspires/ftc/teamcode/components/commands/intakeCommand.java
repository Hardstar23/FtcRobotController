package org.firstinspires.ftc.teamcode.components.commands;

import com.arcrobotics.ftclib.command.CommandBase;

import org.firstinspires.ftc.teamcode.components.subsystems.intakeSubsytem;

public class intakeCommand extends CommandBase {
    private final intakeSubsytem m_subsystem;
    private final double m_intakePower;

    public intakeCommand(intakeSubsytem subsystem, double intakePower) {
        m_subsystem = subsystem;
        m_intakePower = intakePower;
       addRequirements(subsystem);
    }

    @Override
    public void initialize() {
        m_subsystem.setIntake(m_intakePower);  // ✅ start intake when scheduled
    }

    @Override
    public void execute() {
        // Nothing needed; intake just runs continuously
    }

    @Override
    public void end(boolean interrupted) {
        m_subsystem.setIntake(0);  // ✅ stop intake when command ends
    }

        @Override
    public boolean isFinished() {
        return false;  // runs until canceled
    }
}
