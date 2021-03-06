package engine.wavefront;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class ObjReader {

	public List<Point2D> points = new ArrayList<Point2D>();
	public List<Point> lines = new ArrayList<Point>();
	
	public Point camera = new Point();
	public Point cameraPressed = new Point();
	public Point mousePressed = new Point();
	
	public Point mouse = new Point();
	public Rectangle rectangle = new Rectangle();
	
	public boolean useClockwise = true;
	
	private BufferedImage image;
	
	public ObjReader() {
		this(null);
	}
	
	public ObjReader(String filePath) {
		try {
			image = ImageIO.read(getClass().getResourceAsStream(filePath));
		} catch(IOException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
			System.exit(-1);
		}
	}
	
	public void read(String filePath) throws Exception {
		double scale = 70;
		points.clear();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(".obj")));
		String line = null;
		
		while((line = reader.readLine()) != null) {
			if(line.startsWith("v ")) {
				String[] data = line.split("\\ ");
				double x = Double.parseDouble(data[1]) * scale;
				double z = Double.parseDouble(data[3]) * scale;
				points.add(new Point2D.Double(x, z));
			} else if(line.startsWith("f ")) {
				String[] data = line.split("\\ ");
				int i1 = Integer.parseInt(data[1]);
				int i2 = Integer.parseInt(data[2]);
				lines.add(new Point(i1 - 1, i2 - 1));
			}
		}
		
		reader.close();
	}
	
	public void drawStatus(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.drawString("Using Clockwise: " + useClockwise, 10, 10);
		g.drawString("Mouse Pos: " + mouse.x + ", " + mouse.y, 10, 20);
		
		g.translate(-camera.x, -camera.y);
		
		
		AffineTransform transform = g.getTransform();
		g.scale(1.715, 1.715);
		g.drawImage(image, 0, 0, null);
		g.setTransform(transform);
		
		g.fillOval((int)(mouse.getX() - 3), (int)(mouse.getY() - 3), 6, 6);
		
		for(Point line : lines) {
			Point2D p1 = points.get(line.x);
			Point2D p2 = points.get(line.y);
			
			g.setColor(Color.BLACK);
			g.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
			
			g.setColor(Color.RED);
			g.fillOval((int)(p1.getX() - 2), (int)(p1.getY() - 2), 4, 4);
			g.fillOval((int)(p2.getX() - 2), (int)(p2.getY() - 2), 4, 4);
			
			g.setColor(Color.BLUE);
			g.drawString("" + line.x, (int)(p1.getX() + 5), (int)(p1.getY()));
			g.drawString("" + line.y, (int)(p2.getY() + 5), (int)(p1.getY()));
		}
		
		Point2D p = getSelectedPoint();
		if(p != null) {
			g.setColor(Color.GREEN);
			g.fillOval((int)(p.getX() - 3), (int)(p.getY() - 3), 6, 6);
			g.setColor(Color.RED);
			String px = String.format("%.2f", p.getX());
			String py = String.format("%.2f", p.getY());
			g.drawString("x = " + px + " y = " + py, (int)(p.getX()  + 5), (int)(p.getY() + 10));
		}
		
		g.translate(camera.x, camera.y);
		g.setColor(Color.BLACK);
		g.drawString("Using Clockwise" + useClockwise, 10, 10);
		g.drawString("Mouse Pos: " + mouse.x + ", " + mouse.y, 10, 20);
	}
	
	public Point2D getSelectedPoint() {
		for(Point2D p : points) {
			rectangle.setBounds((int)(p.getX() - 5), (int)(p.getY() - 5), 10, 10);
			if(rectangle.contains(mouse)) {
				return p;
			}
		}
		
		return null;
	}
	
	private List<Point2D> wallPoints = new ArrayList<Point2D>();
	
	public void generateCode(Point2D p) {
		if(p == null) {
			return;
		}
		
		wallPoints.clear();
		int pIndex = points.indexOf(p);
		
		if(pIndex >= 0) {
			addWallPoint(points.get(pIndex));
		}
		
		List<Point> linesTmp = new ArrayList<Point>(lines);
		Point found = null;
		
		do {
			found = null;
			for(Point line : linesTmp) {
				if(line.x == pIndex) {
					addWallPoint(points.get(line.y));
					pIndex = line.y;
					found = line;
					lines.remove(line);
					break;
				} else if(line.y == pIndex) {
					addWallPoint(points.get(line.x));
					pIndex = line.x;found = line;
					lines.remove(line);
					break;
				}
			}
			
			linesTmp.remove(found);
		} while(found != null);
		
		generateWallPointsCode();
	}
	
	private void addWallPoint(Point2D p) {
		wallPoints.add(p);
	}
	
	private void generateWallPointsCode() {
		boolean clockwise = isClockwise(wallPoints);
		if((clockwise && !useClockwise) || (!clockwise && useClockwise)) {
			Collections.reverse(wallPoints);
		}
		
		for(int p = 0; p < wallPoints.size(); p++) {
			Point2D p1 = wallPoints.get(p);
			
			System.out.print("" + (int)(p1.getX()) + ", " + (int)(p1.getY()) + ", ");
		}
		
		System.out.println("// ---");
	}
	
	public boolean isClockwise(List<Point2D> points) {
		double sum = 0;
		for(int i = 0; i < points.size(); i++) {
			Point2D p1 = points.get(i);
			Point2D p2 = points.get((i + 1) % points.size());
			sum += (p2.getX() - p1.getX()) * (p2.getX() + p1.getY());
		}
		
		return sum >= 0;
	}
	
	public void printSelectedPoint() {
		Point2D p = getSelectedPoint();
		if(p != null) {
			System.out.println("" + (int)(p.getX()) + ", " + (int)(p.getY()));
		} else {
			System.out.println("" + (int)(mouse.getX()) + ", " + (int)(mouse.getY()));
		}
	}
}