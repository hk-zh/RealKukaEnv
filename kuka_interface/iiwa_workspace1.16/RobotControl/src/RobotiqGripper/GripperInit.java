package RobotiqGripper;  
import javax.inject.Inject;  

import robotiq.gripper.twoFingersF85.RobotiqGripper2F85;

import com.kuka.roboticsAPI.controllerModel.Controller;
//import robotiq.gripper.twoFingersF85.RobotiqGripper2F85; 
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import com.kuka.common.ThreadUtil;

public class GripperInit extends RoboticsAPIApplication {
	@Inject  
	private Controller MasterController;
	private RobotiqGripper2F85 robotiqGripper; 
	int widthGlass= 50;
	int widthBottle= 60;  
	@Override
	public void initialize() {
		MasterController = (Controller) getContext().getControllers().toArray()[0]; 
		robotiqGripper= new RobotiqGripper2F85(MasterController); // The Gripper is activated automatically in the constructor
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
		System.out.println("full opened");
		
		robotiqGripper.fullyClose() ;
		robotiqGripper.waitForfullyClosed();
		System.out.println("full closed");

		robotiqGripper.fullyOpen() ;
		robotiqGripper.waitForfullyOpen();
		System.out.println("full opened");
	} 
}
