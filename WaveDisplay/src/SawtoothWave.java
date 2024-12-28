
public class SawtoothWave  extends AbsrtactWave {

	private Config singletonInstance;
	
	SawtoothWave() {
		singletonInstance = new Config();		
	}
	
	public int getValue(int x) {
		return x % singletonInstance.getWaveHeight();
	}

//	public void draw(DrawingSurface s, int startY) {
//		throw new UnsupportedOperationException();
//	}
}