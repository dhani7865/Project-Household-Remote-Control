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

import org.eclipse.paho.client.mqttv3.MqttException;

import com.google.gson.Gson;
import com.phidget22.PhidgetException;
import com.phidget22.RFID;
import com.phidget22.RFIDTagEvent;
import com.phidget22.RFIDTagListener;
import com.phidget22.RFIDTagLostEvent;
import com.phidget22.RFIDTagLostListener;

//import IOTServer.RFIDDao;
import mqtt.publisher.PhidgetPublisher;

/**
 *  Creating rfid blind open class.
 *  Setting the user id
 *  If valid tag has been used open the blinds.
 *  Otherwise, invalid tag blinds  not open.
 *  The data will then be sent to the database. 
 */

// Creating public class for the rfid RFIDBlinds
public class RFIDBlinds  {
	// Creating a list of valid tags which are recognised
	final String[] tagsArray = { "1600ee15e9", "tag123", "tag9795","123456" };
	// Creating array where all the data will be stored
	ArrayList<String> tagsArray1 = new ArrayList<String>(Arrays.asList(tagsArray));

	// Calling the sensor data class and getting new sensor data
	SensorData RFID = new SensorData();

	// Declaring Json string
	String RFIDJSON = new String();


	// Declaring GSON utility object
	Gson gson = new Gson();


	// The address of server which will receive sensor data
	public static String sensorServerURL = "http://localhost:8080/Household_remote_control_app_IOT_Server/RFIDDao";


	// Creating main method and calling the rfid blind open method
	public static void main(String[] args) throws PhidgetException, SQLException {
		new RFIDBlinds();
	} // close main method


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


	// Creating method for the rfid blinds which throws phidget exception
	public RFIDBlinds() throws PhidgetException {
		// Creating variable for the phidgetPublisher and calling the method
		PhidgetPublisher publisher = new PhidgetPublisher(); // source in PhidgetPublisher.java

		RFID rfid = new RFID(); // Avarable for the rfid

		// Creating try to valodate the tag on the client side
		try {
			// Making the RFID Phidget able to detect loss or gain of an rfid card
			rfid.addTagListener(new RFIDTagListener() {
				// Creating public void for on tag for the rfid tag event
				public void onTag(RFIDTagEvent e) {
					// Creating string for the tag string
					String Tag = e.getTag();
					// Creating try to check if its the correct tag
					try {
						// If the tag is "1600ee15e9", publish the message
						if (tagsArray1.contains(Tag)) {
							// Print message, if it's the correct tag
							System.out.println("Tag read OPEN BLINDS: " + Tag);

							// Publish rfid and motor
							publisher.publishRfid(Tag);
							publisher.publishMotor(Tag);

							//Print statements to the console
							System.out.println("VALID TAG/USER");
							System.out.println("Room8 - BLINDS OPEN");

							// Seting the sensor name(tag), sensorvalue and user id for the valid tag/user)
							RFID.setSensorname("1600ee15e9");// Setting valid sensorname (tag)
							RFID.setSensorvalue("Room8, blinds open");// Setting the valid sensor value
							RFID.setUserid("16038287");// Setting valid user id
							// Converting the data to json format
							RFIDJSON = gson.toJson(RFID);
							// Sending the json data to the server/database
							sendToServer(RFIDJSON);						


							// inserting rfid data to the database and printing message out onto the console
							System.out.println("Success rrfid data have been added to the database!"); // printing success message
							// Creating variable for gson
							// Declaring gson object
							// Json is data format which would express data objects consisting of attribute value pairs. 
							Gson gson = new Gson();
							// Creating string for the json OBJECT and then converting it to json format
							String allRFIDDataJson = gson.toJson(RFIDJSON);


							// Print all rfid data in json format
							System.out.println(allRFIDDataJson);
							System.out.println("RFID data successfully in json format!"); // printing success message
						} // close if statement to check the tag string

						// Otherwise fail and print out invalid rfid tag to the console.
						else {
							// Print message, if it's the wrong tag
							System.out.println("Tag read: BLINDS NOT OPEN: " + Tag);

							//Print statements to the console
							System.out.println("INVALID TAG/USER");
							System.out.println("Room9 - NOT OPEN");
							// Setting the sensor name(tag), sensorvalue and user id for the invalid tag/user)
							RFID.setSensorname("4d004a9afa");// Setting invalid sensorname (tag)
							RFID.setSensorvalue("Room9- NOT OPEN");// Setting the invalid sensor value
							RFID.setUserid("16038287");// Setting valid user id for the invalid user which has been used
							// Converting the data to json format
							RFIDJSON = gson.toJson(RFID);
							// Sending json the data to the server/database
							sendToServer(RFIDJSON);						


							// inserting rfid data to the database and printing message out onto the console
							System.out.println("Success rrfid data have been added to the database!"); // printing success message
							// Creating variable for gson
							// Declaring gson object
							// Json is data format which would express data objects consisting of attribute value pairs. 
							Gson gson = new Gson();
							// Creating string for the json object and then converting it to json format
							String allRFIDDataJson = gson.toJson(RFIDJSON);

							// Print all rfid data in json format
							System.out.println(allRFIDDataJson);
							System.out.println("RFID data successfully in json format!"); // printing success message
						} // CLose if tags array
					} catch (MqttException mqtte) {
						mqtte.printStackTrace();
					} // Close catch mqtt exception
				} // Close public on tag
			}); // Close rfid add tag listener

			// Creating rfid add tag lost listener
			// This will then print out tag lost onto the console, when the tag has been lost.
			rfid.addTagLostListener(new RFIDTagLostListener() {
				// Creating public void for on tag, which has parameter RFIDTagLostEvent e
				public void onTagLost(RFIDTagLostEvent e) {
					// Creating string for the tag
					String tagStr = e.getTag();
					// If the tag has been lost, print message to the console
					System.out.println("Tag lost: " + tagStr);
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

			// Print message to the console
			System.out.println("\n\nGathering data for 15 seconds\n\n");
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


			// Attach to the sensor and start reading
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
	} // Close public rfid blinds method
} // Close public class RFIDBlinds