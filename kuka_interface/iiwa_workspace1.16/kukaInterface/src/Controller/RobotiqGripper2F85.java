package Controller;

import com.kuka.common.ThreadUtil;
import com.kuka.roboticsAPI.controllerModel.Controller;
import com.kuka.generated.ioAccess.RobotiqGripperIOGroup;

public class RobotiqGripper2F85 extends RobotiqGripperIOGroup {
	public static final int gripperMaxWidth = 85;
	public static final int gripperMinWidth = 0;  
	public static final int gripperMaxHexPosition = 227;
	public static final int gripperMinHexPosition = 4;    
	public static final int maxSpeed = 255;
	public static final int avgSpeed = 128;
	public static final int lowSpeed = 15;
	public static final int minSpeed = 0;
	public static final int maxForce = 255;
	public static final int avgForce = 128;
	public static final int lowForce = 32;
	public static final int minForce = 1; 
	public static final int waitDelayMS = 50;
	public static final int initDelayMS = 500;
	private logger log = new logger();
	
	public RobotiqGripper2F85(Controller controller) {
		super(controller);
		this.initialize();
		
	}
	public void initialize() {
		this.deactivate();
		this.activate();
		this.waitForInitialization();
	}
	
	public int moveToCM(int width, int speed, int force) {
		boolean error = false; 
		// ****** Check that we have values in acceptable ranges *******
		if ( width < gripperMinWidth || width > gripperMaxWidth  ) {
			System.err.println("Error Gripper! Width should be in the [0,85cm] range "); 
			error = true; 
		}  else { this.setPositionRequestCM(width); }
		//*****
		if (speed < minSpeed || speed > maxSpeed) {
			System.err.println("Error Gripper! Speed should be in the [0,255] range "); 
			error = true;  
		} else { this.setSpeed(speed); }
		//*****
		if (force < minForce || force > maxForce) {
			System.err.println("Error Gripper! Force should be in the [0,255] range "); 
			error = true; 
		} else { this.setForce(force); } 
		// ******  ******* ******* ******* ******* ******* ******* *******  
		ThreadUtil.milliSleep(waitDelayMS); 
		if (!error) {
			goToPosition();
			return 1;
		} else {
			return -1;
		}
	}
	
	public int moveToHex(int hexPosition, int speed,int force) { 
		boolean error = false; 
		// ****** Check we have values in acceptable ranges *******
		if ( hexPosition < gripperMinHexPosition || hexPosition > gripperMaxHexPosition  ) 
		{
			System.err.println("Error Gripper! Width should be in the [0,85cm] range "); 
			error = true; 
		}  else { this.setPositionRequest(hexPosition) ; }

		if (speed < minSpeed || speed > maxSpeed) {
			System.err.println("Error Gripper! Speed should be in the [0,255] range "); 
			error = true;  
		} else { this.setSpeed(speed); }

		if (force < minForce || force > maxForce) {
			System.err.println("Error Gripper! Force should be in the [0,255] range "); 
			error = true; 
		} else { this.setForce(force); }

		// ******  ******* ******* ******* ******* ******* ******* *******  
		ThreadUtil.milliSleep(waitDelayMS); 
		if (!error) {
			goToPosition();
			return 1;
		} else {
			return -1;
		}  
	}
	
	public void fullyClose()  { 
		moveToCM( gripperMaxWidth, lowSpeed, minForce);
	} 
	
	public void fullyOpen() { 
		moveToCM( gripperMinWidth, lowSpeed,minForce); 
	} 
	
	/* ############################ Functions for waiting ############################*/
	
	public void waitForMoveEnd() { 
		// To be sure the movement ended, we should either reach the requested position with no object detected, or stop before reaching position and detecting object
		while (!positionReached() && (!objectDetectedWhileOpening() &&  !objectDetectedWhileClosing()) )  
			ThreadUtil.milliSleep(waitDelayMS);  
	}
	
	public void waitForObjectDetected() { 
		while (!objectDetectedWhileOpening() && !objectDetectedWhileClosing() )  
			ThreadUtil.milliSleep(waitDelayMS);  
	} 
	
	public void waitForfullyOpen() { 
		while (!isFullyOpen() && !objectDetectedWhileOpening()) 
			ThreadUtil.milliSleep(waitDelayMS);  
	} 
	
	public void waitForfullyClosed() {  
		while (!isFullyClosed () && !objectDetectedWhileClosing())  
			ThreadUtil.milliSleep(waitDelayMS);  
	}  
	
	private void waitForInitialization() {
		// wait while the "ACTIVATION IN PROGRASS": gSTA (bits 4 & 5 of Byte 0 of "GripperStatus" is equal 0x01
		boolean activationInProgress = true; 
		while(activationInProgress) 
		{  
			//System.out.println("Gripper initializing..."); 
			char[] gripperStatusByte = this.getGripperStatusCharArray();  
			if (gripperStatusByte[5] == '0' && gripperStatusByte[4] =='1') {
				activationInProgress = true;
			} else {
				activationInProgress = false;
			}
			ThreadUtil.milliSleep(initDelayMS); 
		}  
		System.out.println("Gripper initialized"); 
	} 
	
	
	/* ############################################################################### */
	
	
	
	/* ########## Functions to check if it has reached the destination. ############# */
	
	public boolean positionReached() {  
		// Check that the requestedPositionEcho (Byte [3] of Input) is equal to the actualPosition (Byte[4])
		// the "==" Operator doesn't work on Workbench Java's Version 
		if (this.getPositionRequestEcho().equals (this.getPosition() )) 
			return  true; 
		else   
			return  false;   
	}  
	public boolean isFullyClosed() {  
		//  the actual position Byte[4] == gripperMaxHexPosition the max value 
		if ( this.getPosition() >= gripperMaxHexPosition ) 
			return  true; 
		else   
			return  false;   
	}  
	public boolean isFullyOpen() {  
		//  the actual position Byte[4] == 229 the max value 
		if ( this.getPosition() <= gripperMinHexPosition ) 
			return  true; 
		else   
			return  false;   
	} 
	
	/* ############################################################################### */
	
	
	
	/* ###################### Functions for object detection ######################### */
	
	public boolean objectDetectedWhileOpening() {
		// of gGTO == 0 ignore if gGTO ==1 check
		if (gGTO() == '0') {
			return false; // If there was no goto request ignore
		} else {
			if (gOBJ().equals("01")) {
				return true; // if gOBJ =="0x01" fingers are have stopped due to a contact while opening before requested position
			} else {
				return false; // It's other cases of detection
			}
		} 
	}
	
	public boolean objectDetectedWhileClosing() {
		// of gGTO == 0 ignore if gGTO ==1 check
		if (gGTO() == '0') {
			return false; // If there was no goto request ignore
		} else {
			if (gOBJ().equals("10")) {
				System.out.println("Object Detected!");
				return true;
				// if gOBJ =="0x02" fingers are have stopped due to a contact while closing before requested position
			} else {
				return false; // 
			}
		} 
	} 
	
	public char gGTO() { 
		char[] gripperStatusByte = this.getGripperStatusCharArray();
		return gripperStatusByte[3];
	}	
	
	public String gOBJ() { 
		char[] gripperStatusByte = this.getGripperStatusCharArray();  
		return String.valueOf(gripperStatusByte[7]) + String.valueOf(gripperStatusByte[6]);
	}   
	
	/* ############################################################################### */
	
	
	

	public static int mapCentimetersToDecimalValue (int cm) {
		if (cm <= gripperMaxWidth)  
			return ( (int) Math.round ( ( (gripperMaxWidth- cm)*255 ) /gripperMaxWidth ) ) ;
		else  
			return -1;  
	} 
	
	public double mapDecimalValueToCentimeters (double dec) {
		if (dec > 255) {
			return (double) 85;
		} else {
			return (double) gripperMaxWidth - (double)dec / 255 * gripperMaxWidth;
		}
	}
	
	public void setPositionRequestCM (int cm) {
		// we use 85 complement because for the gripper 0x00 is Fully Open 85cm and 0xFF is fully closed 0cm
		this.setPositionRequest( mapCentimetersToDecimalValue (gripperMaxWidth - cm));
	}  
	
	private void goToPosition() { 
		this.setActionRequest(9); //"ActionRequest" byte <-- â€­0000 1001 : rACT bit = 1 && rGTO = 1
	} 
	
	
	public void deactivate() {
		this.setActionRequest(0); //"ActionRequest" byte <-- â€­0000 0000: rACT bit =  0
	} 
	private void activate() {
		log.info("Activating Gripper...");
		this.setActionRequest(1);//"ActionRequest" byte <-- â€­â€­0000 0001 : rACT bit = 1â€¬
	}

	public char[] getGripperStatusCharArray() { 
		return  this.intToBinaryCharArray (this.getGripperStatus());  
	}
	public char[] intToBinaryCharArray (int integerValue) {   
		byte gripperInputByte = (byte) integerValue; // 
		String binaryString = String.format("%8s", Integer.toBinaryString(gripperInputByte & 0xFF)).replace(' ', '0'); // get a string of 8 bits 
		char[] stringToCharArray = binaryString.toCharArray(); // Big Endian    R--> Left
		char[] binaryCharArray =  stringToCharArray.clone();// Little Endian L--> Right 
		for (int j=0; j< stringToCharArray.length; j++) { 
			binaryCharArray [j] =  stringToCharArray[(stringToCharArray.length-1)- j ]  ;   
		}   
		return binaryCharArray;  
	}  

}
