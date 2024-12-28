package Lightandheating;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
//import array list for all the rfid data
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import com.google.gson.Gson;
import com.phidget22.PhidgetException;
import com.phidget22.RFID;
import com.phidget22.RFIDTagEvent;
import com.phidget22.RFIDTagListener;
import com.phidget22.RFIDTagLostEvent;
import com.phidget22.RFIDTagLostListener;
import com.phidget22.VoltageRatioInput;
import com.phidget22.VoltageRatioInputVoltageRatioChangeEvent;
import com.phidget22.VoltageRatioInputVoltageRatioChangeListener;

import mqtt.publisher.PhidgetPublisher;


/**
 * The temperture class is the class which checks the temperture of the heatings.
 * If valid tag has been used the heatings will turn on
 * If invalid tag has been used it wouldn't turn the heaters on.
 * Slider has been created, which would allow the user to control the temperture of the heatings. 
 * The temperture sensor would turn the heatings on/off from the application. Witout this sensor, it wouldn't allow the user to control the heatings.
 * @author Dhanyaal.
 */
// Creating TempertureSensor class
public class TempertureSensor  {
	/**
	 *  Setting the user id
	 *  Also, setting the correct broker url and the correct topic where the messages will be publihed to.
	 */
	public static final String userid = "16038287";

	// Setting the correct broker url
	public static final String BROKER_URL = "tcp://iot.eclipse.org:1883";
	// Creating string for the client id and setting the userid + sub_rfid
	String clientId = userid + "-sub_rfid";

	private MqttClient client;

	// Creating a list of valid tags which are recognised
	final String[] tagsArray = { "1600ee15e9", "tag1", "tagi23","4d004a7bbf23" };
	// Creating array list where all the tags will be stored
	ArrayList<String> tagsArray1 = new ArrayList<String>(Arrays.asList(tagsArray));


	// Calling the sensor data class and getting new sensor data
	SensorData RFID = new SensorData();

	// Declaring Json string
	String RFIDJSON = new String();

	// Declaring GSON utility object
	Gson gson = new Gson();


	// The address of server which will receive sensor data
	public static String sensorServerURL = "http://localhost:8080/Household_remote_control_app_IOT_Server/RFIDDao";


	// Creating variables for the connection and statement
	Connection conn = null;
	Statement stmt;

	// Creating main method and calling the TempertureSensor method
	public static void main(String[] args) throws PhidgetException, SQLException, InterruptedException {
		new TempertureSensor();
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



	// Creating method for the TempertureSensor which throws phidget exception
	public TempertureSensor() throws PhidgetException, InterruptedException {
		// Creating variable for phidgetPublisher and calling the method
		PhidgetPublisher publisher = new PhidgetPublisher(); // source in PhidgetPublisher.java

		RFID rfid = new RFID(); // Avarable for the rfid

		//		VoltageRatioInput slider = new VoltageRatioInput();
		VoltageRatioInput temperture = new VoltageRatioInput();
		// This is the id of the PhidgetInterfaceKit (on back of device
		// Setting the device serial number for the temperture sensor and slider
		temperture.setDeviceSerialNumber(273100);
		//		slider.setDeviceSerialNumber(273100);


		//		// This is the channel the slider/temperture senor is connected to on the interface kit
		//		slider.setChannel(1); // plugged into channel 0
		//		slider.open(1000); // wait for 5 seconds to register sensor

		temperture.setChannel(0);
		temperture.open(1000); // wait for 5 seconds to register sensor


		//		// the VoltageChangeRatio listener will detect changes in the voltage ratio of the sensor
		//		// Voltage ratio is in the range 0-1. When a change is detected, the method is invoked
		//		slider.addVoltageRatioChangeListener(new VoltageRatioInputVoltageRatioChangeListener() {
		//			public void onVoltageRatioChange(VoltageRatioInputVoltageRatioChangeEvent e) {
		//				System.out.println("Slider Voltage Ratio Changed for temperture: "+ e.getVoltageRatio());
		//			}
		//		});
		//
		//		// Display initial value of slider voltage ratio
		//		double x;
		//		x = slider.getVoltageRatio();
		//		System.out.println("Start slider Voltage Ratio is "+x);

		try {
			// Making the RFID Phidget able to detect loss or gain of an rfid card
			rfid.addTagListener(new RFIDTagListener() {
				// Creating public void for on tag for the rfid tag event
				public void onTag(RFIDTagEvent e) {
					// Creating string for the tag string
					String tag = e.getTag();
					// Creating try to check if its thecorrect tag
					try {
						// If the tag is "1600ee15e9", publish the message
						if (tagsArray1.contains(tag)) {
							// Publish the temperture and calling the check tag method
							publisher.publishTemperature(tag);
							checkTag(tag); 
						} // Close if statement
						// Otherwise fail and print out invalid rfid tag to the console.
						else 
						{
							// Calling the check tag method
							checkTag(tag); 
						} // Close else
					} catch (MqttException mqtte) {
						mqtte.printStackTrace();
					} // Close catch mqtt exception
					catch (PhidgetException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} // Close catch exception e1
				} // Close public on tag
			}); // Close rfid add tag listener

			// Creating rfid add tag lost listener
			rfid.addTagLostListener(new RFIDTagLostListener() {
				// Creating public void for on tag, which has parameter RFIDTagLostEvent e
				public void onTagLost(RFIDTagLostEvent e) {
					// Creating string for the tag
					String tag = e.getTag();
					// If the tag has been lost, print message to the console
					System.out.println("Tag lost: " + tag);
				} // Close public void on tag listener
			}); // Close rfid addTagLostListener

			// Open and starting to detect rfid cards
			rfid.open(10000);  // wait 5 seconds for device to respond

			// Display info on currently connected devices
			System.out.println("Device Name " + rfid.getDeviceName());
			System.out.println("Serial Number " + rfid.getDeviceSerialNumber());
			System.out.println("Device Version " + rfid.getDeviceVersion());

			// Set the rfid AntennaEnabled to true
			rfid.setAntennaEnabled(true);

			// try to put the thread to sleep
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// Otherwise close the rfid, and print message to the console
			rfid.close();
			System.out.println("\nClosed RFID");

			//			slider.close();
			//			System.out.println("\nClosed slider Voltage Ratio Input");

			// Attach to the sensor and start reading
			try {      	                                
				System.out.println("\n\nGathering data for 15 seconds\n\n");
				Thread.sleep(150000);
			} catch (Exception ex) {
				ex.printStackTrace();
			} // Close catch Exception e
		} finally { // Cewatting finally
			// Close the sensor
			// Print message to the console
			System.out.println("Closed and exiting...");
		} // Close finally
	} // Close RFIDDoorOpener method


	// checking the tag if its the correct tag or not
	// If its wrong tag, print message to the console saying valid TAG!
	private void checkTag(String tag) throws PhidgetException, MqttPersistenceException, MqttException {
		// Creating string variable for rfid json
		String RFIDJSON = null;

		// if tag 1600ee15e9, correct tag and turn the heatings on
		if (tag.equals("1600ee15e9")) {
			//Print statements to the console
			System.out.println("VALID TAG/USER");
			System.out.println("Room 1 - HEATING ON");
			// Seting the sensor name(tag), sensorvalue and user id for the valid tag/user)
			RFID.setSensorname("1600ee15e9");// Setting valid sensorname (tag)
			RFID.setSensorvalue("HEATING ON");// Setting the valid sensor value
			RFID.setUserid("16038287");// Setting valid user id
			// Converting the data to json format
			RFIDJSON = gson.toJson(RFID);
			// Sending the data to the server
			sendToServer(RFIDJSON);						


			// inserting rfid data to the database and printing message out onto the console
			// printing success message
			System.out.println("Success rrfid data have been added to the database!");

			// Declaring gson object
			// Json is data format which would express data objects consisting of attribute value pairs. 
			Gson gson = new Gson();

			// Creating string for the json bject and then converting it to json format
			String allRFIDDataJson = gson.toJson(RFIDJSON);


			// Print all rfid data in json format
			System.out.println(allRFIDDataJson);
			System.out.println("RFID data successfully in json format!"); // printing success message



			// When valid tag has been used heating is on
			// Creating sensor data object for the valid tag which has been used, to turn the heating on
			SensorData RFID = new SensorData("1600ee15e9","HEATING ON","none","none");
			// Print message out onto the console
			System.out.println("Publishing the heating/temperture sequence, to turn the heating on");
		} // CLose if statement
		// Otherwise, if the tag id is not equal to 1600ee15e9, HEATING off
		else {
			// Print messages out onto the console
			System.out.println("INVALID TAG!");
			System.out.println("HEATING OFF!");

			// Setting the sensor name(tag), sensorvalue and user id for the invalid tag/user)
			RFID.setSensorname("4d004a5587");// Setting invalid sensorname (tag)
			RFID.setSensorvalue("HEATING OFF");// Setting the invalid sensor value
			RFID.setUserid("16038287");// Setting valid user id for the invalid user which has been used
			// Converting the data to json format
			RFIDJSON = gson.toJson(RFID);
			// Sending the data to the server
			sendToServer(RFIDJSON);						


			// inserting rfid data to the database and printing message out onto the console
			System.out.println("Success rfid data have been added to the database!"); // printing success message

			// Declaring gson object
			// Json is data format which would express data objects consisting of attribute value pairs. 
			Gson gson = new Gson();

			// Creating string for the json bject and then converting it to json format
			String allRFIDDataJson = gson.toJson(RFIDJSON);

			// Print all rfid data in json format
			System.out.println(allRFIDDataJson);
			System.out.println("RFID data successfully in json format!"); // printing success message

			// When invalid tag has been used and the heating is of
			// Creating sensor data object for the invalid tag which has been used
			SensorData RFID = new SensorData("RFID","HEATING OFF","none","none");
			// Print message out onto the console
			System.out.println("Publishing the heating/temperture sequence, heating not on");

		} // CLose else
	} // Close private void check tag method
} // Close temperture sensor class