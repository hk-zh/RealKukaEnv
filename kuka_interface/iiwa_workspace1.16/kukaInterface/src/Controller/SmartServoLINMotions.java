package Controller;

import static com.kuka.roboticsAPI.motionModel.BasicMotions.ptp;

import com.kuka.common.StatisticTimer;
import com.kuka.common.StatisticTimer.OneTimeStep;
import com.kuka.common.ThreadUtil;
import com.kuka.connectivity.motionModel.smartServo.ServoMotion;
import com.kuka.connectivity.motionModel.smartServoLIN.ISmartServoLINRuntime;
import com.kuka.connectivity.motionModel.smartServoLIN.SmartServoLIN;
import com.kuka.roboticsAPI.RoboticsAPIContext;
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.geometricModel.AbstractFrame;
import com.kuka.roboticsAPI.geometricModel.CartDOF;
import com.kuka.roboticsAPI.geometricModel.Frame;
import com.kuka.roboticsAPI.geometricModel.LoadData;
import com.kuka.roboticsAPI.geometricModel.ObjectFrame;
import com.kuka.roboticsAPI.geometricModel.Tool;
import com.kuka.roboticsAPI.geometricModel.math.XyzAbcTransformation;
import com.kuka.roboticsAPI.motionModel.controlModeModel.CartesianImpedanceControlMode;
import com.kuka.roboticsAPI.motionModel.controlModeModel.IMotionControlMode;
import com.kuka.roboticsAPI.motionModel.controlModeModel.PositionControlMode;
import com.kuka.roboticsAPI.deviceModel.JointPosition;
import Controller.logger;
import robot.SunriseConnector;
import utility.SingleInstanceChecker;


public class SmartServoLINMotions extends RoboticsAPIApplication{
	
	private LBR _lbr;
	private ObjectFrame toolFrame;
    private Tool _toolAttachedToLBR;
    private LoadData _loadData;
    private ISmartServoLINRuntime _theSmartServoLINRuntime = null;
    
    private static final String TOOL_FRAME = "toolFrame";
    private static final double[] TRANSLATION_OF_TOOL = { 0, 0, 150 };
    private static final double MASS = 0;
    private static final double[] CENTER_OF_MASS_IN_MILLIMETER = { 0, 0, 150 };
    private static final double[] MAX_TRANSLATION_VELOCITY = { 150, 150, 150 };
    private logger log = new logger();
    private static final double Dt = 0.05;
    private double [] startPosition = new double [] {0., Math.PI / 180 * 30., 0., -Math.PI / 180 * 60., 0.,
            Math.PI / 180 * 90., 0.};
    
    private Frame destPosition;
    private Frame initialPosition;
    private double Z_lower_bound= 70.0;
    private double X_upper_bound = 740;
    private double X_lower_bound = 440;
    
	public SmartServoLINMotions(RoboticsAPIContext context) {
		super(context);
	}
    private logger _getLogger() {
    	return this.log;
    }
    
    public void moveToInitialPosition()
    {
    	//please make sure startPosition makes the tool towards the ground!
    	//_getLogger().info("move to initial position");
    	_lbr.move(ptp(new JointPosition(startPosition)).setJointVelocityRel(0.1));
    	initialPosition = _lbr.getCurrentCartesianPosition(_lbr
                .getFlange());
    }
    public void resetInitialPosition() {
    	_getLogger().info("resetInitialPosition");
    	_theSmartServoLINRuntime.setDestination(initialPosition);
    	ThreadUtil.milliSleep(2000);
    }
	@Override
	public void run() {
		_getLogger().info("Move to start position.");
		moveToInitialPosition();
		
		final PositionControlMode positionCtrlMode = new PositionControlMode();
		 
		AbstractFrame initialPosition = _lbr.getCurrentCartesianPosition(_lbr
                .getFlange());
		SmartServoLIN aSmartServoLINMotion = new SmartServoLIN(initialPosition);
        aSmartServoLINMotion.setMinimumTrajectoryExecutionTime(20e-3);
        aSmartServoLINMotion.setMaxTranslationVelocity(MAX_TRANSLATION_VELOCITY);
		_lbr.moveAsync(aSmartServoLINMotion.setMode(positionCtrlMode));
        _theSmartServoLINRuntime = aSmartServoLINMotion.getRuntime();
		
	}
	public void setStartPosition(double [] startPosition) {
		System.arraycopy(startPosition, 0, this.startPosition, 0, 7);
	}
	public double [] getCurrentPosition() {
		_theSmartServoLINRuntime.updateWithRealtimeSystem();
		Frame currentPosition = _theSmartServoLINRuntime.getCurrentCartesianPosition(toolFrame);
		return new double [] {currentPosition.getX(), currentPosition.getY(), currentPosition.getZ()};
	}
	
	public double [] getCurrentVelocity() {
		double [] p1 = this.getCurrentPosition();
		ThreadUtil.milliSleep((long)(Dt*1000));
		double [] p2 = this.getCurrentPosition();
		return new double [] {(p2[0] - p1[0])/Dt, (p2[1] - p1[1])/Dt,(p2[2] - p1[2])/Dt};
	}
	
	public double [] getCurrentJointsPosition () {
		JointPosition j = _lbr.getCurrentJointPosition();
		double [] toReturn = new double [7];
		for (int i = 0; i < 7; i++) {
			toReturn[i] = j.get(i);
		}
		return toReturn;
	}
	public double [] getCurrentJointsVelocity() {
		double [] j1 = this.getCurrentJointsPosition();
		ThreadUtil.milliSleep((long)(Dt*1000));
		double [] j2 = this.getCurrentJointsPosition();
	    double [] toReturn = new double [7];
		for (int i =0; i < 7;i++) {
			toReturn[i] = (j2[i] - j1[i]) / Dt;
		}
		return toReturn;
		
	}
	
	public void setAction(double [] action) {
		_theSmartServoLINRuntime.updateWithRealtimeSystem();
		Frame currentPosition = _theSmartServoLINRuntime.getCurrentCartesianPosition(_lbr.getFlange());
		if (currentPosition.getZ() + action[2] <= this.Z_lower_bound) {
			action[2] = -currentPosition.getZ() + this.Z_lower_bound;
		}
		if (currentPosition.getX() + action[0] >= this.X_upper_bound) {
			action[0] = this.X_upper_bound - currentPosition.getX();
		}
		if (currentPosition.getX() + action[0] <= this.X_lower_bound) {
			action[0] = this.X_lower_bound - currentPosition.getX();
		}
		destPosition = new Frame(currentPosition);
		destPosition.setX(-action[0]);
		destPosition.setY(action[1]);
		destPosition.setZ(-action[2]);
	}
	
	public void step() {
		_theSmartServoLINRuntime.setDestination(destPosition);
	}
	
	@Override
	public void initialize() {
		_lbr = getContext().getDeviceFromType(LBR.class);
        // Create a Tool by Hand this is the tool we want to move with some mass
        // properties and a TCP-Z-offset of 100.
        _loadData = new LoadData();
        _loadData.setMass(MASS);
        _loadData.setCenterOfMass(
                CENTER_OF_MASS_IN_MILLIMETER[0], CENTER_OF_MASS_IN_MILLIMETER[1],
                CENTER_OF_MASS_IN_MILLIMETER[2]);
        _toolAttachedToLBR = new Tool("Tool", _loadData);

        XyzAbcTransformation trans = XyzAbcTransformation.ofTranslation(
                TRANSLATION_OF_TOOL[0], TRANSLATION_OF_TOOL[1],
                TRANSLATION_OF_TOOL[2]);
        ObjectFrame aTransformation = _toolAttachedToLBR.addChildFrame(TOOL_FRAME
                + "(TCP)", trans);
        _toolAttachedToLBR.setDefaultMotionFrame(aTransformation);
        // Attach tool to the robot
        _toolAttachedToLBR.attachTo(_lbr.getFlange());
        this.toolFrame = aTransformation;
		
	}
	public static void main (String [] args) {
		RoboticsAPIContext.useGracefulInitialization(true);
		new SingleInstanceChecker().start();
		SmartServoLINMotions t = new SmartServoLINMotions(RoboticsAPIContext.createFromResource(SmartServoLINMotions.class, "RoboticsAPI.config.xml"));
		t.initialize();
		t.run();
	}
}
