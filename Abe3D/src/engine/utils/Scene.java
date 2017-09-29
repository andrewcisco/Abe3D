package engine.utils;

import java.awt.Graphics2D;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import engine.core.Renderer;

public abstract class Scene {

	public Display display;
	public Entity<?> camera;
	public List<Entity<?>> entities = new ArrayList<Entity<?>>();
	
	public Scene() {}
	
	public Display getDisplay() {
		return display;
	}
	
	public void setDisplay(Display engine) {
		this.display = engine;
	}
	
	public Entity<?> getCamera() {
		return camera;
	}
	
	public void setCamera(Entity<?> camera) {
		this.camera = camera;
	}
	
	public void init() throws Exception {
		camera.init();
		for(Entity<?> entity : entities) {
			entity.init();
		}
	}
	
	public void updatePhysics() {}
	
	public void update(Renderer renderer) {
		camera.update(renderer);
		renderer.setViewTransform(camera.transform);
		
		for(Entity<?> entity : entities) {
			entity.update(renderer);
		}
	}
	
	public void draw(Renderer renderer) {
		for(Entity<?> entity : entities) {
			entity.preDraw(renderer);
			entity.draw(renderer);
		}
	}
	
	public void draw(Renderer renderer, Graphics2D g) {
		for(Entity<?> entity : entities) {
			entity.draw(renderer, g);
		}
	}
	
	public void broadcastMessage(String message) {
		for(Entity<?> entity : entities) {
			try {
				Method method = entity.getClass().getMethod(message);
				if(method != null) {
					method.invoke(entity);
				}
			} catch(Exception e) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
			}
		}
		
		try {
			Method method = camera.getClass().getMethod(message);
			if(method != null) {
				method.invoke(camera);
			}
		} catch(Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
		}
	}
}