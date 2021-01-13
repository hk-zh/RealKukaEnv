package Controller;

import com.kuka.roboticsAPI.RoboticsAPIContext;
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.geometricModel.Tool;
import com.kuka.roboticsAPI.deviceModel.JointPosition;
import com.kuka.roboticsAPI.motionModel.BasicMotions;
import com.kuka.roboticsAPI.geometricModel.Frame;

import robot.ExecutionController;
import robot.SunriseConnector;
import utility.SingleInstanceChecker;
import com.kuka.common.ThreadUtil;
import com.kuka.connectivity.motionModel.smartServo.SmartServo;

class Vector {
	double [] array;
	public Vector(double x, double y, double z) {
		array = new double [3];
		array[0] = x;
		array[1] = y;
		array[2] = z;
	}
	public Vector(double [] p) {
		array = new double [p.length];
		for (int i = 0; i < p.length; i++) {
			array[i] = p[i];
		}
	}
	public Vector(int n) {
		array = new double [n];
	}
	public Vector add(Vector p) {
		double [] toReturn = new double [array.length];
		for (int i = 0; i < array.length; i++) {
			toReturn[i] = array[i] + p.array[i];
		}
		return this;
	}
	public Vector sub(Vector p) {
		double [] toReturn = new double [array.length];
		for (int i = 0; i < array.length; i++) {
			toReturn[i] = array[i] - p.array[i];
		}
		return this;
	}
	public void set(double [] p) {
		for (int i = 0; i < Math.min(p.length, array.length);i ++) {
			array[i] = p[i];
		}
	}
	public double norm(Vector p) {
		double norm = 0;
		for (int i = 0; i < array.length; i++) {
			norm += Math.pow(array[i] - p.array[i], 2);
		}
		return Math.sqrt(norm);
	}
	public double norm() {
		double norm = 0;
		for (int i = 0; i < array.length; i++) {
			norm += Math.pow(array[i], 2);
		}
		return Math.sqrt(norm);
	}
	public Vector normalize() {
		double norm = norm();
		double [] toReturn = new double [array.length];
		for (int i = 0; i < array.length; i++) {
			toReturn [i] = array[i] / norm;
		}
		return new Vector(toReturn);
	}
	public Vector dot(double p) {
		double [] toReturn = new double [array.length];
		for (int i = 0; i < array.length; i++) {
			toReturn[i] = array[i]*p; 
		}
		return new Vector(toReturn);
	}
	public double [] toArray() {
		double [] toReturn = new double [array.length];
		for (int i = 0; i < toReturn.length; i++) {
			toReturn[i] = array[i];
		}
		return toReturn;
	}
}
public class Motions extends RoboticsAPIApplication {
	private Vector target = new Vector(3);
	private double threshold = 0.01;
	LBR robot;
	Tool tool;
	
	public void stepJoints(double [] joints) {
		JointPosition targetPosition = new JointPosition(joints);
		tool.move(BasicMotions.ptp(targetPosition));
	}
	public void stepFrame(double [] frame) {
		Frame targetFrame = new Frame(frame[0], frame[1], frame[2], 0, 0, 0);
		tool.moveAsync(BasicMotions.ptp(targetFrame));
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

	public double[] getCurrentFrame() {
		Frame framePosition = robot.getCurrentCartesianPosition(tool.getFrame("/TCP"));
	    double [] pos = new double [3];
	    pos[0] = framePosition.getX();
	    pos[1] = framePosition.getY();
	    pos[2] = framePosition.getZ();
	    return pos;
	}
	public double[] getCurrentFrameAndVelocity() {
		double [] pos1 = getCurrentFrame();
		ThreadUtil.milliSleep(10);
		double [] pos2 = getCurrentFrame();
		double [] pos_vel = new double [6];
		for (int i = 0; i < 6; i++) {
			if (i < 3) {
			    pos_vel[i] = pos1[i];
			} else {
				pos_vel[i] = (pos1[i-3] - pos2[i-3]) / 0.01;
			}
		}
		return pos_vel;
	}
	public double[] getCurrentFrameVelocity() {
		double [] pos1 = getCurrentFrame();
		ThreadUtil.milliSleep(10);
		double [] pos2 = getCurrentFrame();
		double [] vel = new double [3];
		for (int i = 0; i < 3; i++) {
			vel[i] = (pos1[i] - pos2[i])/ 0.01;
		}
		return vel;
	}
	private boolean isReached(Vector currentPosition) {
		return this.target.norm(currentPosition) < threshold;
	}
	public void setTarget(double [] target) {
		this.target.set(target);
	}
	
	public void step() {
		Vector currentPosition = new Vector(getCurrentFrame());
	    Vector direction = this.target.sub(currentPosition).normalize();
	    if (!isReached(currentPosition)) {
	    	double [] tTarget = currentPosition.add(direction.dot(10)).toArray();
	    	Frame targetFrame = new Frame(tTarget[0], tTarget[1], tTarget[2], 0, 0, 0);
	    	tool.moveAsync(BasicMotions.ptp(targetFrame));
	    }
	}
}
