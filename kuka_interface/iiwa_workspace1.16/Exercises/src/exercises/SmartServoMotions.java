package exercises;

import com.kuka.roboticsAPI.RoboticsAPIContext;
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import com.kuka.roboticsAPI.deviceModel.JointPosition;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.geometricModel.Frame;
import com.kuka.roboticsAPI.geometricModel.ObjectFrame;
import com.kuka.roboticsAPI.geometricModel.Tool;
import com.kuka.roboticsAPI.motionModel.BasicMotions;

import robot.SunriseConnector;

import com.kuka.common.ThreadUtil;
import com.kuka.connectivity.motionModel.smartServo.ISmartServoRuntime;
import com.kuka.connectivity.motionModel.smartServo.SmartServo;

public class SmartServoMotions extends RoboticsAPIApplication{

	private LBR _lbr;
	private Tool tool;
	private SmartServo motion;
	private ObjectFrame toolFrame;
	private ISmartServoRuntime motion_rt;
	private double [] target;
	public SmartServoMotions(RoboticsAPIContext context) {
		super(context);
		target = new double [3];
	}

	public void run() {
		// TODO Auto-generated method stub
		_lbr = getContext().getDeviceFromType(LBR.class);
		tool = SunriseConnector.getTool();
		toolFrame = _lbr.getFlange();
		motion = new SmartServo(_lbr.getCurrentJointPosition());
		motion.overrideJointAcceleration(1.0);
		motion.setMinimumTrajectoryExecutionTime(0.0);
		toolFrame.moveAsync(motion);
		motion_rt = motion.getRuntime();
		
	}
	
	public void stepJoints(double [] joints) {
		JointPosition targetPosition = new JointPosition(joints);
		tool.move(BasicMotions.ptp(targetPosition));
	}
	public double [] getCurrentJoints() {
		JointPosition currentPosition = _lbr.getCurrentJointPosition();
		double [] joints = new double [7];
		for (int i = 0; i < 7; i++) {
			joints[i] = currentPosition.get(i);
		}
		return joints;
	} 
	
	public double[] getCurrentFrame() {
		Frame framePosition = _lbr.getCurrentCartesianPosition(tool.getFrame("/TCP"));
	    double [] pos = new double [3];
	    pos[0] = framePosition.getX();
	    pos[1] = framePosition.getY();
	    pos[2] = framePosition.getZ();
	    return pos;
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

	public void setTarget(double [] target) {
         for (int i = 0; i < 3; i++) {
        	 this.target[i] = target[i];
         }	
	}
	public void step() {
		Frame frame = new Frame();
		frame.setX(this.target[0]);
		frame.setY(this.target[1]);
		frame.setZ(this.target[2]);
		frame.setAlphaRad(0.0);
		frame.setBetaRad(0.0);
		frame.setGammaRad(0.0);
		motion_rt.setDestination(frame);
		ThreadUtil.milliSleep(40);
	}
	
}
