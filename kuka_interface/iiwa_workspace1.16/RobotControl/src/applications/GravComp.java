package applications;

import static com.kuka.roboticsAPI.motionModel.BasicMotions.positionHold;
import static com.kuka.roboticsAPI.motionModel.BasicMotions.ptp;

import java.util.concurrent.TimeUnit;

import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import com.kuka.roboticsAPI.deviceModel.JointPosition;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.geometricModel.Tool;
import com.kuka.roboticsAPI.motionModel.controlModeModel.JointImpedanceControlMode;

public class GravComp extends RoboticsAPIApplication {
	private LBR robot;
	private Tool tool;

	public void initialize() {
		robot = getContext().getDeviceFromType(LBR.class);

		tool = getApplicationData().createFromTemplate("EXERCISE_TOOL");

	}

	public void run() {
		tool.attachTo(robot.getFlange());
		gravCompImpedance();
	}

	private void gravCompImpedance() {
		// move to current position
		JointPosition jp = robot.getCurrentJointPosition();
		System.out.println("Moving to current position");
		robot.move(ptp(jp));

		// configure joint impedance controller
		double damF = 0.7;
		double stiffness = 0;
		JointImpedanceControlMode jipcm = new JointImpedanceControlMode(stiffness, stiffness, stiffness, stiffness,
				stiffness, stiffness, stiffness);
		jipcm.setDamping(damF, damF, damF, damF, damF, damF, damF);
		// jipcm.setMaxJointDeltas(Math.PI, Math.PI, Math.PI, Math.PI, Math.PI,
		// Math.PI, Math.PI);

		// start gravitation compensation
		System.out.println("Starting positionHold");
		tool.move(positionHold(jipcm, 1000, TimeUnit.SECONDS));
	}

	/**
	 * Auto-generated method stub. Do not modify the contents of this method.
	 */
	public static void main(String[] args) {
		GravComp app = new GravComp();
		// app.runApplication();
		app.initialize();
		app.run();
	}
}
