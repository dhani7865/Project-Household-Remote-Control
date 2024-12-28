import java.awt.Color;

public class GraphicsDrawingSurface<graphic> implements DrawingSurface {
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	// private instance variable to hold a Graphics object, which is passed through the constructor 
	private graphic Graphics; // instance variablefor graphic object
	private Config config;


	private  Color getColor() {
		return null;
	}


	public void setColor(Color c) {
	}
	
	// Creating protected void graphics construcotr, which is passed through the constructor
	protected void Graphics(graphic Graphic) {
		this.config = new Config();
		this.Graphics = Graphics;
	}

	/**
	 * Creating draw line method.
	 * x1 - the first point's x coordinate.
	 * y1 - the first point's y coordinate.
	 * x2 - the second point's x coordinate.
	 * y2 - the second point's y coordinate.
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void drawLine(int x1, int y1,int x2,int y2) {
	}


	@Override
	public void line(int startX, int endX, int startY, int endY, int color) {
		// TODO Auto-generated method stub
		
	}


	//	public GraphicsDrawingSurface(Wave wave) {
	//		this.config = new Config();
	//		this.graphics = graphics;
	//		//setupWindow();
	//	}

}
