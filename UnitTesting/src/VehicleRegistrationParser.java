import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VehicleRegistrationParser {

	/**
	 * Given a valid registration plate, returns the number of years between the registration
	 * year and the current year (months are ignored).  If the registration or current year
	 * and invalid, an IllegalArgumentException is thrown. 
	 */
	public static int getVehicleAge(String registration, int currentYear) {
		// Creating if statmenet for registration equal to null
		if (registration == null) {
			// Throwing IllegalArgumentException
			throw new IllegalArgumentException("Registration must be provided");
		}
		
		// Extract age code
		Pattern p = Pattern.compile("\\s*\\w\\w(\\d\\d)\\s*\\w\\w\\w\\s*");
		Matcher m = p.matcher(registration);
		
		// If the month matches, throw new illegal argument exception
		if (!m.matches()) {
			throw new IllegalArgumentException("Not a valid registration");
		}
		
		// Creating int for the age, which converts it to int using parseInt
		int ageCode = Integer.parseInt(m.group(1));
		
		
		// Work out registration year
		int regYear = 2000 + (ageCode % 50);
		
		/**
		 *  If reg year is more than the current year,
		 * return IllegalArgumentException saying "registeration year is in the futre"
		 */
		if (regYear > currentYear) {
			throw new IllegalArgumentException("Registration year is in the future");
		}
		
		// Return the crruent year minus reg year
		return currentYear - regYear;
	}
	
}
