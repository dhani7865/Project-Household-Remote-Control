package mqtt.publisher;

import org.eclipse.paho.client.mqttv3.*;

import mqtt.utils.Utils;

// Creating publisher class to publish the messages to the topic
public class Publisher {
	// Setting the broaker url
	// public static final String BROKER_URL = "tcp://broker.hivemq.com:1883";
	public static final String BROKER_URL = "tcp://iot.eclipse.org:1883";
	//public static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";

	// Setting the user id
	public static final String userid = "16038287"; // change this to be your student-id

	// Creating topics for brightness, temperture, slider, rfid and motor
	public static final String TOPIC_BRIGHTNESS 	= userid + "/brightness";
	public static final String TOPIC_TEMPERATURE = userid +"/temperature";
	public static final String TOPIC_SLIDER     = userid + "/slider";
	public static final String TOPIC_RFID       = userid + "/rfid";
	public static final String TOPIC_MOTOR     = userid + "/motor";

	// Creating variable for the mqtt client
	private MqttClient client;

	// Creating public publisher method
	public Publisher() {
		// Creating try and calling the broker url and the user id
		try {
			client = new MqttClient(BROKER_URL, userid);
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(1);
		} // CLose catch
	} // Close public publisher method

	// Creating private void start method to start publishing the messages to the topic
	private void start() {
		// Creating try to start publishing the messages
		try {
			MqttConnectOptions options = new MqttConnectOptions();
			options.setCleanSession(false);
			options.setMaxInflight(1000);
			options.setAutomaticReconnect(true);
			options.setWill(client.getTopic(userid+"/LWT"), "I'm gone :(".getBytes(), 0, false);
			// connecting the cleint
			client.connect(options);

			//Publish the data forever
			while (true) {
				// Publish the temperture and put the thread to sleep
				publishTemperature();
				Thread.sleep(2000);
			} // Close while loop true
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} // Close catch InterruptedException e
	} // Close private void start method

	// Creating private void publish temperature which throws MqttException
	private void publishTemperature() throws MqttException {
		// Creating temperature topic
		final MqttTopic temperatureTopic = client.getTopic(TOPIC_TEMPERATURE);
		// Creating random numbers for the temperature
		final int temperatureNumber = Utils.createRandomNumberBetween(20, 30);
		// Setting the temperature number
		final String temperature = temperatureNumber + "°C";
		// Publishing the messages to the temperature topic
		temperatureTopic.publish(new MqttMessage(temperature.getBytes()));
		// print message out onto the console
		System.out.println("Published data. Topic: " + temperatureTopic.getName() + "  Message: " + temperature);
	} //Close publish temperature


	// Creating publish void for the publish rfid method
	public void publishRfid(String rfidTag) throws MqttException {
		// Getting the rfid topic
		final MqttTopic rfidTopic = client.getTopic(TOPIC_RFID);
		final String rfid = rfidTag + "";
		rfidTopic.publish(new MqttMessage(rfid.getBytes()));
		System.out.println("Published data. Topic: " + rfidTopic.getName() + "   Message: " + rfid);
	} // CLose publish rfid method



	public static void main(String... args) {
		final Publisher publisher = new Publisher();
		publisher.start();
	}
}
