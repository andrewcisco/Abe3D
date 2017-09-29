package engine.utils;

import java.awt.Graphics2D;
import java.util.List;

import engine.core.Renderer;
import engine.core.Transform;
import engine.math.Vec2;
import engine.math.Vec4;
import engine.shader.GouraudTextureShader;
import engine.wavefront.Obj;
import engine.wavefront.WavefrontParser;

public abstract class Entity<T extends Scene> {

	public String name;
	public T scene;
	public boolean visible = false;
	public Transform transform = new Transform();
	public List<Obj> mesh;
	
	public Entity(String name, T scene) {
		this.name = name;
		this.scene = scene;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public void init() throws Exception {
		transform.setIdentity();
	}
	
	public void update(Renderer renderer) {}

	public void preDraw(Renderer renderer) {
		renderer.setBackfaceCullingEnabled(true);
		renderer.setShader(scene.display.gouraudShader);
		renderer.setMatrixMode(Renderer.MatrixMode.MODEL);
		renderer.setModelTransform(transform);
		GouraudTextureShader.minIntensity = 0.5;
		GouraudTextureShader.maxIntensity = 1.0;
		GouraudTextureShader.scale = 1.0;
	}
	
	public void draw(Renderer renderer) {
		if(!visible || mesh == null) {
			return;
		}
		
		for(Obj obj : mesh) {
			renderer.setMaterial(obj.material);
			renderer.begin();
			for(WavefrontParser.Face face : obj.faces) {
				for(int f = 0; f < 3; f++) {
					Vec4 v = face.vertex[f];
					Vec4 n = face.normal[f];
					Vec2 t = face.texture[f];
					renderer.setTextureCoordinates(t.x, t.y);
					renderer.setNormal(n.x, n.y, n.z);
					renderer.setVertex(v.x, v.y, v.z);
				}
			}
			
			renderer.end();
		}
	}
	
	public void draw(Renderer renderer, Graphics2D g) {}
}