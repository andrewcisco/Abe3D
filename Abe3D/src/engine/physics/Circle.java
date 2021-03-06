package engine.physics;

import java.awt.Color;
import java.awt.Graphics2D;

public class Circle extends Shape {

	private final double radius;
	
	public Circle(double radius) {
		this.radius = radius;
	}
	
	public double getRadius() {
		return radius;
	}
	
	@Override
	public void drawDebug(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.drawOval((int) -radius, (int) -radius, (int)(2 * radius), (int)(2 * radius));
		g.drawLine(0, 0, (int) radius, 0);
	}
}