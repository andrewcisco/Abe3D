package engine.physics;

import java.awt.Color;
import java.awt.Graphics2D;

public class Shape {

	protected Color debugColor = Color.BLACK;
	
	public Color getDebugColor() {
		return debugColor;
	}
	
	public void setDebugColor(Color debugColor) {
		this.debugColor = debugColor;
	}
	
	public void drawDebug(Graphics2D g) {}
}