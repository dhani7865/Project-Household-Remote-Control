package mqtt.publisher;

import org.eclipse.paho.client.mqttv3.*;

import mqtt.utils.Utils;


public class PhidgetPublisherRfidSolution {

	public static final String BROKER_URL = "tcp://iot.eclipse.org:1883";
	// public static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";

	public static final String userid = "16038287"; // change this to be your student-id

	public static final String TOPIC_SLIDER     = userid + "/slider";
	public static final String TOPIC_RFID       = userid + "/rfid";
	public static final String TOPIC_MOTOR      = userid + "/motor";

	public static final String TOPIC_GENERIC    = userid + "/";

	private MqttClient client;


	public PhidgetPublisherRfidSolution() {

		try {
			client = new MqttClient(BROKER_URL, userid+"-publisher");
			// create mqtt session
			MqttConnectOptions options = new MqttConnectOptions();
			options.setCleanSession(false);
			options.setWill(client.getTopic(userid + "/LWT"), "I'm gone :(".getBytes(), 0, false);
			client.connect(options);
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}


	public void publishSlider(double d) throws MqttException {
		final MqttTopic sliderTopic = client.getTopic(TOPIC_SLIDER);
		final String slider = d + "";
		sliderTopic.publish(new MqttMessage(slider.getBytes()));
		System.out.println("Published data. Topic: " + sliderTopic.getName() + "   Message: " + slider);
	}
	public void publishRfid(String rfidTag) throws MqttException {
		final MqttTopic rfidTopic = client.getTopic(TOPIC_RFID);
		final String rfid = rfidTag + "";
		rfidTopic.publish(new MqttMessage(rfid.getBytes()));
		System.out.println("Published data. Topic: " + rfidTopic.getName() + "   Message: " + rfid);
	}

	public void publishRfid() throws MqttException {
		final MqttTopic rfidTopic = client.getTopic(TOPIC_RFID);
		System.out.println("Publishing message : TESTER to topic: "+rfidTopic.getName());
		final String rfidMessage = "LIGHT ON";
		rfidTopic.publish(new MqttMessage(rfidMessage.getBytes()));
		System.out.println("Published data. Topic: " + rfidTopic.getName() + "   Message: " + rfidMessage);
	}

	// Creating public void publish motor method, which would then publish everything to the motor topic
	// If valid tag is used move the motor, otherwise if invalid tag is used motor won't move
	public void publishMotor(double d) throws MqttException {
		// Creating final mqtt topic and getting the motor topic and then print message out onto the console saying published data to the correct topic
		final MqttTopic motorTopic = client.getTopic(TOPIC_MOTOR);
		// Print message out onto the console
		System.out.println("Publishing message : "+d + " to topic: "+motorTopic.getName());
		final String motorMessage = d + "";
		motorTopic.publish(new MqttMessage(motorMessage.getBytes()));
		System.out.println("Published data. Topic: " + motorTopic.getName() + "   Message: " + motorMessage);
	} // Close publishMotor method

	// Creating another publishMotor method which throws mqtt exception
	public void publishMotor() throws MqttException {
		// Creating final mqtt topic and getting the motor topic and then print message out onto the console saying published data to the correct topic
		final MqttTopic motorTopic = client.getTopic(TOPIC_MOTOR);
		// Print message out onto the console
		System.out.println("Publishing message : TESTER to topic: "+motorTopic.getName());
		// If valid tag is used this message will then be printed out onto the console
		final String motorMessage = "OPEN BLINDS";
		motorTopic.publish(new MqttMessage(motorMessage.getBytes()));
		System.out.println("Published data. Topic: " + motorTopic.getName() + "   Message: " + motorMessage);
	}  // CLose publish motor method


	// More generic publishing methods - avoids having to name every one
	public void publishSensor(String sensorValue, String sensorName) throws MqttException {
		final MqttTopic mqttTopic = client.getTopic(TOPIC_GENERIC + sensorName);
		final String sensor = sensorValue + "";
		System.out.println("publishing via generic publishsensor");
		mqttTopic.publish(new MqttMessage(sensor.getBytes()));
		System.out.println("Published data. Topic: " + mqttTopic.getName() + "   Message: " + sensor);
	}
	public void publishSensor(int sensorValue, String sensorName) throws MqttException {
		// same as string publisher, just convert int to String
		publishSensor(String.valueOf(sensorValue), sensorName);
	}
	public void publishSensor(float sensorValue, String sensorName) throws MqttException {
		// same as string publisher, just convert float to String
		publishSensor(String.valueOf(sensorValue), sensorName);
	}
}
