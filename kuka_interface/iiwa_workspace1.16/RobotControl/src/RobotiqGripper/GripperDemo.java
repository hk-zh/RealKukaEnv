package RobotiqGripper; 
import javax.inject.Inject; 

import robotiq.gripper.twoFingersF85.RobotiqGripper2F85;
import utility.SingleInstanceChecker;

import com.kuka.common.ThreadUtil;
import com.kuka.roboticsAPI.controllerModel.Controller;


//import robotiq.gripper.twoFingersF85.RobotiqGripper2F85; 
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;

import com.kuka.roboticsAPI.RoboticsAPIContext;

public class GripperDemo extends RoboticsAPIApplication {
	@Inject  
	private Controller MasterController;
	private RobotiqGripper2F85 robotiqGripper; 
	int widthWorkPiece= 50; 
	public GripperDemo(RoboticsAPIContext context) {
		super(context);
	}
	@Override
	public void initialize() {
		MasterController = (Controller) getContext().getControllers().toArray()[0]; 
		robotiqGripper= new RobotiqGripper2F85(MasterController); 
	} 
	@Override
	public void run() {  
		robotiqGripper.deactivate();
		System.out.println("Gripper Deactivated"); 
		ThreadUtil.milliSleep(50); 
		
		robotiqGripper.activate();
		robotiqGripper.waitForInitialization(); 
		System.out.println("Gripper Activated"); 
		
		robotiqGripper.fullyOpen() ;
		robotiqGripper.waitForfullyOpen(); 
		System.out.println("Gripper Fully Opened for the first time xxx" );  

		robotiqGripper.fullyClose() ;
		robotiqGripper.waitForfullyClosed() ;
		System.out.println("Gripper Fully Closed for the first time" ); 

		robotiqGripper.fullyOpen();
		robotiqGripper.waitForfullyOpen();
		System.out.println("Gripper Fully Opened for the second time" ); 

		robotiqGripper.moveToCM(this.widthWorkPiece, RobotiqGripper2F85.avgSpeed, RobotiqGripper2F85.lowForce); // Width in CM [0-85]
		robotiqGripper.waitForMoveEnd(); 
		System.out.println("Gripper moved to " + this.widthWorkPiece +" cm"); 
		
		robotiqGripper.fullyOpen() ;
		robotiqGripper.waitForfullyOpen();
		System.out.println("Gripper Fully Opened for the third time" ); 
		
		robotiqGripper.moveToHex(128,RobotiqGripper2F85.maxSpeed,RobotiqGripper2F85.lowForce); // Width in Hexa [4-227] 
		System.out.println("Gripper moved to " + 128 +" in hex"); 
		robotiqGripper.waitForMoveEnd();  
		
		robotiqGripper.fullyOpen() ;
		robotiqGripper.waitForfullyOpen(); 
		System.out.println("Gripper Fully Opened for the fourth time" );   
	} 
	public static void main (String args[]) {
		RoboticsAPIContext.useGracefulInitialization(true);
		new SingleInstanceChecker().start();
		GripperDemo t = new GripperDemo(RoboticsAPIContext.createFromResource(GripperDemo.class, "RoboticsAPI.config.xml"));
		t.initialize();
		t.run();
	}
}
