/**
 * The SineWave class implements this interface, including the draw method. 
 * Sinewave would extend the absractWave class.
 * The abstract wave would also need to implement the wave.
 * @author dh11a
 *
 */
public class SineWave extends AbsrtactWave {

	private Config singletonInstance;
	
	SineWave() {
		singletonInstance = new Config();		
	}
	
	public int getValue(int x) {
		return (int) (singletonInstance.getWaveHeight() * Math.sin(0.1 * x));
	}
	
//	public void draw(DrawingSurface s, int startY) {
//		int lastY = startY;
//		
//		for (int x = 1; x < singletonInstance.getWindowSize(); x++) {
//			int y = getValue(x) + startY;
//			s.line(x - 1, x, lastY, y, 0x0000FF);
//			lastY = y;
//		}
//	}
	
}
