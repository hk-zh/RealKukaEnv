package applications;

import static com.kuka.roboticsAPI.motionModel.BasicMotions.ptp;

import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import com.kuka.roboticsAPI.deviceModel.JointPosition;
import com.kuka.roboticsAPI.deviceModel.LBR;

public class GoFront extends RoboticsAPIApplication {
	private LBR robot;

	public void initialize() {
		robot = getContext().getDeviceFromType(LBR.class);
	}

	public void run() {
		robot.move(ptp(new JointPosition(0, Math.toRadians(30), 0, Math.toRadians(-60), 0, Math.toRadians(45), 0))
				.setJointVelocityRel(0.3).setJointAccelerationRel(0.3));
	}

	/**
	 * Auto-generated method stub. Do not modify the contents of this method.
	 */
	public static void main(String[] args) {
		GoFront app = new GoFront();
		app.runApplication();
	}
}
