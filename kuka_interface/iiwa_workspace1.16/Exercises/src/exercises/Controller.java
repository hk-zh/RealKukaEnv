package exercises;

import com.kuka.roboticsAPI.RoboticsAPIContext;
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.geometricModel.Tool;

import robot.ExecutionController;
import robot.SunriseConnector;
import utility.SingleInstanceChecker;


public class Controller {
    
	private SmartServoMotions app = null;
	public Controller() {
		RoboticsAPIContext.useGracefulInitialization(true);

		// check if another robot application is already running
		new SingleInstanceChecker().start();

		// initialization
		app = new SmartServoMotions(
				RoboticsAPIContext.createFromResource(SmartServoMotions.class, "RoboticsAPI.config.xml"));
		
		SunriseConnector.initialize(app);

		app.run();
	}
	
	
	public double [] getCurrentJoints() {
		return app.getCurrentJoints();
	}
	
	public double [] getCurrentFrame() {
		return app.getCurrentFrame();
	}
	
	public double [] getCurrentFrameAndVelocity() {
		return app.getCurrentFrameAndVelocity();
	}
	
	public double [] getCurrentFrameVelocity() {
		return app.getCurrentFrameVelocity();
	}
	
	public void setTarget(double [] target) {
		app.setTarget(target);
	}
	public void step() {
		app.step();
	}
	
	
	
	public String path() {
		return this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
	}


    public static void main (String [] args) {
	     Controller c = new Controller();
	     c.app.run();
    }
}
