package Controller;

import javax.inject.Inject;
import com.kuka.roboticsAPI.RoboticsAPIContext;
import com.kuka.common.ThreadUtil;
import com.kuka.connectivity.motionModel.smartServo.ISmartServoRuntime;
import com.kuka.connectivity.motionModel.smartServo.SmartServo;
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplicationState;

import com.kuka.roboticsAPI.deviceModel.JointPosition;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.geometricModel.ObjectFrame;
import com.kuka.roboticsAPI.uiModel.ApplicationDialogType;

import utility.SingleInstanceChecker;


public class CheckJointVelocityControl extends RoboticsAPIApplication {
	@Inject
	private LBR robot;
	private final static String informationText=
			"The robot will move with a constant joint velocity.";
	private SmartServo motion;
	private ObjectFrame toolFrame;
	private JointPosition jp;
	private JointPosition jv;
	//private JointPosition jointDisplacement;
	private boolean running = true;
	public CheckJointVelocityControl(RoboticsAPIContext context) {
		super(context);
	}

	@Override
	public void initialize() {
		robot = getContext().getDeviceFromType(LBR.class);
	}

	@Override
	public void run() {
		getLogger().info("Show modal dialog and wait for user to confirm");
        int isCancel = getApplicationUI().displayModalDialog(ApplicationDialogType.QUESTION, informationText, "OK", "Cancel");
        if (isCancel == 1)
        {
            return;
        }
        
        running = true;
        
        toolFrame = robot.getFlange();
        motion = new SmartServo(robot.getCurrentJointPosition());
        //motion.setSpeedTimeoutAfterGoalReach(0.1);
        motion.overrideJointAcceleration(1.0);
        motion.setMinimumTrajectoryExecutionTime(0.0);
                
        toolFrame.moveAsync(motion);
        
        ISmartServoRuntime motion_rt = motion.getRuntime();
        //motion_rt.activateVelocityPlanning(true);
        
        jp = new JointPosition(robot.getJointCount());
        jv = new JointPosition(robot.getJointCount());
        
		//getLogger().info("Moving with constant joint velocity");
        System.out.println("Moving with constant joint veclocity");
		//jp.set( -90.0 * Math.PI / 180.0, -40.0 * Math.PI / 180.0, +50.0 * Math.PI / 180.0,
		//		+70.0 * Math.PI / 180.0, -30.0 * Math.PI / 180.0, -90.0 * Math.PI / 180.0, +150.0 * Math.PI / 180.0);
		jp.set( 0.0 * Math.PI / 180.0, 30.0 * Math.PI / 180.0, 0.0 * Math.PI / 180.0,
				-60.0 * Math.PI / 180.0, 0.0 * Math.PI / 180.0, 45.0 * Math.PI / 180.0, 0.0 * Math.PI / 180.0);
		
		jv.set( 0.0 * Math.PI / 180.0, 0.0 * Math.PI / 180.0, 0.0 * Math.PI / 180.0,
				5 * Math.PI / 180.0, 0.0 * Math.PI / 180.0, 0.0 * Math.PI / 180.0, 0.0 * Math.PI / 180.0);
		
		if (robot.isReadyToMove()) {
			motion_rt.setDestination(jp);
			while ( running && motion_rt.isDestinationReached() == false ) {
				ThreadUtil.milliSleep(10);
			}
		}
		//int _count = 0;
		int t = 0; 
		while (running && robot.isReadyToMove() && t < 250) {
			motion_rt.updateWithRealtimeSystem();
			for (int k = 0; k < 7; ++k) {
				jp.set( k, motion_rt.getCurrentJointDestination().get(k) + 0.02 * jv.get(k));
				//jp.set( k, motion_rt.getAxisQMsrOnController().get(k) + 0.02 * jv.get(k));
			}
			//motion_rt.setDestination(jp,jv);
			motion_rt.setDestination(jp);
			//if (_count % 50 == 0) {
			//	getLogger().info("Time step: " + motion_rt.getRemainingTime());
			//}
			ThreadUtil.milliSleep(20);
			//_count++;
			t++;
		}
	}
	
	@Override
	public void onApplicationStateChanged(RoboticsAPIApplicationState state) {
	    if (state == RoboticsAPIApplicationState.STOPPING) {
	      running = false;
	    }
	    super.onApplicationStateChanged(state);
	};
	public static void main (String [] args) {
		RoboticsAPIContext.useGracefulInitialization(true);
		new SingleInstanceChecker().start();
		CheckJointVelocityControl c = new CheckJointVelocityControl(RoboticsAPIContext.createFromResource(CheckJointVelocityControl.class, "RoboticsAPI.config.xml"));
		c.initialize();
		c.run();
	}
}
