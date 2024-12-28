package mqtt.subscriber;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import com.google.gson.Gson;

import mqtt.utils.Utils;
/**
 * The motor subscriber callback class would then move the motor when valid tag has been used. 
 * MQTT would use the topic to determine which message goes to which client (subscriber). 
 * A topic is a structured string which can be used to filter and route messages.
 * Both publishers and subscribers are MQTT clients.
 * The publisher and subscriber would then refer to whether the client has published the messages or subsribed to messages.
 * @author dhanyaal.
 */

// Creating public class for the motor subscriber callback class which implements the mqtt callback.
public class MotorSubscribeCallback implements MqttCallback {
	/**
	 *  Setting the user id
	 *  Also, setting the correct broker url and the correct topic which the messages will subsribe to.
	 */
	public static final String userid = "16038287";
	// Setting the correct broker url
	public static final String BROKER_URL = "tcp://iot.eclipse.org:1883";
	//	public static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";

	public static final String TOPIC_MOTOR     = userid + "/motor_blind";
	private MqttClient client;

	// Adding the mysql workbench username and assword, in order to connect to the database
	String user = "rashidd";
	String password = "rooSedef6";
	//  Usiing port 6306 instead of 3306 and connecting to the mudfoot server
	String url = "jdbc:mysql://mudfoot.doc.stu.mmu.ac.uk:6306/rashidd";



	// The address of server which will receive sensor data
	public static String sensorServerURL = "http://localhost:8080/Household_remote_control_app_IOT_Server/RFIDDao";


	/**
	 * Creating public MotorSubscribeCallback method and calling the client id and the broker url.
	 *  Once the messages have been subscribed to the correct topic, it will then move the motor. 
	 *  If the correct tag has been used, it will then move the motor. The blinds will then open.
	 *  If invalid tag has been used it wouldn't move the motor. Blinds will then not open.
	 */
	public MotorSubscribeCallback()
	{
		try {
			client = new MqttClient(BROKER_URL, userid+"-motor_Publisher");
			// create mqtt session
			MqttConnectOptions options = new MqttConnectOptions();
			options.setCleanSession(false);
			options.setWill(client.getTopic(userid + "/LWT"), "I'm gone :(".getBytes(), 0, false);
			client.connect(options);
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(1);
		} // Close catch MqttException e
	} // lose MotorSubscribeCallback method

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
			// Setting the request method as get
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

	@Override
	//This is called when the connection is lost. We could reconnect here.
	public void connectionLost(Throwable cause) {
	}

	@Override
	/**
	 * This is the message arrived method, which would then check if  correct message (Tag id)
	 *  has been received. If the correct tag id has been received, it will then move the motor and open the blnds of your home.
	 *  Otherwise, invalid tag id (message), motor won't move and blinds wont open.
	 *  The PhidgetMotorMover class has been called as this is the class, which would then move the motor.
	 */
	public void messageArrived(String topic, MqttMessage message) throws MqttException {
		// Declaring Json string
		String RFIDJSON = new String();
		System.out.println("Message arrived. Topic: " + topic + "  Message: " + message.toString());
		// Move motor to open, then shut after pausing
		System.out.println("DEBUG: Trying to move motor");
		PhidgetMotorMover.moveServoTo(180.0);
		System.out.println("Waiting until motor at position 180");


		// When the blinds are open
		SensorData data = new SensorData("1600ee15e9","OPEN BLINDS","16038287");
		Gson gson = new Gson();
		final MqttTopic motorTopic = client.getTopic(TOPIC_MOTOR);
		motorTopic.publish(new MqttMessage(gson.toJson(data, SensorData.class).getBytes()));
		System.out.println("Publishing the blind sequence, to open the blinds");
		// Converting the data to json format
		RFIDJSON = gson.toJson(data);
		// Sending the data to the server/database
		sendToServer(RFIDJSON);	
		System.out.println("Data has been added to the database");

		// When the blinds are closed
		Utils.waitFor(5);
		PhidgetMotorMover.moveServoTo(0.0);
		System.out.println("wait complete");
		Utils.waitFor(2);

		// When the blinds are closed
		SensorData data2 = new SensorData("motor","BLINDS CLOSED","none");
		motorTopic.publish(new MqttMessage(gson.toJson(data2).getBytes()));
		System.out.println("Published locking sequence, blinds not open");

		// Converting the data to json format
		RFIDJSON = gson.toJson(data2);
		// Sending the data to the server
		sendToServer(RFIDJSON);
		System.out.println("Data has been added to the database");


		// Creating if statement for the user id and then printing message out onto the console.
		if ((userid+"/LWT").equals(topic)) {
			// Print message out onto the console letting the user know the sensor has gone.
			System.err.println("Sensor gone!");
		} // CLose if statement
	} // Close public void message arrived method


	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		//no-op
	} // Closing the deliveryComplete method
} // Close public void MotorSubscribeCallback class