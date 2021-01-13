package applications;

import static com.kuka.roboticsAPI.motionModel.BasicMotions.ptpHome;

//import org.slf4j.LoggerFactory;

import com.kuka.common.ThreadUtil;
import com.kuka.roboticsAPI.RoboticsAPIContext;
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import com.kuka.roboticsAPI.controllerModel.sunrise.SunriseController;
import com.kuka.roboticsAPI.controllerModel.sunrise.api.SSR;
import com.kuka.roboticsAPI.controllerModel.sunrise.api.SSRFactory;
import com.kuka.roboticsAPI.controllerModel.sunrise.connectionLib.Message;
import com.kuka.roboticsAPI.deviceModel.LBR;

public class GMSSetZero extends RoboticsAPIApplication {
	private SunriseController kuka_Sunrise_Cabinet_1;
	private LBR lbr_iiwa_7_R800_1;
	// Workaround, damit Applikation auf Sunrise Developper Workbench startet
	// LoggerFactory aFactory;

	/** Logger instance. */
	// private static final Logger LOG = .getLogger(.class);

	public GMSSetZero(RoboticsAPIContext context) {
		super(context);
	}

//	public GMSSetZero(){
//		super(RoboticsAPIContext.createFromResource(GMSSetZero.class, "RoboticsAPI.config.xml")); // necessary to start from external pc
//	}

	public void initialize() {
		kuka_Sunrise_Cabinet_1 = (SunriseController) getController("KUKA_Sunrise_Cabinet_1");
		lbr_iiwa_7_R800_1 = (LBR) getRobot(kuka_Sunrise_Cabinet_1, "LBR_iiwa_7_R800_1");
	}

	public void printDoubleVec(String aText, double[] vec) {
		System.out.print(aText + " ");
		for (int i = 0; i < vec.length; i++) {
			System.out.print(vec[i]);
			System.out.print(' ');
		}
		System.out.println();
	}

	public void setAxisPositionRef(int index) {
		final LBR robot = kuka_Sunrise_Cabinet_1.getDevice(LBR.class);
		SSR resetSSR = SSRFactory.requestDeviceFunction(robot.getName(), "ResetPosition");

		resetSSR.addParam(0.0);
		resetSSR.addParam(index);
		((SunriseController) robot.getController()).sendSynchronousSSR(resetSSR);
	}

	public void setAxisTorqueZero(final int index) {
		final double RESET_VALUE = 0;
		final LBR robot = kuka_Sunrise_Cabinet_1.getDevice(LBR.class);
		// LOG.info("Reset torque of axis #{} of {} to {}", (index + 1),
		// robot.getName(), RESET_VALUE);
		System.out.printf("Reset torque of axis #%d of %s to %f", (index + 1), robot.getName(), RESET_VALUE);
		// Und nu noch nen SSR zum setzen der DATEN!
		// final SSR unmasterSsr =
		// SSRFactory.createUnmasterTorqueGMSSSR(robot.getName(), index);
		// kuka_Sunrise_Cabinet_1.sendSynchronousSSR(unmasterSsr);
		final SSR masterSsr = SSRFactory.createResetTorqueGMSSSR(robot.getName(), index, RESET_VALUE);
		Message msg = kuka_Sunrise_Cabinet_1.sendSynchronousSSR(masterSsr);
		System.out.println("set returned " + msg);
	}

	public void moveKerze() {
		lbr_iiwa_7_R800_1.move(ptpHome());
	}

	public void run() {

		// moveKerze();

		ThreadUtil.milliSleep(1000);
		System.out.println("curTorque " + lbr_iiwa_7_R800_1.getSensorForMeasuredTorque().getSensorData());

//		for (int i = 0; i < lbr_iiwa_7_R800_1.getJointCount(); i++) {
//			setAxisTorqueZero(i);
//			setAxisPositionRef(i);
//			System.out.println("curTorque " + lbr_iiwa_7_R800_1.getSensorForMeasuredTorque().getSensorData());
//			ThreadUtil.milliSleep(1000);
//		}

		int i = 0;
		setAxisTorqueZero(i);
		setAxisPositionRef(i);
		System.out.println("curTorque " + lbr_iiwa_7_R800_1.getSensorForMeasuredTorque().getSensorData());
		ThreadUtil.milliSleep(1000);

		System.out.println("finished");

	}

	/**
	 * Auto-generated method stub. Do not modify the contents of this method.
	 */
	public static void main(String[] args) {
		RoboticsAPIContext.useGracefulInitialization(true);

		// initialization
		GMSSetZero app = new GMSSetZero(
				RoboticsAPIContext.createFromResource(GMSSetZero.class, "RoboticsAPI.config.xml"));

		app.initialize();
		app.run();
	}
}