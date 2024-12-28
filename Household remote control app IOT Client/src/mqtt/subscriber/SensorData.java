package mqtt.subscriber;

/**
 * Constructors initialize objects which are being created with the new operator and methods would perform operations on objects which already exist. 
 * The constructors cannot be called directly; they would be called indirectly.
 * @author dhanyaal
 */

// Creating public class for sensor data and initializing the different strings
public class SensorData {
	// Creating strings for the user id, sensor name, sensor value and sensor date
	String sensorname;
	String sensorvalue;
	String userid;
	String sensordate;

	// Creating method for SensorData and setting string paramters for the sensorname, sensorvalue, userid and sensor date.
	public SensorData(String sensorname, String sensorvalue, String userid, String sensordate) {
		super();
		this.sensorname = sensorname;
		this.sensorvalue = sensorvalue;
		this.userid = userid;
		this.sensordate = sensordate;
	} // Close RFIDSensorData method

	// Constructors depending on which parameters are known e.g. sensorname, sensorvalue and userid
	public SensorData(String sensorname, String sensorvalue, String userid) {
		super();
		this.sensorname = sensorname;
		this.sensorvalue = sensorvalue;
		this.userid = userid;
		// Defaults for when no sensordate is known
		this.sensordate = "unknown";
	} // Close SensorData Constructors

	// Creating constructor for the SensorData for the sensorname and sensorvalue
	public SensorData(String sensorname, String sensorvalue) {
		super();
		this.sensorname = sensorname;
		this.sensorvalue = sensorvalue;
		// Defaults for when no userid or location known
		this.userid = "unknown";
		this.sensordate = "unknown";
	} // Close constructor for RFIDSensorData

	// Creating setter and getter for getSensorname and setSensorname
	public String getSensorname() {
		return sensorname;
	} // Close get sensorname

	public void setSensorname(String sensorname) {
		this.sensorname = sensorname;
	} // Close setter sensor name

	// Creating setter and getter for sensorvalue and setSensorvalue
	public String getSensorvalue() {
		return sensorvalue;
	} // Close getter sensor value

	public void setSensorvalue(String sensorvalue) {
		this.sensorvalue = sensorvalue;
	} // Close setter sensor value 

	// Creating setter and getter for userid
	public String getUserid() {
		return userid;
	} // Close public get user id

	public void setUserid(String userid) {
		this.userid = userid;
	} // CLose public set user id

	// Creating setter and getter for usensor date
	public String getSensordate() {
		return sensordate;
	} // Close public string set sensor date

	public void setSensordate(String sensorvalue) {
		this.sensordate = sensorvalue;
	} // Close public set sensor date

	/**
	 * The toString method is used in java when we want a object to represent string. 
	 * Overriding toString() method would return the specified values. 
	 * This method can be overridden to customize the String representation of the Object.
	 */
	@Override
	public String toString() {
		return "SensorData [sensorname=" + sensorname + ", sensorvalue=" + sensorvalue + ", userid=" + userid
				+ ", sensordate=" + sensordate + "]";
	}

}

