
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;


/**
 * import connection to this class and also creating new class called RFIDDao
 * creating public array list to get all getAlldata
 *  I have created a array to store all the sensor data into a array.
 * @author Dhanyaal Rashid
 */

// Creating web servlet for RFIDDao and creating public class which extends HttpServlet
@WebServlet("/RFIDDao")
public class RFIDDao extends HttpServlet {
	// Creating private static final serialVersionUID
	private static final long serialVersionUID = 1L;
	// Declaring GSON utility object
	// Connection = null and statement.
	Gson gson = new Gson();
	Connection conn = null;
	static Statement stmt;
	// Creating public static final string for the user id
	public static final String userid = "16038287";
	// Creating topic for the server and broker url
	public static final String TOPIC_SERVER = userid + "/server";
	public static final String BROKER_URL = "tcp://iot.eclipse.org:1883";
	//	public static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";


	// Creating public void init method for ServletConfig which throws ServletException
	public void init(ServletConfig config) throws ServletException {
		// init method is run once at the start of the servlet loading
		// This will load the driver and establish a connection
		super.init(config);

		// Adding the mysql workbench username and assword, in order to connect to the database
		String user = "rashidd";
		String password = "rooSedef6";
		//  Usiing port 6306 instead of 3306 and connecting to the mudfoot server
		String url = "jdbc:mysql://mudfoot.doc.stu.mmu.ac.uk:6306/rashidd";


		// Loading the database driver
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			System.out.println(e);
		} // close catch exception e

		// Creating try to get a connection with the user/pass
		try {
			conn = DriverManager.getConnection(url, user, password);
			// Print messages out onto the console
			System.out.println("Sensor to DB  server is up and running\n");
			System.out.println("DEBUG: Connection to database successful.");
			stmt = conn.createStatement();
			//System.out.println("debug: Statement created " + stmt);
		} catch (SQLException se) {
			se.printStackTrace();
			System.out.println(se);
			System.out.println("\nDid you alter the lines to set user/password in the sensor server code?");
		} // Close catch sql exception
	} // init()

	// Creating destroy method
	public void destroy() {
		try {
			conn.close();
		} catch (SQLException se) {
			System.out.println(se);
		} // Close catch sql exception se
	} // destroy()

	/***
	 * Creating rfiddao method,
	 * Super is a method which is used inside a sub-class method, to call a method in the super class. 
	 * Private method of the super-class would not be called. 
	 * Only public and protected methods can be called by the super keyword.
	 * It is also used by class constructors to invoke constructors of its parent class.
	 */
	public RFIDDao() {
		// Calling the super method
		super();
		/*try {
		client = new MqttClient(BROKER_URL, userid+"-server_publisher");
		// create mqtt session
		MqttConnectOptions options = new MqttConnectOptions();
		options.setCleanSession(false);
		options.setWill(client.getTopic(userid + "/LWT"), "I'm gone :(".getBytes(), 0, false);
		client.connect(options);
	} catch (MqttException e) {
		System.out.println("mqtt");
		e.printStackTrace();
		System.exit(1);
	}*/
	} // Close public RFIDDao method


	/**
	 * Creating do get method, to insert data into the database.
	 * Then retrieve the data, depending on sensorname
	 * Creating user valid method which would then check if valid user is using valid tag to turn the light/heating on and open/close the blinds.
	 *  Retrieve all RFID Data.
	 * @return
	 * @throws SQLException
	 */

	// doGet is a method which supports the servlet HTTP GET requests 
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		// Declaring a SensorData object to hold the incoming data
		SensorData oneSensor = new SensorData("1600ee15e9", "IGHT ON", "16038287");

		// Checking to see whether the client is requesting data or sending it
		String getdata = request.getParameter("getdata");

		// if no getdata parameter, client is sending data
		if (getdata == null) {
			// getdata is null, therefore it is receiving data
			// Extracting the parameter data holding the sensordata
			String sensorJsonString = request.getParameter("sensordata");
			// Extracting the parameter data holding the sensorname
			String data = request.getParameter("sensorname");
			// creating boolean value and setting it to false at the start
			boolean valid = false;
			// if sensor json string not equal to null get the sensorname and calling the sensor data class
			if (sensorJsonString != null) {
				oneSensor = gson.fromJson(sensorJsonString,SensorData.class);
				// Print message out onto the console saying the sensorname data has been sent
				System.out.println("Data sent"+oneSensor.getSensorname());
				// Creating variable for valid and calling  is user valid method and getting the sensorname
				valid = isUserValid(oneSensor.getSensorname());
			} // Close if statement for sensor json string
			// If valid user has been used return valid user/tag and turn light/heating on or open/close the blinds
			if (valid == true) {
				// Print valid statement out onto the console
				System.out.println("valid user/tag");

				// send command to turn the light/heating on or open the blinds
				PrintWriter writer = response.getWriter();
				writer.println("true"); // Writer = true
				writer.close();
			} // Close if statement for valid ==true
			// Otherwise, return invalid user/tag and light/heating not on or blinds not open
			else {
				// print message out onto the console
				System.out.println("invalid user/tag");
			} // Close else


			// Problem if sensordata parameter not sent, or is invalid json
			if (sensorJsonString != null) {
				// Converting the json string to an object of type SensorData
				// Also, calling the sensordata class
				oneSensor = gson.fromJson(sensorJsonString, SensorData.class);

				// Update sensor values and send back response and updating the data in the database
				PrintWriter out = response.getWriter();
				// Calling the updateSensorTable method
				try {
					updateSensorTable(oneSensor);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				out.close(); // close out
			} // endif sensorJsonString not null
		} // end if getdata is null
		else { // Otherwise retrieve and return data in json format
			// retrive data sensor by sensorname parameter
			String sensorname = request.getParameter("sensorname");
			// If sensorname not equal to null, retrieve the data
			if (sensorname != null) {
				PrintWriter out = response.getWriter();
				out.println(retrieveSensorData(sensorname));
				out.close();
			} // Close if statement
		} // CLose else
	} // CLose protected void doget method

	// Creating do post method
	//doPost is used for HTTP POST requests 
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Post is same as Get, so pass on parameters and do same
		doGet(request, response);
	} // CLose protected void dopost

	/**
	 *  Creating boolean to check if a valid user is trying to turn the light/heating on or opening the blinds, using specific sensor name (tagid)  = 1600ee15e9
	 *  Selecting everything from the sensor controls valid table where the id is '1600ee15e9'
	 *  This will then check in the second table if its valid user or not and then return success message.
	 * @param sensorname
	 * @return
	 */
	public static boolean isUserValid(String sensorname) {
		// print message out onto the console
		System.out.println("valid start"+sensorname);
		// Creating try and catch for the sql query
		try {
			// select everything from the SensorControlsValid  table where the sensorname is valid tag (1600ee15e9)
			String sql = "SELECT * FROM SensorControlsValid WHERE sensorname='"+sensorname+"';";

			// execute sql query
			System.out.println("DEBUG: statement correct" +sql);
			System.out.println(sql); // print the query result
			System.out.println(">> Debug: All RFID data displayed");

			ResultSet rs = stmt.executeQuery(sql);

			// Creating while loop for result set next and creating if statement to get the string for the result set
			while (rs.next()) {
				if (rs.getString("sensorname").equals(sensorname)) return true;
			} // CLose while loop
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} // CLose catch
		// Otherwise return false if invalid user is trying to turn the light/heating on or opening the blinds
		return false;
	} // CLose buplic booelan is user valid method


	// Creating static update sensor table to insert the status of the light/heating/blinds to the database
	public static void updateSensorTable(SensorData oneSensor) throws Exception {
		// Creating try and catch for the sql query, to insert the data to the database
		try {
			// Creating the INSERT statement from the parameters
			// setting the time inserted to be the current time on database server
			String updateSQL = 
					"insert into SensorControls(userid,sensorname,sensorvalue) VALUES ('" +oneSensor.getUserid() +"',"
							+ "'"+ oneSensor.getSensorname() + "', "
							+ "'"+ oneSensor.getSensorvalue() + "')";


			// Print out debug message to the console
			System.out.println("DEBUG: Update: " + updateSQL);
			// Execute query
			stmt.executeUpdate(updateSQL);
			//publish_to_Android(oneSensor);
			// Print debug message to the console
			System.out.println("DEBUG: Update successful ");
		} catch (SQLException se) {
			// Problem with update, return failure message
			System.out.println(se);
			System.out.println("\nDEBUG: Update error - see error trace above for help. ");
			return;
		} // CLose catch sql exception

		// all ok, return
		return;
	} // Close private void update sensor table method

	/**
	 *  Creating private string to  retrieveSensorData
	 * Declaring a string to hold the sql select, setting its value to a select
	 * statement that will retrieve all values from the sensor controls table
	 *  where the sensorname equals the parameter supplied "where sensorname='" + sensorname + "'".
	 * http://localhost:8080/Household_remote_control_app_IOT_Server/RFIDDao?getdata&sensorname=4d004a5587
	 * http://localhost:8080/Household_remote_control_app_IOT_Server/RFIDDao?getdata&sensorname=1600ee15e9
	 * @param sensorname
	 * @return
	 */
	private String retrieveSensorData(String sensorname) {
		//SELECT from mudfoot server where sensorname = the sensorname inserted
		String selectSQL = "select * from rashidd.SensorControls where sensorname = \"" + sensorname + "\"";

		// String selectSQL = "select * from rashidd.SensorControls";
		ResultSet rs = null; // creating variable for result set
		// Creating array list of sensor data
		ArrayList<SensorData> s = new ArrayList<>();

		// Creating try to declare ArrayList of SensorData called allSensors to hold results,
		// and initialising it
		try {
			/**
			 *  iterate over the result set created by the select for each of
			 *  the columns in the table, putting them into oneSensor with
			 *  the corresponding set method for sensorname, sensorvalue, userid, timeinserted, using either
			 *  getString or getDouble and then execute query.
			 */
			rs = conn.createStatement().executeQuery(selectSQL);
			System.out.println("DEBUG: statement correct" +selectSQL);

			// Creating while resultset
			while (rs.next()) {
				// String sensorname, String sensorvalue, String userid, String sensordate
				SensorData oneSensor = new SensorData(rs.getString("sensorname"), rs.getString("sensorvalue"),
						rs.getString("userid"), rs.getString("timeinserted"));
				s.add(oneSensor); // add the data
			} // CLose while loop for rs.next
		} catch (SQLException ex) {
			System.out.println("Error in SQL " + ex.getMessage());
		} // Close catch sql exception e
		// Return the responce in json format
		return new Gson().toJson(s);
	} // Close private string retrieve sensor data

	// Creating public void publish to android to publish the sensor data to the application, in order to view the data on the appication
	//	public void publish_to_Android(SensorData a)throws Exception
	//	{
	//
	//		Gson g = new Gson();
	//		final MqttTopic motorTopic = client.getTopic(TOPIC_SERVER);
	//		motorTopic.publish(new MqttMessage(g.toJson(a).getBytes()));
	//
	//
	//	}
} // Close public class rfid dao