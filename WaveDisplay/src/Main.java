
public class Main {

	public static void main(String[] args) {
		
		Config config = new Config();
		
		if (config.getWaveType().equals("sine")) {
			WaveDisplayFrame frame = new WaveDisplayFrame(new SineWave());			
		}
		else if (config.getWaveType().equals("sawtooth")) {
			WaveDisplayFrame frame = new WaveDisplayFrame(new SawtoothWave());			
		}
		else if (config.getWaveType().equals("sine")) {
			WaveDisplayFrame frame = new WaveDisplayFrame(new SquareWave());			
		}

	}

}
