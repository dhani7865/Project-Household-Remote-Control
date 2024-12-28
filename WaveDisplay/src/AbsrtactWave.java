// Creating astract class for wave which implements wave
public abstract class AbsrtactWave implements Wave {
	// Creating private variable for config for the singleton instance
	private Config singletonInstance;
	// Absract wave method, which returns singleton instance
	AbsrtactWave() {
		singletonInstance = new Config();		
	}
	// creating public int get value method, which returns singleton instance and get wave height
	public int getValue(int x) {
		return x % singletonInstance.getWaveHeight();
	}
	
	// Creating public draw method, to draw the wave
	public void draw(DrawingSurface s, int startY) {
		int lastY = startY;
		// drawing the window andcalling the singleton instance
		for (int x = 1; x < singletonInstance.getWindowSize(); x++) {
			int y = getValue(x) + startY;
			s.line(x - 1, x, lastY, y, 0x0000FF);
			lastY = y;
		}
	}

}
