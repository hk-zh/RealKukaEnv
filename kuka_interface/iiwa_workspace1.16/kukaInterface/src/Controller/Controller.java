package Controller;

import com.kuka.common.ThreadUtil;
import com.kuka.roboticsAPI.RoboticsAPIContext;
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.geometricModel.Tool;

import robot.ExecutionController;
import robot.SunriseConnector;
import utility.SingleInstanceChecker;


public class Controller {
    
	private SmartServoLINMotions motion = null;
	private GripperMotions gmotion = null;
	public Controller(double [] startPosition) {
		RoboticsAPIContext.useGracefulInitialization(true);

		// check if another robot application is already running
		new SingleInstanceChecker().start();

		// initialization
		motion = new SmartServoLINMotions(RoboticsAPIContext.createFromResource(SmartServoLINMotions.class, "RoboticsAPI.config.xml"));
		gmotion = new GripperMotions(RoboticsAPIContext.createFromResource(GripperMotions.class, "RoboticsAPI.config.xml"));
		motion.setStartPosition(startPosition);
		motion.initialize();
		motion.run();
		gmotion.initialize();
		gmotion.run();
	}
	
	
	public double [] getCurrentJoints() {
		return motion.getCurrentJointsPosition();
	}
	
	public double [] getCurrentFrame() {
		return motion.getCurrentPosition();
	}
	
	
	public double [] getCurrentFrameVelocity() {
		return motion.getCurrentVelocity();
	}

	public void step() {
		motion.step();
		gmotion.step();
		ThreadUtil.milliSleep(20);
	}
	public void resetInitialPosition() {
		motion.resetInitialPosition();
	}
	
	public void setAction(double [] action) {
		motion.setAction(action);
	}

	public void setGripperAction(double gripper_ctrl) {
		gmotion.setAction(gripper_ctrl);
	}
	
	public boolean hasObject() {
		return gmotion.hasObject();
	}
	
	public double getCurrentGripperPosition() {
		return gmotion.getCurrentGripperPosition();
	}
	
	public double getCurrentGripperVelocity() {
		return gmotion.getCurrentGripperVelocity();
	}
	public void dispose() {
		motion.dispose();
		System.exit(0);
	}
	
    public void gripperClose() {
    	gmotion.gripperClose();
    }
    
    public void gripperOpen() {
    	gmotion.gripperOpen();
    }
	public String path() {
		return this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
	}
	public static void print(double [] x) {
		for (int i = 0; i < x.length; i++) {
			System.out.print(x[i]+ " ");
		}
		System.out.println();
		
	}
	
    public static void main (String [] args) {
    	Controller c = new Controller(new double [] {0.326, 1.07, 0.0, -1.34, 0.0, 0.733, 0.336});
    	print(c.getCurrentFrame());
    	c.gripperClose();
    	
    }


}
