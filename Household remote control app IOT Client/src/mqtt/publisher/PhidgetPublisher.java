package mqtt.publisher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.eclipse.paho.client.mqttv3.*;

import com.google.gson.Gson;

import mqtt.utils.Utils;

/**
 * The PhidgetPublisher class would then detect when valid tag has been used. 
 * MQTT would use the topic to determine which message goes to which client. 
 * A topic is a structured string which can be used to filter and route messages.
 * Both publishers and subscribers are MQTT clients.
 * The publisher would then refer to whether the client has published the messages.
 * @author dhanyaal.
 */

public class PhidgetPublisher {

	public static final String BROKER_URL = "tcp://iot.eclipse.org:1883";
	// public static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";


	public static final String userid = "16038287"; // change this to be your student-id

	public static final String TOPIC_TEMPERATURE = userid +"/temperature";
	public static final String TOPIC_SLIDER     = userid + "/slider";
	public static final String TOPIC_MOTOR     = userid + "/motor";
	public static final String TOPIC_RFID       = userid + "/rfid_light";


	public static final String TOPIC_GENERIC    = userid + "/";

	// Creating string for the client id and setting the userid + sub_rfid
	//	String clientId = userid + "-sub_rfid";

	private MqttClient client;


	// Adding the mysql workbench username and assword, in order to connect to the database
	String user = "rashidd";
	String password = "rooSedef6";
	//  Usiing port 6306 instead of 3306 and connecting to the mudfoot server
	String url = "jdbc:mysql://mudfoot.doc.stu.mmu.ac.uk:6306/rashidd";



	// The address of server which will receive sensor data
	public static String sensorServerURL = "http://localhost:8080/Household_remote_control_app_IOT_Server/RFIDDao";


	/**
	 * Creating public PhidgetPublisher method and calling the client id and the broker url.
	 *  Once the messages have been published to the correct topic, it will then check if valid tag has been used and turn the light/heating on. 
	 *  If the correct tag has been used, it will then turn the light/heating on. 
	 *  If invalid tag has been used it wouldn't turn the light/heating on. 
	 */
	public PhidgetPublisher() {
		try {
			client = new MqttClient(BROKER_URL, userid+"rfid_Publisher");
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

	// Creating public string sendToServer, to send the json string to the server
	public String sendToServer(String RFIDJSON){
		// Creatng variable for url, HttpURLConnection and BufferedReader
		URL url;
		HttpURLConnection conn;
		BufferedReader rd = null;

		// Replacing invalid URL characters from json string
		try {
			RFIDJSON = URLEncoder.encode(RFIDJSON, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} // CLose catch UnsupportedEncodingException

		// Creating string full url for the sensor server url for the sensor data
		String fullURL = sensorServerURL + "?sensordata="+RFIDJSON;
		// Print message to the console
		System.out.println("Sending data to: "+fullURL);  // DEBUG confirmation message
		// Creating variable for line and result
		String line;
		String result = "";
		// Creating try for the url connection
		try {
			// Creating new url
			url = new URL(fullURL);
			// Creating http url connection to open the connection
			conn = (HttpURLConnection) url.openConnection();
			// Setting the request mehtod as get
			// Get is used to request data from a specified resource.
			conn.setRequestMethod("GET");
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			// Request response from server to enable URL to be opened
			while ((line = rd.readLine()) != null) {
				result += line;
			} // CLose while loop
			rd.close();  // CLose buffered reader
		} catch (Exception e) {
			e.printStackTrace();
		} // CLose catch Exception e
		// Return the reuslt
		return result;    	
	} // Close public string send to server

	private void start() {
		// Creating try to start publishing the messages
		try {
			MqttConnectOptions options = new MqttConnectOptions();
			options.setCleanSession(false);
			options.setMaxInflight(1000);
			//Subscribe to correct topic
			final String topic = userid+"/rfid";
			options.setAutomaticReconnect(true);
			options.setWill(client.getTopic(userid+"/LWT"), "I'm gone :(".getBytes(), 0, false);
			// connecting the cleint
			client.connect(options);

			//Publish the data forever
			while (true) {
				// Publish the temperature and put the thread to sleep
				publishTemperature(TOPIC_TEMPERATURE);
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
	public void publishTemperature(String tag) throws MqttException {
		// reating temperature topic
		final MqttTopic temperatureTopic = client.getTopic(TOPIC_TEMPERATURE);
		// Creating random numbers for the temperature
		final int temperatureNumber = Utils.createRandomNumberBetween(20, 30);
		// Setting the temperature number
		final String temperature = temperatureNumber + "°C";
		// Publishing the messages to the temperature topic
		temperatureTopic.publish(new MqttMessage(temperature.getBytes()));
		// print message out onto the console
		System.out.println("Published data. Topic: " + temperatureTopic.getName() + "  Message: " + temperature);
		/**
		 * If valid tag has been used turn the heating on, if it reaches certain temperature.
		 * The message will then get published to the correct topic, e.g. topic_temperature.
		 * Otherwise, invalid tag has been used and the heating won't turn on.
		 */
		if (tag.equals("1600ee15e9")) {
			// When the heating is on and valid tag has been used
			SensorData data = new SensorData("1600ee15e9","HEATING ON","none","none");
			Gson gson = new Gson();
			final MqttTopic temperatureTopic1 = client.getTopic(TOPIC_TEMPERATURE);
			temperatureTopic1.publish(new MqttMessage(gson.toJson(data, SensorData.class).getBytes()));
			System.out.println("Publishing the heating/temperture sequence, to turn the heating on");
			// Converting the data to json format
			String RFIDJSON = gson.toJson(data);
			// Sending the data to the server/database
			sendToServer(RFIDJSON);	
			System.out.println("Data has been added to the database");	
		}
		// Otherwise, invalid tag has been used and the heating won't turn on.
		else {
			// When the heating is off
			SensorData data2 = new SensorData("rfid","HEATING NOT ON","none", "none");
			Gson gson = new Gson();
			// Publish the message to the temperature topic
			temperatureTopic.publish(new MqttMessage(gson.toJson(data2).getBytes()));
			System.out.println("Published heating/temperature sequence, HEATING not on");
			// Converting the data to json format
			String RFIDJSON = gson.toJson(data2);
			// Sending the data to the server/database
			sendToServer(RFIDJSON);	
			System.out.println("Data has been added to the database");	
		} // CLose else
	} //Close publish temperture

	public void publishTemperture() throws MqttException {
		final MqttTopic tempertureTopic = client.getTopic(TOPIC_TEMPERATURE);
		System.out.println("Publishing message : TESTER to topic: "+tempertureTopic.getName());
		final String tempertureMessage = "HEATING ON";
		tempertureTopic.publish(new MqttMessage(tempertureMessage.getBytes()));
		System.out.println("Published data. Topic: " + tempertureTopic.getName() + "   Message: " + tempertureMessage);
	}

	// Creating publish rfid method where all the messages will be published to the correct topic, e.g. topic_rfid
	public void publishRfid(String rfidTag) throws MqttException {
		final MqttTopic rfidTopic = client.getTopic(TOPIC_RFID);
		final String rfid = rfidTag + "";
		rfidTopic.publish(new MqttMessage(rfid.getBytes()));
		System.out.println("Published data. Topic: " + rfidTopic.getName() + "   Message: " + rfid);

		/**
		 * If valid tag has been used turn the light on.
		 * The message will then get published to the correct topic, e.g. topic_rfid.
		 * Otherwise, invalid tag has been used and the light won't turn on.
		 */
		if (rfidTag.equals("1600ee15e9")) {
			// When the light is on and valid tag has been used
			SensorData data = new SensorData("1600ee15e9","LIGHT ON","none","none");
			Gson gson = new Gson();
			// publish the message to the rfid topic
			final MqttTopic rfidTopic1 = client.getTopic(TOPIC_RFID);
			rfidTopic1.publish(new MqttMessage(gson.toJson(data, SensorData.class).getBytes()));
			System.out.println("Publishing the light sequence, to turn the light on");
			// Converting the data to json format
			String RFIDJSON = gson.toJson(data);
			// Sending the data to the server/database
			sendToServer(RFIDJSON);	
			System.out.println("Data has been added to the database");	
		}
		// Otherwise, invalid tag has been used and the LIGHT won't turn on.
		else {
			// When the light is not on
			SensorData data2 = new SensorData("rfid","light NOT ON","none", "none");
			Gson gson = new Gson();
			// Publish the message to the light topic
			rfidTopic.publish(new MqttMessage(gson.toJson(data2).getBytes()));
			System.out.println("Published light sequence, light not on");
			// Converting the data to json format
			String RFIDJSON = gson.toJson(data2);
			// Sending the data to the server/database
			sendToServer(RFIDJSON);	
			System.out.println("Data has been added to the database");	
		} // CLose else
	} // CLose public void rfid method

	// Creating publish rfid method where all the messages will be published to the correct topic, e.g. topic_rfid
	public void publishRfid() throws MqttException {
		final MqttTopic rfidTopic = client.getTopic(TOPIC_RFID);
		System.out.println("Publishing message : TESTER to topic: "+rfidTopic.getName());
		final String rfidMessage = "LIGHT ON";
		rfidTopic.publish(new MqttMessage(rfidMessage.getBytes()));
		System.out.println("Published data. Topic: " + rfidTopic.getName() + "   Message: " + rfidMessage);
	}

	// Creating public void publish motor method, which would then publish everything to the motor topic
	// If valid tag is used move the motor, otherwise if invalid tag is used motor won't move and the blinds won't open
	public void publishMotor(String tagStr) throws MqttException {
		// Creating final mqtt topic and getting the motor topic and then print message out onto the console saying published data to the correct topic
		final MqttTopic motorTopic = client.getTopic(TOPIC_MOTOR);
		// Print message out onto the console
		System.out.println("Publishing message : "+tagStr + " to topic: "+motorTopic.getName());
		final String motorMessage = tagStr + "";
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
	} // CLose publish motor method


	// More generic publishing methods - avoids having to name every one
	public void publishSensor(String sensorValue, String sensorName) throws MqttException {
		final MqttTopic mqttTopic = client.getTopic(TOPIC_GENERIC + sensorName);
		final String sensor = sensorValue + "";
		mqttTopic.publish(new MqttMessage(sensor.getBytes()));
		System.out.println("Published data. Topic: " + mqttTopic.getName() + "   Message: " + sensor);
	}
	public void publishSensor(int sensorValue, String sensorName) throws MqttException {
		// same as string publisher, just converting int to String
		publishSensor(String.valueOf(sensorValue), sensorName);
	}
	public void publishSensor(float sensorValue, String sensorName) throws MqttException {
		// same as string publisher, just converting float to String
		publishSensor(String.valueOf(sensorValue), sensorName);
	}
}
