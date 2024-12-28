
public class SquareWave extends AbsrtactWave {

//	private Config config;
	private static Config singletonInstance ;
	
	SquareWave() {
		singletonInstance = new Config();		
	}
	
	public int getValue(int x) {
		int halfHeight = singletonInstance.getWaveHeight() / 2;
		return (x / 20) % 2 == 0 ? halfHeight : -halfHeight;
	}

//	public void draw(DrawingSurface s, int startY) {
//		throw new UnsupportedOperationException();
//	}
}
