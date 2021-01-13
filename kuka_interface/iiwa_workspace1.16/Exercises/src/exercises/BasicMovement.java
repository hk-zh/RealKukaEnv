package exercises;

import com.kuka.roboticsAPI.RoboticsAPIContext;
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.geometricModel.Tool;
import com.kuka.roboticsAPI.deviceModel.JointPosition;
import com.kuka.roboticsAPI.motionModel.BasicMotions;

import robot.ExecutionController;
import robot.SunriseConnector;
import utility.SingleInstanceChecker;

public class BasicMovement extends RoboticsAPIApplication {
	public BasicMovement(RoboticsAPIContext context) {
		super(context);
	}
	
	LBR robot;
	Tool tool;
	
	public void step(double [] joints) {
		JointPosition targetPosition = new JointPosition(joints);
		tool.move(BasicMotions.ptp(targetPosition));
	}
	public double [] getCurrentJoints() {
		JointPosition currentPosition = robot.getCurrentJointPosition();
		double [] joints = new double [7];
		for (int i = 0; i < 7; i++) {
			joints[i] = currentPosition.get(i);
		}
		return joints;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		robot = SunriseConnector.getRobot();
		tool = SunriseConnector.getTool();
		
	}
}
