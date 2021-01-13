package applications;

import static com.kuka.roboticsAPI.motionModel.BasicMotions.ptp;
import static com.kuka.roboticsAPI.motionModel.BasicMotions.spl;

import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import com.kuka.roboticsAPI.controllerModel.sunrise.SunriseController;
import com.kuka.roboticsAPI.deviceModel.JointPosition;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.geometricModel.Frame;
import com.kuka.roboticsAPI.motionModel.IMotionContainer;
import com.kuka.roboticsAPI.motionModel.PTP;
import com.kuka.roboticsAPI.motionModel.Spline;

public class SimpleEndlessLoop extends RoboticsAPIApplication {
	private SunriseController kuka_Sunrise_Cabinet_1;
	private LBR robot;

	final static double radiusOfCircMove = 120;
	final static int nullSpaceAngle = 80;

	final static double offsetAxis2And4 = Math.toRadians(20);
	final static double offsetAxis4And6 = Math.toRadians(-40);
	double[] loopCenterPosition = new double[] { 0, offsetAxis2And4, 0,
			offsetAxis2And4 + offsetAxis4And6 - Math.toRadians(90), 0, offsetAxis4And6, Math.toRadians(90) };

	public void initialize() {
		kuka_Sunrise_Cabinet_1 = (SunriseController) getContext().getDefaultController();
		robot = getContext().getDeviceFromType(LBR.class);
	}

	public void run() {
		// while (true) {
		// moveInDegrees(5, 30, -5, -60, 5, 90, -5);
		// moveInDegrees(-5, 30, 5, -60, -5, 90, 5);
		// }

		IMotionContainer activeMove = null;
		IMotionContainer nextMove = null;

		PTP ptpToLoopCenter = ptp(loopCenterPosition);
		ptpToLoopCenter.setJointVelocityRel(0.25);

		activeMove = robot.move(ptpToLoopCenter);

		Frame startFrame = robot.getCurrentCartesianPosition(robot.getFlange());
		Spline lemniscateSpline = createLemniscateSpline(startFrame).setJointJerkRel(0.5).setCartVelocity(250);
		lemniscateSpline.setJointVelocityRel(0.25);
		lemniscateSpline.setBlendingCart(10);

		// robot.move(lemniscateSpline);
		while (true) {

			if (!kuka_Sunrise_Cabinet_1.getExecutionService().isPaused()) {
				nextMove = robot.moveAsync(lemniscateSpline);
				activeMove.await();
				activeMove = nextMove;
			} else {
				break;
			}
		}
	}

	@SuppressWarnings("unused")
	private void moveInDegrees(double j1, double j2, double j3, double j4, double j5, double j6, double j7) {
		robot.move(ptp(
				new JointPosition(Math.toRadians(j1), Math.toRadians(j2), Math.toRadians(j3), Math.toRadians(j4), Math
						.toRadians(j5), Math.toRadians(j6), Math.toRadians(j7))).setJointVelocityRel(0.3));
	}

	private Spline createLemniscateSpline(Frame centerFrame) {

		// Create a new frame with the center frame as parent. Set an offset for the x axis to this parent.
		Frame rightFrame = (new Frame(centerFrame)).setX(2 * radiusOfCircMove);

		// Create a new frame with the center frame as parent. Set an offset for the x axis to this parent.
		Frame leftFrame = (new Frame(centerFrame)).setX(-2 * radiusOfCircMove);

		// Create a new frame with the center frame as parent. Set an offset for the x and y axes to this parent.
		Frame topLeftFrame = (new Frame(centerFrame)).setX(-radiusOfCircMove).setY(radiusOfCircMove);

		// Create a new frame with the center frame as parent. Set an offset for the x and y axes to this parent.
		Frame topRightFrame = (new Frame(centerFrame)).setX(+radiusOfCircMove).setY(radiusOfCircMove);

		// Create a new frame with the center frame as parent. Set an offset for the x and y axes to this parent.
		Frame bottomRightFrame = (new Frame(centerFrame)).setX(+radiusOfCircMove).setY(-radiusOfCircMove);

		// Create a new frame with the center frame as parent. Set an offset for the x and y axes to this parent.
		Frame bottomLeftFrame = (new Frame(centerFrame)).setX(-radiusOfCircMove).setY(-radiusOfCircMove);

		// Create a spline that describes a lemniscate
		Spline spline = new Spline(spl(bottomLeftFrame), spl(leftFrame), spl(topLeftFrame), spl(centerFrame),
				spl(bottomRightFrame), spl(rightFrame), spl(topRightFrame), spl(centerFrame));
		return spline;
	}

	/**
	 * Auto-generated method stub. Do not modify the contents of this method.
	 */
	public static void main(String[] args) {
		SimpleEndlessLoop app = new SimpleEndlessLoop();
		app.runApplication();
	}
}
