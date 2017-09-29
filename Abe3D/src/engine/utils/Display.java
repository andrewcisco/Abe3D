package engine.utils;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import engine.core.Light;
import engine.core.Renderer;
import engine.core.Shader;
import engine.shader.GouraudTextureShader;

import static engine.core.Renderer.MatrixMode.*;

@SuppressWarnings("serial")
public class Display extends Canvas {

	public static final int DEFAULT_WINDOW_WIDTH = 800;
	public static final int DEFAULT_WINDOW_HEIGHT = 600;
	
	public static final int FINAL_SCREEN_WIDTH = 420;
	public static final int FINAL_SCREEN_HEIGHT = 315;
	
	public boolean running = false;
	public BufferStrategy bs;
	
	public int screenWidth;
	public int screenHeight;
	
	public int windowWidth;
	public int windowHeight;
	
	public Renderer renderer;
	public Thread thread;
	
	public Shader gouraudShader = new GouraudTextureShader();
	public Scene scene;
	
	public double sx, sy;
	
	private final Font fpsFont = new Font("Arial", Font.PLAIN, 10);
	public final Light light = new Light();
	
	public Display(int width, int height) {
		screenWidth = width;
		screenHeight = height;
		new Display(scene);
	}
	
	private Display(Scene scene) {
		setBackground(Color.BLACK);
		addKeyListener(new KeyHandler());
		scene.setDisplay(this);
		this.scene = scene;
	}
	
	public void init() throws Exception {
		MouseHandler mouseHandler = new MouseHandler();
		addMouseListener(mouseHandler);
		addMouseMotionListener(mouseHandler);
		
		createBufferStrategy(1);
		bs = getBufferStrategy();
		renderer = new Renderer(screenWidth, screenHeight);
		
		light.diffuse.set(1, 1, 1, 1);
		light.position.set(0, 20000, 0, 1);
		renderer.addLight(light);
		
		sx = screenWidth / (double)windowWidth;
		sy = screenHeight / (double)windowHeight;
		
		renderer.setShader(gouraudShader);
		
		renderer.setMatrixMode(PROJECTION);
		renderer.setPerspectiveProjection(Math.toRadians(60));
		
		renderer.setClipZNear(-1);
		renderer.setClipZFar(-15000);
		
		scene.init();
	
		running = true;
		thread = new Thread(new MainLoop());
		thread.start();
	}
	
	public void updatePhysics() {
		scene.updatePhysics();
	}
	
	public void update() {
		scene.update(renderer);
	}
	
	private void draw() {
		renderer.clearAllBuffers();
		scene.draw(renderer);
	}
	
	public void draw(Graphics2D g) {
		Graphics2D g2 = (Graphics2D) renderer.getColorBuffer().getColorBuffer().getGraphics();
		scene.draw(renderer, g2);
		g2.setFont(fpsFont);
		g2.setXORMode(Color.BLACK);
		g2.drawString("FPS: " + Timer.fps, 5, screenHeight - 20);
		g2.setPaintMode();
		g2.setColor(Color.WHITE);
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(renderer.getColorBuffer().getColorBuffer(), 0, 0, 400, 300, 0, 0, screenWidth, screenHeight, null);
		
		g.drawImage(renderer.getColorBuffer().getColorBuffer(), 0, 0, windowWidth, windowHeight, 0, 0, 400, 300, null);
	}
	
	
	private class MainLoop implements Runnable {
		
		@Override
		public void run() {
			while(running) {
				Timer.update();
				updatePhysics();
				update();
				draw();
				Graphics2D g = (Graphics2D)bs.getDrawGraphics();
				draw(g);
				g.dispose();
				bs.show();
				
				try {
					Thread.sleep(1);
					Thread.yield();
				} catch(InterruptedException e) {
					Logger.getLogger(Display.class.getName()).log(Level.SEVERE, null, e);
				}
			}
		}
	}
	
	private class MouseHandler extends MouseAdapter {
		
		@Override
		public void mouseMoved(MouseEvent e) {
			Mouse.x = (e.getX() - windowWidth * 0.5) * sx;
			Mouse.y = (windowHeight * 0.5 - e.getY()) * sy;
			Mouse.sx = e.getX() * sx;
			Mouse.sy = e.getY() * sy;
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			Mouse.x = (e.getX() - windowWidth * 0.5) * sx;
			Mouse.y = (windowHeight * 0.5 - e.getY()) * sy;
			Mouse.sx = e.getX() * sx;
			Mouse.sy = e.getY() * sy;
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			if(SwingUtilities.isLeftMouseButton(e)) {
				Mouse.pressed1 = true;
				Mouse.pressedConsumed1 = false;
			} else if(SwingUtilities.isRightMouseButton(e)) {
				Mouse.pressed2 = true;
				Mouse.pressedConsumed2 = false;
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if(SwingUtilities.isLeftMouseButton(e)) {
				Mouse.pressed1 = false;
				Mouse.pressedConsumed1 = false;
			} else if(SwingUtilities.isRightMouseButton(e)) {
				Mouse.pressed2 = false;
				Mouse.pressedConsumed2 = false;
			}
		}
	}
	
	private class KeyHandler extends KeyAdapter {
		
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() > Keyboard.keyDown.length - 1) {
				return;
			}
			
			Keyboard.keyDown[e.getKeyCode()] = true;
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode() > Keyboard.keyDown.length - 1) {
				return;
			}
			
			Keyboard.keyDown[e.getKeyCode()] = false;
			Keyboard.keyDownConsumed[e.getKeyCode()] = false;
		}
	}
}