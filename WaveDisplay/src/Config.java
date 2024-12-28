
public class Config {
	private static Config singletonInstance ;
	

	public Config Config(Wave wave) {
		return singletonInstance;
	}
	public String getWaveType() {
		return "sine";
	}
	
	public int getWindowSize() {
		return 500;
	}
	
	public int getWaveHeight() {
		return 50;
	}
	
}
