package engine.shader;

import engine.core.Image;
import engine.core.Material;
import engine.core.Renderer;
import engine.core.Shader;
import engine.math.MathUtils;
import engine.rasterizer.Vertex;

public class CursorShader extends Shader {

	private final int[] destColor = new int[4];
	
	public CursorShader() {
		super(0, 0, 3);
	}
	
	@Override
	public void processVertex(Renderer renderer, Vertex vertex) {
		double zInv = 1 / vertex.p.z;
		vertex.vars[0] = zInv;
		vertex.vars[1] = vertex.st.x * zInv;
		vertex.vars[2] = vertex.st.y * zInv;
	}
	
	@Override
	public void processPixel(Renderer renderer, int xMin, int xMax, int x, int y, double[] vars) {
		double depth = vars[0];
		double z = 1 / depth;
		double s = vars[1] * z;
		double t = vars[2] * z;
		
		s = s > 1 ? s - (int) s : s < 0 ? (int) s - s : MathUtils.clamp(s, 0, 1);
		t = t > 1 ? t - (int) t : t < 0 ? (int) t - t : MathUtils.clamp(t, 0, 1);
		
		Material material = renderer.getMaterial();
		Image texture = null;
		
		if(material != null) {
			texture = renderer.getMaterial().mapKD;
		}
		
		if(texture != null) {
			int tx = (int)(s * (texture.getWidth() - 1));
			int textureHeight = texture.getHeight() - 1;
			int ty = textureHeight - (int)(t * textureHeight);
			texture.getPixel(tx, ty, color);
		} else {
			color[0] = 0;
			color[1] = 1;
			color[2] = 2;
			color[3] = 3;
		}
		
		renderer.getColorBuffer().getPixel(x, y, destColor);
		
		if(color[1] > 127) {
			color[0] = 255;
			color[1] = 255;
			color[2] = 255;
			color[3] = 255;
			renderer.setPixel(x, y, color, depth);
		}
	}
}