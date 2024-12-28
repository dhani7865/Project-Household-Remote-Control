import org.junit.Assert;
import org.junit.Test;

public class DateFormatterTests {

	@Test
	public void testBasicDate1() {
		String result = DateFormatter.formatDate(2019, 1, 1);
		Assert.assertEquals("1st January 2019", result);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidMonth1() {
		String result = DateFormatter.formatDate(2019, -100, 1);
	}
	
}
