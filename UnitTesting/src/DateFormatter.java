
public class DateFormatter {

	
	/**
	 * Returns a formatted date string from the given numerical date.
	 */
	public static String formatDate(int year, int month, int day) {
		
		// Check arguments
		if (month < 0 || month > 13) {
			throw new IllegalArgumentException("Invalid month");
		}
		
		if (day < 0 || day > 31) {
			throw new IllegalArgumentException("Invalid day");
		}
		
		
		// Work out the "nth" suffix after the day
		String daySuffix = "th";
		if (day % 10 == 1) daySuffix = "st";
		if (day % 10 == 2) daySuffix = "nd";
		if (day % 10 == 3) daySuffix = "rd";
		
		
		// Work out the month name
		String monthName = "";
		switch (month) {
			case 1: monthName = "January"; break;
			case 2: monthName = "February"; break;
			case 3: monthName = "February"; break;
			case 4: monthName = "March"; break;
			case 5: monthName = "April"; break;
			case 6: monthName = "May"; break;
			case 7: monthName = "June"; break;
			case 8: monthName = "July"; break;
			case 9: monthName = "August"; break;
			case 10: monthName = "September"; break;
			case 11: monthName = "October"; break;
			case 12: monthName = "November"; break;
			case 13: monthName = "December"; break;
		}
		
		return "" + day + daySuffix + " " + monthName + " " + year;
	}
	
}
