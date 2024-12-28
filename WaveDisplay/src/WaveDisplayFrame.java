import java.awt.Graphics;

import javax.swing.JFrame;

/**
 * The WaveDisplayFrame class represents a window which displays a wave.
 * @author dh11a
 *
 */
public class WaveDisplayFrame extends JFrame {
	
	private Wave wave;
	private Config singletonInstance;
	
	public WaveDisplayFrame(Wave wave) {
		this.singletonInstance = new Config();
        this.wave = wave;
        setupWindow();
	}
	
	public void paint(Graphics g) {
		drawWave(g, wave, null, singletonInstance.getWindowSize() / 2);
	}
	

	private void drawWave(Graphics g, Wave wave, DrawingSurface startX, int startY) {
		
		// TODO: complete this method.  We want to call wave.draw,
		// but this requires a DrawingSurface object.  We only have
		// a Graphics object, so we need an adapter class to convert
		// our Graphics object into a DrawingSurface object.
		
		
		wave.draw(startX, startY);
	}
	
	
	
	private void setupWindow() {
		setSize(singletonInstance.getWindowSize(), singletonInstance.getWindowSize());
		setTitle("Wave display");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
	}
	
}
