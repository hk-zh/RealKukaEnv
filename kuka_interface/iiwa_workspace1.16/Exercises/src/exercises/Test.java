package exercises;

import com.kuka.roboticsAPI.RoboticsAPIContext;
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.geometricModel.Tool;

import robot.ExecutionController;
import robot.SunriseConnector;
import utility.SingleInstanceChecker;

public class Test {
    
	BasicMovement app = null;
	public Test() {
		RoboticsAPIContext.useGracefulInitialization(true);

		// check if another robot application is already running
		new SingleInstanceChecker().start();

		// initialization
		app = new BasicMovement(
				RoboticsAPIContext.createFromResource(BasicMovement.class, "RoboticsAPI.config.xml"));
		
		SunriseConnector.initialize(app);

		app.run();
	}
	public void step(double [] joints) {
		app.step(joints);
	}
	
	public double [] getCurrentJoints() {
		return app.getCurrentJoints();
	}
	
	public String path() {
		return this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
	}
}
