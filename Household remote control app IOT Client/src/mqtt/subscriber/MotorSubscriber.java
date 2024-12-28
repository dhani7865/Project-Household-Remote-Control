package mqtt.subscriber;

import mqtt.subscriber.MotorSubscribeCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * This class is the Motor subscriber class which calls the motor subscriber callback class.
 * The motor subscriber callback class would then move the motor when valid tag has been used. 
 * MQTT would use the topic to determine which message goes to which client (subscriber). 
 * A topic is a structured string which can be used to filter and route messages.
 * Both publishers and subscribers are MQTT clients.
 * The publisher and subscriber would refer to whether the client has published the messages or subscribed to messages.
 * @author dhanyaal.
 */

// Creating public class for the motor subscriber.
public class MotorSubscriber {
	// Creating public static final string for the broker url and setting the url.
	public static final String BROKER_URL = "tcp://iot.eclipse.org:1883";
	//	 public static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";

	// Creating public static final string and setting the user id.
	public static final String userid = "16038287";
	//String clientId = userid + "-sub";
	// Creating string for the client id and setting the userid + sub_motor
	String clientId = userid + "-sub_Motor";

	// Declaring mqtt client
	private MqttClient mqttClient;

	/**
	 * Creating public MotorSubscriber method and calling the client id and the broker url.
	 *  Once the messages have been subscribed to the correct topic, it will then move the motor and blinds will open. 
	 *  If the correct tag has been used, it will then move the motor and open he blinds. 
	 *  If invalid tag has been used it wouldn't move the motor and the blinds will be closed. 
	 */
	public MotorSubscriber() {
		// Creating try and setting broker url and client id for the mqtt client.
		try {
			mqttClient = new MqttClient(BROKER_URL, clientId);
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(1);
		} // Close catch exception e
	} // Close MotorSubscriber method

	/**
	 *  Creating public void start method, which would then call the mootor subscriber callback class.
	 *  The motor subscriber callback class would then move the motor. 
	 *  Once the messages have been pubished to the correct topic, it will then subscribe to the correct topic.
	 *  The mqtt client would be connected to the client.
	 *  It will then subscribe to the correct topic and publish the data. 
	 *  It will then start subscribing the messages and move the motor, to open the blinds.
	 */
	public void start() {
		try {
			// setting the callback for the motor subscriberCallback
			mqttClient.setCallback(new MotorSubscribeCallback());
			mqttClient.connect(); // Connect to the mqtt client

			//Subscribe to correct topic
			final String topic = userid+"/motor";
			//  Subscribe to the mqtt client topic
			mqttClient.subscribe(topic);
			// print message onto the console
			System.out.println("Subscriber is now listening to "+topic);

		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(1);
		} // Close mqtt Exception
	} // Close public void start

	// Creating public main method
	public static void main(String... args) {
		// Calling the MotorSubscriber function
		final MotorSubscriber subscriber = new MotorSubscriber();
		subscriber.start(); // Starting the subscriber
	} // Close public static void main
} // Close public class motor subscriber