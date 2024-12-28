package Lightandheating;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
//import array list for all the rfid data
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import com.google.gson.Gson;
import com.phidget22.DigitalOutput;
import com.phidget22.PhidgetException;
import com.phidget22.RFID;
import com.phidget22.RFIDTagEvent;
import com.phidget22.RFIDTagListener;
import com.phidget22.RFIDTagLostEvent;
import com.phidget22.RFIDTagLostListener;
import com.phidget22.VoltageRatioInput;

//import IOTServer.RFIDDao;
import mqtt.publisher.PhidgetPublisher;

/**
 *  Creating rfidLIGHT class.
 *  Setting the user id
 *  If valid tag has been used turn the light on.
 *  Otherwise, invalid tag light  not on.
 *  The data will then be sent to the database/server and be viewed on the browser/database. 
 */

// Creating public class for the RFIDLight
public class RFIDLight  {

	/**
	 *  Setting the user id
	 *  Also, setting the correct broker url and the correct topic where the messages will be published to.
	 */
	public static final String userid = "16038287";
	// Setting the correct broker url
	public static final String BROKER_URL = "tcp://iot.eclipse.org:1883";
	//public static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";
	// Creating string for the client id and setting the userid + sub_rfid
	String clientId = userid + "-sub_rfid";

	private MqttClient client;

	// Creating a list of valid tags which are recognised
	final String[] tagsArray = { "1600ee15e9", "tag123", "tag9795","123456"};
	// Creating array where all the data will be stored
	ArrayList<String> tagsArray1 = new ArrayList<String>(Arrays.asList(tagsArray));

	// Creating DigitalOutput for the dig out
	DigitalOutput digOut  = new DigitalOutput();


	// Calling the sensor data class and getting new sensor data
	SensorData RFID = new SensorData();


	// Declaring Json string
	String RFIDJSON = new String();


	// Declaring GSON utility object
	Gson gson = new Gson();


	// The address of server which will receive sensor data
	public static String sensorServerURL = "http://localhost:8080/Household_remote_control_app_IOT_Server/RFIDDao";


	// Creating main method and calling the RFIDLight method
	public static void main(String[] args) throws PhidgetException, SQLException {
		new RFIDLight();
	} // close main method


	// Creating public string sendToServer, to send the json string to the server/database
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
			// Requesting response from server to enable URL to be opened
			while ((line = rd.readLine()) != null) {
				result += line;
			} // CLose while loop
			rd.close();  // CLose buffered reader
		} catch (Exception e) {
			e.printStackTrace();
		} // CLose catch Exception e
		// Return the result
		return result;    	
	} // Close public string send to server


	// Creating method for the RFIDLight which throws phidget exception
	public RFIDLight() throws PhidgetException {
		// Creating variable for the phidgetPublisher and calling the method
		PhidgetPublisher publisher = new PhidgetPublisher();

		RFID rfid = new RFID(); // variable for the rfid

		// Creating voltage ratio input for the light sensor
		VoltageRatioInput light = new VoltageRatioInput();

		//		 This is the id of the PhidgetInterfaceKit (on back of device)
		// This is the serial number of the interface kit which is attached to the light sensor
		light.setDeviceSerialNumber(273100);

		// This is the channel where the light sensor is connected to on the interface kit
		light.setChannel(0); //  plugged into channel 0
		light.open(1000); // wait for 5 seconds to register sensor

		// set the DigitalOutput channel (0 or 1 on RFID board) for the led light
		digOut.setChannel(0);
		// open for writing
		digOut.open(2000);

		// Creating try to validate the tag on the client side
		try {
			// Making the RFID able to detect loss or gain of an rfid card
			rfid.addTagListener(new RFIDTagListener() {
				// Creating public void for on tag for the rfid tag event
				public void onTag(RFIDTagEvent e) {
					// Creating string for the tag string
					String tag = e.getTag();
					// Creating try to check if its the correct tag
					try {
						// If the tag is "1600ee15e9", publish the message
						if (tagsArray1.contains(tag)) {
							// Print message, if it's the correct tag
							System.out.println("Tag read: LIGHT ON: " + tag);

							// Publish rfid
							publisher.publishRfid(tag);

							// Calling the turnOnLight method
							turnOnLight(true); 
						} // close if statement to check the tag string

						// Otherwise, fail and print out invalid rfid tag to the console.
						else {
							// Print message, if it's the INVALID tag
							System.out.println("Tag read: LIGHT NOT ON: " + tag);

							// Calling the turnOnLight method
							turnOnLight(false); 
						} // Close else
					} catch (MqttException mqtte) {
						mqtte.printStackTrace();
					} // Close catch mqtt exception
				} // Close public on tag
			}); // Close rfid add tag listener

			// Creating rfid add tag lost listener
			rfid.addTagLostListener(new RFIDTagLostListener() {
				// Creating public void for on tag, which has parameter RFIDTagLostEvent e
				public void onTagLost(RFIDTagLostEvent e) {
					// Creating string for the tag
					String tag = e.getTag();
					// If the tag has been lost, print message to the console
					System.out.println(tag);
				} // Close public void on tag listener
			}); // Close rfid addTagLostListener

			// Open and starting to detect rfid cards
			rfid.open(50000);  // wait 25 seconds for device to respond

			// Display info on currently connected devices
			System.out.println("Device Name " + rfid.getDeviceName());
			System.out.println("Serial Number " + rfid.getDeviceSerialNumber());
			System.out.println("Device Version " + rfid.getDeviceVersion());

			// Set the rfid AntennaEnabled to true
			rfid.setAntennaEnabled(true);

			// Print message to the console
			System.out.println("\n\nGathering data for 15 seconds\n\n");
			// try to put the thread to sleep
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// Otherwise, close the rfid, and print message to the console
			rfid.close();
			System.out.println("\nClosed RFID");


			// Attach to the sensor and start reading for 15 seconds
			try {      	                                
				System.out.println("\n\nGathering data for 15 seconds\n\n");
				Thread.sleep(150000);
			} catch (Exception ex) {
				ex.printStackTrace();
			} // Close catch Exception e
		} finally { // Creating finally
			// Close the sensor
			// Print message to the console
			System.out.println("Closed and exiting...");
		} // Close finally
	} // Close rfid light method

	// checking the tag if its the correct tag or not
	// If its wrong tag, print message to the console saying UNKOWN TAG!
	private void turnOnLight(boolean lightState) throws MqttPersistenceException, MqttException{
		// method to set state of light as true
		if (lightState==true) {
			try {
				// Setting the digout as true, if valid tag has been used
				digOut.setState(true);


				//Print statements to the console
				System.out.println("VALID TAG/USER");
				System.out.println("Room 101 - LIGHT ON");
				// Seting the sensor name(tag), sensorvalue and user id for the valid tag/user)
				RFID.setSensorname("1600ee15e9");// Setting valid sensorname (tag)
				RFID.setSensorvalue("LIGHT ON");// Setting the valid sensor value
				RFID.setUserid("16038287");// Setting valid user id
				// Converting the data to json format
				RFIDJSON = gson.toJson(RFID);
				// Sending the data to the server/database
				sendToServer(RFIDJSON);						

				// inserting rfid data to the database and printing message out onto the console
				System.out.println("Success rrfid data have been added to the database!"); // printing success message

				// Creating string for the json object and then converting it to json format
				String allRFIDDataJson = gson.toJson(RFIDJSON);

				// Print all rfid data in json format
				System.out.println(allRFIDDataJson);
				System.out.println("RFID data successfully in json format!"); // printing success message


				// When valid tag has been used light is on
				// Creating sensor data object for the valid tag which has been used
				SensorData RFID = new SensorData("1600ee15e9","LIGHT ON","none","none");
				// Gson object to convert the data to json format
				Gson gson = new Gson();
				// Print message out onto the console
				System.out.println("Publishing the light sequence, to turn the light on");

			} catch (PhidgetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // CLose catch exception e
		} // CLose if statement
		// Otherwise, if the tag id is not equal to 1600ee15e9, light not on
		else {
			// Creating try to check if invalid tag has been used
			try {
				// Print messages out onto the console
				System.out.println("Tag read: LIGHT OFF!: " + lightState);
				System.out.println("INVALID TAG!");
				System.out.println("LIGHT OFF");

				// Digout and set the state to false
				digOut.setState(false);

				// Setting the sensor name(tag), sensorvalue and user id for the invalid tag/user)
				RFID.setSensorname("4d004a5587");// Setting invalid sensorname (tag)
				RFID.setSensorvalue("light NOT on");// Setting the invalid sensor value
				RFID.setUserid("16038287");// Setting valid user id for the invalid user which has been used

				// Converting the data to json format
				RFIDJSON = gson.toJson(RFID);
				// Sending the data to the server/database
				sendToServer(RFIDJSON);						


				// inserting rfid data to the database and printing message out onto the console
				System.out.println("Success rrfid data have been added to the database!"); // printing success message


				// Creating string for the json object and then converting it to json format
				String allRFIDDataJson = gson.toJson(RFIDJSON);

				// Print all rfid data in json format
				System.out.println(allRFIDDataJson);
				System.out.println("RFID data successfully in json format!"); // printing success message


				// When invalid tag has been used and the light is of
				// Creating sensor data object for the invalid tag which has been used
				SensorData RFID = new SensorData("RFID","LIGHT OFF","none","none");
				//				// Publish all messages to rfid topic
				//				final MqttTopic rfidTopic = client.getTopic(TOPIC_RFID);
				//				// Convert all the sensor data object to json format
				//				rfidTopic.publish(new MqttMessage(gson.toJson(RFID).getBytes()));
				// Print message out onto the console
				System.out.println("Published light sequence, light not on");

			} catch (PhidgetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // CLose catch exception e
		} // CLose else
	} // Close private void turn on light method
} // Close public class RFID light class