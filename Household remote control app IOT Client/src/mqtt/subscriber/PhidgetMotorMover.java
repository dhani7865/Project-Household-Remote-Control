package mqtt.subscriber;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.phidget22.PhidgetException;
import com.phidget22.RCServo;
import com.phidget22.RCServoPositionChangeEvent;
import com.phidget22.RCServoPositionChangeListener;
import com.phidget22.RCServoTargetPositionReachedEvent;
import com.phidget22.RCServoTargetPositionReachedListener;
import com.phidget22.RCServoVelocityChangeEvent;
import com.phidget22.RCServoVelocityChangeListener;


/**
 * Singleton is an object that can only be created once in any session. 
 * This class would then move the motor, depending on the tag which has been used.
 * If valid tag has been used, the motor will then move and the blinds are open. 
 * If invalid tag has been used, the blinds won't open.
 * The client id will be used when a message has been published to the topic. 
 * @author dhanyaal
 */

// Creating phidget motor mover class
public class PhidgetMotorMover {
	// Singleton implementation to allow multiple callbacks to the code
	static RCServo servo = null;
	// Setting the user id
	public static final String userid = "16038287";
	// Calling the broker url
	public static final String BROKER_URL = "tcp://iot.eclipse.org:1883";
	//	public static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";

	// Calling the user id and the motor topic
	public static final String TOPIC_MOTOR     = userid + "/motor_open";
	private static MqttClient client;


	/**
	 *  This gets access to a motor via the singleton.
	 * This would check to see if the servo has a value. 
	 *  If it doesn't, it will create one. If there is one, it is sent back.
	 *  Also, calling the broker url and the user id, which the motor will publish to.
	 *  Otherwise, return the servo.
	 * @return
	 */
	// Creating public static rc servo method which gets instance and throws mqtt exception
	public static RCServo getInstance() throws MqttException {
		client = new MqttClient(BROKER_URL, userid+"-motor_Publisher");
		System.out.println("In singleton constructor");
		// If the servo is equal to null, then calling the phidget motor mover class and moving the motor.
		if(servo == null) {
			servo = PhidgetMotorMover();
		} // CLose if stteemnt
		// Return the servo
		return servo;
	} // Close public static rc servo method 

	/**
	 * 	Creating new instance of servo board and start listening for motor changes.
	 *  This method would only be called once when first constructing a servo instance.
	 * @return
	 */
	private static  RCServo PhidgetMotorMover() {
		// Creating try for construcing the motor mover
		try {
			System.out.println("Constructing MotorMover");
			// Calling the RCServo method
			servo = new RCServo();

			// Creating addVelocityChangeListener for the servo
			servo.addVelocityChangeListener(new RCServoVelocityChangeListener() {
				// Creating public void on velocity change
				public void onVelocityChange(RCServoVelocityChangeEvent e) {
					//System.out.println("Velocity Changed: " + e.getVelocity());
				} // Close public void on velocity change
			}); // Close addVelocityChangeListener

			// Creating addPositionChangeListener, which would check if the position has been changed. 
			servo.addPositionChangeListener(new RCServoPositionChangeListener() {
				// Creating public void onPositionChange
				public void onPositionChange(RCServoPositionChangeEvent e) {
					//System.out.println("Position Changed: " + e.getPosition());
				} // Close public void on position change
			}); // lose addPositionChangeListener

			// Creating addTargetPositionReachedListener, which would then check if the target positoon has been recieved
			servo.addTargetPositionReachedListener(new RCServoTargetPositionReachedListener() {
				// Creating public void onTargetPositionReached for when the target position has been reached. 
				public void onTargetPositionReached(RCServoTargetPositionReachedEvent e) {
					//System.out.println("Target Position Reached: " + e.getPosition());
				} // Close public void onTargetPositionReached
			}); // Close addTargetPositionReachedListener


			// Start listening for motor interaction
			servo.open(2000);
		} catch (PhidgetException e) {
			e.printStackTrace();
		} // close catch
		return servo;
	} // Close private static rc servo            

	/**
	 *  This method would move the motor position and move the motor,
	 *  depending on the tag which has been used.
	 *  If valid tag has been used the motor will be moved.
	 *  If invalid tag has been used motor won't move.
	 * @param motorPosition
	 */
	public static void moveServoTo(double motorPosition) {
		try {
			// Get the servo that is available and moving the motor position.
			PhidgetMotorMover.getInstance();
			System.out.println("moving to "+motorPosition);
			servo.setTargetPosition(motorPosition);
			servo.setEngaged(true);
		} catch (PhidgetException | MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Close catch mqtt exception
	} //Close public static void moveServoTo
} // Close public class PhidgetMotorMover