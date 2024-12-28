

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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class LoginServer
 */
@WebServlet("/LoginServer")
public class LoginServer extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Declaring GSON utility object
	// Connection = null and statement.
	Gson gson = new Gson();
	Connection conn = null;
	static Statement stmt;
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



	/**
	 * @see HttpServlet#HttpServlet()
	 * Creating login server method and calling the super method
	 */
	public LoginServer() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Check to see whether the client is requesting data or sending it
		String getdata = request.getParameter("getdata");

		// get parameter from request (email and password)
		// if no getdata parameter, client is sending data
		if (getdata == null) {
			// getdata is null, therefore it is receiving data
			// Extracting the parameter data holding the sensordata
			String sensorJsonString = request.getParameter("sensordata");
			// Extracting the parameter data holding the email and password
			String data = request.getParameter("email");
			String data1 = request.getParameter("password");

			// creating boolean value and setting it to false at the start
			boolean valid = false;

			// if statement for valid equal to true
			if (valid == true) {
				// Print valid statement out onto the console
				System.out.println("valid login details");

				// send command to login
				PrintWriter writer = response.getWriter();
				writer.println("true"); // Writer = true
				writer.close();
			} // Close if statement for valid ==true
			// Otherwise, return invalid login details
			else {
				// print message out onto the console
				System.out.println("invalid login details");
			} // CLose else
		} // CLose if statement for getdata == null
	} // Close protected void do get method

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}


	/**
	 *  Creating boolean to check if a valid user is trying to login, using specific credentials
	 *  Selecting everything from the login table where the email is 'dh11any@hotmail.com'
	 * @param email
	 * @return
	 */
	public static boolean isUserValid(String email) {
		// print message out onto the console
		System.out.println("valid"+email);

		try {
			// select everything from the SensorControlsValid  table where the email is valid email)
			String sql = "SELECT * FROM login WHERE email='"+email+"';";

			// execute sql query
			System.out.println("DEBUG: statement correct" +sql);
			System.out.println(sql); // print the query result
			System.out.println(">> Debug: All RFID data displayed");
			// execute query
			ResultSet rs = stmt.executeQuery(sql);

			// Creating while loop for result set next and creating if statement to get the string for the result set
			while (rs.next()) {
				if (rs.getString("email").equals(email)) return true;
			} // CLose while loop
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} // CLose catch
		// Otherwise return false if invalid user is trying to login
		return false;
	} // CLose buplic booelan is user valid method
	
	/**
	 *  Creating private string to  retrieveSensorData
	 * Declaring a string to hold the sql select, set its value to a select
	 * statement that will retrieve all values from the log in table
	 * @param email
	 * @return
	 */
	private String retrieveSensorData(String email) {
		//SELECT from mudfoot server where email = the email inserted
		String selectSQL = "select * from rashidd.login where email = \"" + email + "\"";

		ResultSet rs = null; // creating variable for result set
		// Creating array list of sensor data
		ArrayList<SensorData> s = new ArrayList<>();


		try {
			/**
			 *  iterate over the result set created by the select for each of
			 *  the columns in the table, putting them into oneSensor with
			 *  the corresponding set method for email and password using either
			 *  getString or getDouble and then execute query.
			 */
			rs = conn.createStatement().executeQuery(selectSQL);
			System.out.println("DEBUG: statement correct" +selectSQL);

			// Creating while resultset
			while (rs.next()) {
				// String email and password
				SensorData oneSensor = new SensorData(rs.getString("email"), rs.getString("password"));
				s.add(oneSensor); // add the data
			} // CLose while loop for rs.next
		} catch (SQLException ex) {
			System.out.println("Error in SQL " + ex.getMessage());
		} // Close catch sql exception e
		// Return the responce in json format
		return new Gson().toJson(s);
	} // Close private string retrieve sensor data
} // CLose public class login server