// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXInvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  WPI_TalonFX _leftMaster = new WPI_TalonFX(1, "rio");
	WPI_TalonFX _rightMaster = new WPI_TalonFX(3, "rio");

  WPI_TalonFX _leftSlave = new WPI_TalonFX(2, "rio");
	WPI_TalonFX _rightSlave = new WPI_TalonFX(4, "rio");
	Joystick _gamepad = new Joystick(0);
  Compressor pcmCompressor;
  
  @Override
  public void robotInit() {
    //pcmCompressor = new Compressor(0, PneumaticsModuleType.CTREPCM);
    //pcmCompressor.enableDigital();
  }

  @Override
  public void robotPeriodic() {
    SmartDashboard.putNumber("RS_Leader Supply Current", _rightMaster.getSupplyCurrent());
    SmartDashboard.putNumber("RS_Follower Supply Current", _rightSlave.getSupplyCurrent());

    SmartDashboard.putNumber("LS_Leader Supply Current", _leftMaster.getSupplyCurrent());
    SmartDashboard.putNumber("LS_Follower Supply Current", _leftSlave.getSupplyCurrent());
  }

  @Override
  public void autonomousInit() {}

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {
    /* Ensure motor output is neutral during init */
		_leftMaster.set(ControlMode.PercentOutput, 0);
		_rightMaster.set(ControlMode.PercentOutput, 0);

		/* Factory Default all hardware to prevent unexpected behaviour */
		_leftMaster.configFactoryDefault();
    _leftSlave.configFactoryDefault();
		_rightMaster.configFactoryDefault();
    _rightSlave.configFactoryDefault();

    //Setting follower motor follow the leader motor
    _leftSlave.follow(_leftMaster);
    _rightSlave.follow(_rightMaster);

		/* Set Neutral mode */
		_leftMaster.setNeutralMode(NeutralMode.Brake);
		_rightMaster.setNeutralMode(NeutralMode.Brake);
		
		/* Configure output direction */
		_leftMaster.setInverted(TalonFXInvertType.Clockwise);
		_rightMaster.setInverted(TalonFXInvertType.Clockwise);
  }

  @Override
  public void teleopPeriodic() {
    /* Gamepad processing */
		double forward = -1 * _gamepad.getRawAxis(1);
		double turn = _gamepad.getRawAxis(4);		
		forward = Deadband(forward);
		turn = Deadband(turn);

		/* Arcade Drive using PercentOutput along with Arbitrary Feed Forward supplied by turn */
    // _leftMaster.set(.2);
    // _leftSlave.set(.2);
		_leftMaster.set(ControlMode.PercentOutput, forward, DemandType.ArbitraryFeedForward, +turn);
		_rightMaster.set(ControlMode.PercentOutput, forward, DemandType.ArbitraryFeedForward, -turn);
    
  }

  /** Deadband 5 percent, used on the gamepad */
	double Deadband(double value) {
		/* Upper deadband */
		if (value >= +0.05) 
			return value;
		
		/* Lower deadband */
		if (value <= -0.05)
			return value;
		
		/* Outside deadband */
		return 0;
	}

  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {

  }

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}

  @Override
  public void simulationInit() {}

  @Override
  public void simulationPeriodic() {}
}
