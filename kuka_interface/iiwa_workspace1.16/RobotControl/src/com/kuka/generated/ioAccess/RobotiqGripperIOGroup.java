package com.kuka.generated.ioAccess;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.kuka.roboticsAPI.controllerModel.Controller;
import com.kuka.roboticsAPI.ioModel.AbstractIOGroup;
import com.kuka.roboticsAPI.ioModel.IOTypes;

/**
 * Automatically generated class to abstract I/O access to I/O group <b>RobotiqGripper</b>.<br>
 * <i>Please, do not modify!</i>
 * <p>
 * <b>I/O group description:</b><br>
 * Robotiq 2F-85 gripper EtherCat Control
 */
@Singleton
public class RobotiqGripperIOGroup extends AbstractIOGroup
{
	/**
	 * Constructor to create an instance of class 'RobotiqGripper'.<br>
	 * <i>This constructor is automatically generated. Please, do not modify!</i>
	 *
	 * @param controller
	 *            the controller, which has access to the I/O group 'RobotiqGripper'
	 */
	@Inject
	public RobotiqGripperIOGroup(Controller controller)
	{
		super(controller, "RobotiqGripper");

		addDigitalOutput("ActionRequest", IOTypes.UNSIGNED_INTEGER, 8);
		addDigitalOutput("PositionRequest", IOTypes.UNSIGNED_INTEGER, 8);
		addInput("GripperStatus", IOTypes.UNSIGNED_INTEGER, 8);
		addDigitalOutput("Force", IOTypes.UNSIGNED_INTEGER, 8);
		addInput("FaultStatus", IOTypes.UNSIGNED_INTEGER, 8);
		addInput("Current", IOTypes.UNSIGNED_INTEGER, 8);
		addInput("Position", IOTypes.UNSIGNED_INTEGER, 8);
		addInput("PositionRequestEcho", IOTypes.UNSIGNED_INTEGER, 8);
		addDigitalOutput("Speed", IOTypes.UNSIGNED_INTEGER, 8);
	}

	/**
	 * Gets the value of the <b>digital output '<i>ActionRequest</i>'</b>.<br>
	 * <i>This method is automatically generated. Please, do not modify!</i>
	 * <p>
	 * <b>I/O direction and type:</b><br>
	 * digital output
	 * <p>
	 * <b>User description of the I/O:</b><br>
	 * ./.
	 * <p>
	 * <b>Range of the I/O value:</b><br>
	 * [0; 255]
	 *
	 * @return current value of the digital output 'ActionRequest'
	 */
	public java.lang.Integer getActionRequest()
	{
		return getNumberIOValue("ActionRequest", true).intValue();
	}

	/**
	 * Sets the value of the <b>digital output '<i>ActionRequest</i>'</b>.<br>
	 * <i>This method is automatically generated. Please, do not modify!</i>
	 * <p>
	 * <b>I/O direction and type:</b><br>
	 * digital output
	 * <p>
	 * <b>User description of the I/O:</b><br>
	 * ./.
	 * <p>
	 * <b>Range of the I/O value:</b><br>
	 * [0; 255]
	 *
	 * @param value
	 *            the value, which has to be written to the digital output 'ActionRequest'
	 */
	public void setActionRequest(java.lang.Integer value)
	{
		setDigitalOutput("ActionRequest", value);
	}

	/**
	 * Gets the value of the <b>digital output '<i>PositionRequest</i>'</b>.<br>
	 * <i>This method is automatically generated. Please, do not modify!</i>
	 * <p>
	 * <b>I/O direction and type:</b><br>
	 * digital output
	 * <p>
	 * <b>User description of the I/O:</b><br>
	 * ./.
	 * <p>
	 * <b>Range of the I/O value:</b><br>
	 * [0; 255]
	 *
	 * @return current value of the digital output 'PositionRequest'
	 */
	public java.lang.Integer getPositionRequest()
	{
		return getNumberIOValue("PositionRequest", true).intValue();
	}

	/**
	 * Sets the value of the <b>digital output '<i>PositionRequest</i>'</b>.<br>
	 * <i>This method is automatically generated. Please, do not modify!</i>
	 * <p>
	 * <b>I/O direction and type:</b><br>
	 * digital output
	 * <p>
	 * <b>User description of the I/O:</b><br>
	 * ./.
	 * <p>
	 * <b>Range of the I/O value:</b><br>
	 * [0; 255]
	 *
	 * @param value
	 *            the value, which has to be written to the digital output 'PositionRequest'
	 */
	public void setPositionRequest(java.lang.Integer value)
	{
		setDigitalOutput("PositionRequest", value);
	}

	/**
	 * Gets the value of the <b>digital input '<i>GripperStatus</i>'</b>.<br>
	 * <i>This method is automatically generated. Please, do not modify!</i>
	 * <p>
	 * <b>I/O direction and type:</b><br>
	 * digital input
	 * <p>
	 * <b>User description of the I/O:</b><br>
	 * ./.
	 * <p>
	 * <b>Range of the I/O value:</b><br>
	 * [0; 255]
	 *
	 * @return current value of the digital input 'GripperStatus'
	 */
	public java.lang.Integer getGripperStatus()
	{
		return getNumberIOValue("GripperStatus", false).intValue();
	}

	/**
	 * Gets the value of the <b>digital output '<i>Force</i>'</b>.<br>
	 * <i>This method is automatically generated. Please, do not modify!</i>
	 * <p>
	 * <b>I/O direction and type:</b><br>
	 * digital output
	 * <p>
	 * <b>User description of the I/O:</b><br>
	 * ./.
	 * <p>
	 * <b>Range of the I/O value:</b><br>
	 * [0; 255]
	 *
	 * @return current value of the digital output 'Force'
	 */
	public java.lang.Integer getForce()
	{
		return getNumberIOValue("Force", true).intValue();
	}

	/**
	 * Sets the value of the <b>digital output '<i>Force</i>'</b>.<br>
	 * <i>This method is automatically generated. Please, do not modify!</i>
	 * <p>
	 * <b>I/O direction and type:</b><br>
	 * digital output
	 * <p>
	 * <b>User description of the I/O:</b><br>
	 * ./.
	 * <p>
	 * <b>Range of the I/O value:</b><br>
	 * [0; 255]
	 *
	 * @param value
	 *            the value, which has to be written to the digital output 'Force'
	 */
	public void setForce(java.lang.Integer value)
	{
		setDigitalOutput("Force", value);
	}

	/**
	 * Gets the value of the <b>digital input '<i>FaultStatus</i>'</b>.<br>
	 * <i>This method is automatically generated. Please, do not modify!</i>
	 * <p>
	 * <b>I/O direction and type:</b><br>
	 * digital input
	 * <p>
	 * <b>User description of the I/O:</b><br>
	 * ./.
	 * <p>
	 * <b>Range of the I/O value:</b><br>
	 * [0; 255]
	 *
	 * @return current value of the digital input 'FaultStatus'
	 */
	public java.lang.Integer getFaultStatus()
	{
		return getNumberIOValue("FaultStatus", false).intValue();
	}

	/**
	 * Gets the value of the <b>digital input '<i>Current</i>'</b>.<br>
	 * <i>This method is automatically generated. Please, do not modify!</i>
	 * <p>
	 * <b>I/O direction and type:</b><br>
	 * digital input
	 * <p>
	 * <b>User description of the I/O:</b><br>
	 * ./.
	 * <p>
	 * <b>Range of the I/O value:</b><br>
	 * [0; 255]
	 *
	 * @return current value of the digital input 'Current'
	 */
	public java.lang.Integer getCurrent()
	{
		return getNumberIOValue("Current", false).intValue();
	}

	/**
	 * Gets the value of the <b>digital input '<i>Position</i>'</b>.<br>
	 * <i>This method is automatically generated. Please, do not modify!</i>
	 * <p>
	 * <b>I/O direction and type:</b><br>
	 * digital input
	 * <p>
	 * <b>User description of the I/O:</b><br>
	 * ./.
	 * <p>
	 * <b>Range of the I/O value:</b><br>
	 * [0; 255]
	 *
	 * @return current value of the digital input 'Position'
	 */
	public java.lang.Integer getPosition()
	{
		return getNumberIOValue("Position", false).intValue();
	}

	/**
	 * Gets the value of the <b>digital input '<i>PositionRequestEcho</i>'</b>.<br>
	 * <i>This method is automatically generated. Please, do not modify!</i>
	 * <p>
	 * <b>I/O direction and type:</b><br>
	 * digital input
	 * <p>
	 * <b>User description of the I/O:</b><br>
	 * ./.
	 * <p>
	 * <b>Range of the I/O value:</b><br>
	 * [0; 255]
	 *
	 * @return current value of the digital input 'PositionRequestEcho'
	 */
	public java.lang.Integer getPositionRequestEcho()
	{
		return getNumberIOValue("PositionRequestEcho", false).intValue();
	}

	/**
	 * Gets the value of the <b>digital output '<i>Speed</i>'</b>.<br>
	 * <i>This method is automatically generated. Please, do not modify!</i>
	 * <p>
	 * <b>I/O direction and type:</b><br>
	 * digital output
	 * <p>
	 * <b>User description of the I/O:</b><br>
	 * ./.
	 * <p>
	 * <b>Range of the I/O value:</b><br>
	 * [0; 255]
	 *
	 * @return current value of the digital output 'Speed'
	 */
	public java.lang.Integer getSpeed()
	{
		return getNumberIOValue("Speed", true).intValue();
	}

	/**
	 * Sets the value of the <b>digital output '<i>Speed</i>'</b>.<br>
	 * <i>This method is automatically generated. Please, do not modify!</i>
	 * <p>
	 * <b>I/O direction and type:</b><br>
	 * digital output
	 * <p>
	 * <b>User description of the I/O:</b><br>
	 * ./.
	 * <p>
	 * <b>Range of the I/O value:</b><br>
	 * [0; 255]
	 *
	 * @param value
	 *            the value, which has to be written to the digital output 'Speed'
	 */
	public void setSpeed(java.lang.Integer value)
	{
		setDigitalOutput("Speed", value);
	}

}
