package applications;

import static com.kuka.roboticsAPI.motionModel.BasicMotions.positionHold;
import static com.kuka.roboticsAPI.motionModel.BasicMotions.ptp;

import java.util.concurrent.TimeUnit;

import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import com.kuka.roboticsAPI.deviceModel.JointPosition;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.geometricModel.CartDOF;
import com.kuka.roboticsAPI.geometricModel.Tool;
import com.kuka.roboticsAPI.motionModel.IMotionContainer;
import com.kuka.roboticsAPI.motionModel.controlModeModel.CartesianImpedanceControlMode;
import com.kuka.roboticsAPI.uiModel.ApplicationDialogType;

public class ImpCartesian extends RoboticsAPIApplication {
	private LBR robot;
	private Tool tool;

	public void initialize() {
		robot = getContext().getDeviceFromType(LBR.class);

		tool = getApplicationData().createFromTemplate("EXERCISE_TOOL");
	}

	public void run() {
		tool.attachTo(robot.getFlange());

		JointPosition start_jpos = new JointPosition(Math.toRadians(0), Math.toRadians(30), 0., Math.toRadians(-75),
				0., Math.toRadians(45.), 0.);
		robot.move(ptp(start_jpos));

		CartesianImpedanceControlMode ci = new CartesianImpedanceControlMode();
		ci.parametrize(CartDOF.X).setStiffness(100);
		ci.parametrize(CartDOF.Y).setStiffness(1000);
		ci.parametrize(CartDOF.Z).setStiffness(5000);

		int ind_damping = getApplicationUI().displayModalDialog(ApplicationDialogType.QUESTION, "damping?", "0.1",
				"0.2", "0.4", "0.6", "0.8", "1.0");
		double damping = ind_damping * 0.2;
		if (damping == 0.) {
			damping = 0.1;
		}
		ci.parametrize(CartDOF.ALL).setDamping(damping);

		// start gravitation compensation
		IMotionContainer imc = null;
		imc = robot.moveAsync(positionHold(ci, -1, TimeUnit.SECONDS));

		int answ = getApplicationUI().displayModalDialog(ApplicationDialogType.INFORMATION, "stop impedance mode?",
				"yes");
		if (answ == 0) {
			imc.cancel();
		}

	}

	/**
	 * Auto-generated method stub. Do not modify the contents of this method.
	 */
	public static void main(String[] args) {
		ImpCartesian app = new ImpCartesian();
		app.runApplication();
	}
}
