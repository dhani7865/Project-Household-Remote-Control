import org.junit.Assert;
import org.junit.Test;

public class VehicleRegistrationParserTests {

	@Test
	public void testBasicReg() {
		int result = VehicleRegistrationParser.getVehicleAge("AB09 XYZ", 2019);
		Assert.assertEquals(10, result);
	}

}
