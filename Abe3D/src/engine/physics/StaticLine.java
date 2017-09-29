package engine.physics;

import java.awt.Graphics2D;

public class StaticLine extends Shape {

	private final PhysicsVec2 point1 = new PhysicsVec2();
	private final PhysicsVec2 point2 = new PhysicsVec2();
	
	public StaticLine(double px1, double py1, double px2, double py2) {
		point1.set(px1, py1);
		point2.set(px2, py2);
	}
	
	public PhysicsVec2 getPoint1() {
		return point1;
	}
	
	public PhysicsVec2 getPoint2() {
		return point2;
	}
	
	@Override
	public void drawDebug(Graphics2D g) {
		g.setColor(debugColor);
		g.drawLine((int)point1.getX(), (int)point1.getY(), (int)(point2.getX()), (int)(point2.getY()));
	}
}