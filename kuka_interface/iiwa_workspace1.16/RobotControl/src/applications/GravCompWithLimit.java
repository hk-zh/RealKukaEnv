package applications;

import static com.kuka.roboticsAPI.motionModel.BasicMotions.positionHold;
import static com.kuka.roboticsAPI.motionModel.BasicMotions.ptp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import com.kuka.roboticsAPI.controllerModel.sunrise.SunriseController;
import com.kuka.roboticsAPI.deviceModel.JointPosition;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.geometricModel.Tool;
import com.kuka.roboticsAPI.motionModel.IMotionContainer;
import com.kuka.roboticsAPI.motionModel.controlModeModel.JointImpedanceControlMode;

public class GravCompWithLimit extends RoboticsAPIApplication {
	private SunriseController kuka_Sunrise_Cabinet_1;
	private LBR robot;
	private Tool tool;

	public void initialize() {
//		kuka_Sunrise_Cabinet_1 = (SunriseController) getController("KUKA_Sunrise_Cabinet_1");
		kuka_Sunrise_Cabinet_1 = (SunriseController) getContext().getDefaultController();
		robot = getContext().getDeviceFromType(LBR.class);

		// tool = getApplicationData().createFromTemplate("NO_TOOL");
		tool = getApplicationData().createFromTemplate("EXERCISE_TOOL");
	}

	public void run() {
		tool.attachTo(robot.getFlange());
		gravCompImpedance();
	}

	public static final double[] JOINT_LIMITS = { Math.toRadians(170), Math.toRadians(120), Math.toRadians(170),
			Math.toRadians(120), Math.toRadians(170), Math.toRadians(120), Math.toRadians(175) };

	private void gravCompImpedance() {
		double limitFactor = 0.92;
		double highStiffness = 30;
		double lowStiffness = 0;

		double damping = 0.1;
//		double maxJointDelta = Math.PI;

		// int intervall = 10;

		JointPosition currentPosition = robot.getCurrentJointPosition();

		// configure joint impedance controller
		JointImpedanceControlMode jointImpMode = new JointImpedanceControlMode(lowStiffness, lowStiffness,
				lowStiffness, lowStiffness, lowStiffness, lowStiffness, lowStiffness);
		jointImpMode.setDamping(damping, damping, damping, damping, damping, damping, damping);
//		jointImpMode.setMaxJointDeltas(maxJointDelta, maxJointDelta, maxJointDelta, maxJointDelta, maxJointDelta,
//				maxJointDelta, maxJointDelta);

		double[] curJointVals;
		double[] stiffness = jointImpMode.getStiffness();
		

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

		// long now = System.currentTimeMillis();
		// long next = now + intervall;
		// boolean notPaused = true;

		// motion containers
		positionHold(jointImpMode, 10, TimeUnit.MILLISECONDS);
		IMotionContainer activeMove = tool.moveAsync(ptp(currentPosition).setMode(jointImpMode));
		IMotionContainer nextMove = null;
		boolean activeMotions = true;
		while (activeMotions) {
			// now = System.currentTimeMillis();
			// next += intervall;

			currentPosition = robot.getCurrentJointPosition();
			curJointVals = currentPosition.get();

			stiffness = jointImpMode.getStiffness();
			for (int i = 0; i < 7; i++) {
				if (Math.abs(curJointVals[i]) > limitFactor * JOINT_LIMITS[i]) {
					// if (stiffness[i] == lowStiffness)
					// if (i == 6)
					// System.out.println("Joint " + (i + 1) + " exceeding limit " + limitFactor * JOINT_LIMITS[i]
					// + " with " + curJointVals[i]);
					stiffness[i] = highStiffness;
					if (curJointVals[i] > 0) {
						currentPosition.set(i, 0.99 * limitFactor * JOINT_LIMITS[i]);
					} else {
						currentPosition.set(i, -0.99 * limitFactor * JOINT_LIMITS[i]);
					}
				} else {
					// System.out.println("Joint " + (i + 1) + " back to normal with " + curJointVals[i]);
					stiffness[i] = lowStiffness;
				}
			}
			jointImpMode.setStiffness(stiffness);
			// System.out.println(Arrays.toString(stiffness));

			// nextMove = tool.moveAsync(positionHold(jointImpMode, 2 * intervall, TimeUnit.MILLISECONDS));
			if (!kuka_Sunrise_Cabinet_1.getExecutionService().isPaused()) {
				nextMove = tool.moveAsync(ptp(currentPosition).setMode(jointImpMode));
			}
			activeMove.await();
			// activeMove.cancel();
			activeMove = nextMove;

			System.out.println(sdf.format(Calendar.getInstance().getTime()));
			// ThreadUtil.milliSleep(next - now);

			if (kuka_Sunrise_Cabinet_1.getExecutionService().getActiveContainerCount() == 0) {
				activeMotions = false;
			}
		}
		System.out.println("GravComp ended");
	}

	/**
	 * Auto-generated method stub. Do not modify the contents of this method.
	 */
	public static void main(String[] args) {
		GravCompWithLimit app = new GravCompWithLimit();
		app.runApplication();

	}
}
