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
import com.kuka.roboticsAPI.deviceModel.JointPosition;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.geometricModel.AbstractFrame;
import com.kuka.roboticsAPI.geometricModel.CartDOF;
import com.kuka.roboticsAPI.geometricModel.Frame;
import com.kuka.roboticsAPI.geometricModel.LoadData;
import com.kuka.roboticsAPI.geometricModel.ObjectFrame;
import com.kuka.roboticsAPI.geometricModel.Tool;
import com.kuka.roboticsAPI.geometricModel.World;
import com.kuka.roboticsAPI.geometricModel.math.XyzAbcTransformation;
import com.kuka.roboticsAPI.motionModel.controlModeModel.CartesianImpedanceControlMode;
import com.kuka.roboticsAPI.motionModel.controlModeModel.IMotionControlMode;
import com.kuka.roboticsAPI.motionModel.controlModeModel.PositionControlMode;
import Controller.logger;
import robot.SunriseConnector;
import utility.SingleInstanceChecker;


public class Test extends RoboticsAPIApplication{
	
	private LBR _lbr;
	private ObjectFrame toolFrame;
    private Tool _toolAttachedToLBR;
    private LoadData _loadData;
    private ISmartServoLINRuntime _theSmartServoLINRuntime = null;
    
    private static final String TOOL_FRAME = "toolFrame";
    private static final double[] TRANSLATION_OF_TOOL = { 0, 0, 100 };
    private static final double MASS = 0;
    private static final double[] CENTER_OF_MASS_IN_MILLIMETER = { 0, 0, 100 };

    private static final int NUM_RUNS = 600;
    private static final double AMPLITUDE = 70;
    private static final double FREQENCY = 0.6;
    private double [] startPosition = new double [] {0., 0.712, 0., -1.26, 0.,
            1.17, 0.};
    private logger log = new logger();
	public Test(RoboticsAPIContext context) {
		super(context);
	}
    private logger _getLogger() {
    	return this.log;
    }
    
    public void moveToInitialPosition()
    {
        _lbr.move(ptp(new JointPosition(startPosition)).setJointVelocityRel(0.1));
        /* Note: The Validation itself justifies, that in this very time instance, the load parameter setting was
         * sufficient. This does not mean by far, that the parameter setting is valid in the sequel or lifetime of this
         * program */
        if (!ServoMotion.validateForImpedanceMode(_lbr))
        {
            _getLogger()
                    .info("Validation of torque model failed - correct your mass property settings");
            _getLogger()
                    .info("Servo motion will be available for position controlled mode only, until validation is performed");
        }
    }
    
	@Override
	public void run() {
		_getLogger().info("Move to start position.");
		moveToInitialPosition();
		
		 final PositionControlMode positionCtrlMode = new PositionControlMode();
		 
		 runSmartServoLINMotion(positionCtrlMode);
		
	}
	protected void runSmartServoLINMotion(final IMotionControlMode controlMode) {
        AbstractFrame initialPosition = _lbr.getCurrentCartesianPosition(_lbr.getFlange());
        SmartServoLIN aSmartServoLINMotion = new SmartServoLIN(initialPosition);
        _lbr.moveAsync(aSmartServoLINMotion.setMode(controlMode));
        _theSmartServoLINRuntime = aSmartServoLINMotion.getRuntime();
        StatisticTimer timing = new StatisticTimer();
        _theSmartServoLINRuntime.updateWithRealtimeSystem();
        Frame aFrame = _theSmartServoLINRuntime.getCurrentCartesianDestination(_lbr.getFlange());
        System.out.println(aFrame.getX() + " " + aFrame.getY() + " " + aFrame.getZ());
        long startTimeStamp = System.nanoTime();
        Frame destFrame = new Frame(aFrame);
        destFrame.setX(-100);
        destFrame.setY(0);
        destFrame.setZ(0);

        _theSmartServoLINRuntime.setDestination(destFrame);
        ThreadUtil.milliSleep(1000);
//        ThreadUtil.milliSleep(100);
//        _theSmartServoLINRuntime.updateWithRealtimeSystem();
//        Frame currentPosition1 = _theSmartServoLINRuntime.getCurrentCartesianPosition(_toolAttachedToLBR.getDefaultMotionFrame());
//        System.out.println(currentPosition1.getX() + " " + currentPosition1.getY() + " " + currentPosition1.getZ());
//        ThreadUtil.milliSleep(80);
//        _theSmartServoLINRuntime.updateWithRealtimeSystem();
//        Frame currentPosition2 = _theSmartServoLINRuntime.getCurrentCartesianPosition(_toolAttachedToLBR.getDefaultMotionFrame());
//        System.out.println(currentPosition2.getX() + " " + currentPosition2.getY() + " " + currentPosition2.getZ());
        

        
        
        
        
        
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
        toolFrame = aTransformation;
		
	}
	public static void main (String [] args) {
		RoboticsAPIContext.useGracefulInitialization(true);
		new SingleInstanceChecker().start();
		Test t = new Test(RoboticsAPIContext.createFromResource(Test.class, "RoboticsAPI.config.xml"));
		t.initialize();
		t.run();
	}
}
