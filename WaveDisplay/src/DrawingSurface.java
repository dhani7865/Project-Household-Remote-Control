
public interface DrawingSurface {
	/**
	 *  The DrawingSurface interface defines a single method to draw a line onto the screen. 
	 * @param startX
	 * @param endX
	 * @param startY
	 * @param endY
	 * @param color
	 */
	void line(int startX, int endX, int startY, int endY, int color);
}
