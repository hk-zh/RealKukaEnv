package Controller;
import javax.inject.Inject; 

import utility.SingleInstanceChecker;
import com.kuka.common.ThreadUtil;
import com.kuka.roboticsAPI.RoboticsAPIContext;
import com.kuka.roboticsAPI.controllerModel.Controller;
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;


public class GripperMotions extends RoboticsAPIApplication{
	@Inject
	private Controller MasterController;
	private RobotiqGripper2F85 robotiqGripper; 
	private int initialPositionHex = 10;
	private double destPosition = 0;
	public GripperMotions(RoboticsAPIContext context) {
		super(context);
	}
	@Override
	public void initialize() {
		MasterController = (Controller) getContext().getControllers().toArray()[0]; 
		robotiqGripper= new RobotiqGripper2F85(MasterController); 
	} 
	
	@Override
	public void run(){
		// TODO Auto-generated method stub
		robotiqGripper.moveToHex(this.initialPositionHex, robotiqGripper.minSpeed, robotiqGripper.minForce);
		robotiqGripper.waitForMoveEnd();
		System.out.println("move to initial position");
	}
	public void moveTo(double pos) {
		if (pos >= 0.06) {
			this.robotiqGripper.moveToCM(0, this.robotiqGripper.maxSpeed, this.robotiqGripper.minForce);
		} else {
			double position = 1- Math.max(pos/0.06, 0.3);
			this.robotiqGripper.moveToCM((int) (position * 85), this.robotiqGripper.maxSpeed, this.robotiqGripper.minForce);
		}
	}
	public void gripperClose() {
		this.robotiqGripper.fullyClose();
		this.robotiqGripper.waitForfullyClosed();
	}
	
	public void gripperOpen() {
		this.robotiqGripper.fullyOpen();
		this.robotiqGripper.waitForfullyOpen();
	}
	
	public double getCurrentGripperPosition() {
		double gripperPos = (double) this.robotiqGripper.getPosition();
		return this.robotiqGripper.mapDecimalValueToCentimeters(gripperPos)/85 * 0.06 ;
	}
	
	public double getCurrentGripperVelocity() {
		double gripperVel = (double)this.robotiqGripper.getSpeed() / 255;
		return gripperVel;
	}
	public void setAction(double action) {
		double currentGripperPosition = this.getCurrentGripperPosition();
		this.destPosition = currentGripperPosition + action;
	}
	
	public boolean hasObject() {
		return this.robotiqGripper.gOBJ().equals("10");
	}
	
	public int getCurrent() {
		return this.robotiqGripper.getCurrent();
	}
	public void step() {
		this.moveTo(this.destPosition);
	}
	
	
	public static void main (String [] args) {
		RoboticsAPIContext.useGracefulInitialization(true);
		new SingleInstanceChecker().start();
		GripperMotions t = new GripperMotions(RoboticsAPIContext.createFromResource(GripperMotions.class, "RoboticsAPI.config.xml"));
		t.initialize();
		t.run();
		t.moveTo(0.12);
		System.out.println(t.getCurrentGripperPosition());
	}
	
}
