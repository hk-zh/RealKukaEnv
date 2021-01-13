package applications;


import static com.kuka.roboticsAPI.motionModel.BasicMotions.ptpHome;

import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import com.kuka.roboticsAPI.deviceModel.LBR;

public class GoHome extends RoboticsAPIApplication {
	private LBR robot;

	public void initialize() {
		robot = getContext().getDeviceFromType(LBR.class);
	}

	public void run() {
		robot.move(ptpHome().setJointVelocityRel(0.3).setJointAccelerationRel(0.3));
	}

	/**
	 * Auto-generated method stub. Do not modify the contents of this method.
	 */
	public static void main(String[] args) {
		GoHome app = new GoHome();
		app.runApplication();
	}
}
