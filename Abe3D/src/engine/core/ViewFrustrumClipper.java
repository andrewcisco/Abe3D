package engine.core;

import java.util.ArrayList;
import java.util.List;

import engine.rasterizer.Vertex;

public class ViewFrustrumClipper {

	private List<Vertex> clippedVertices1 = new ArrayList<Vertex>();
	private List<Vertex> clippedVertices2 = new ArrayList<Vertex>();
	
	public ViewFrustrumClipper() {}
	
	public List<Vertex> clip(Shader shader, Vertex[] vertices, double choppedWidth, double choppedHeight, double near, double far) {
		shader.initVertexCache();
		
		clippedVertices1.clear();
		for(Vertex vertex : vertices) {
			clippedVertices1.add(vertex);
		}
		
		clipAgainstPlane(shader, clippedVertices1, clippedVertices2, 1, choppedWidth);
		clipAgainstPlane(shader, clippedVertices2, clippedVertices1, 1, -choppedWidth);
		clipAgainstPlane(shader, clippedVertices1, clippedVertices2, 2, choppedHeight);
		clipAgainstPlane(shader, clippedVertices2, clippedVertices1, 2, -choppedHeight);
		clipAgainstPlane(shader, clippedVertices1, clippedVertices2, 3, near);
		clipAgainstPlane(shader, clippedVertices2, clippedVertices1, 4, far);
		
		return clippedVertices1;
	}
	
	private boolean isInside(int plane, double v, double w, double planeLimitValue) {
		switch(plane) {
		case 3: return v <= planeLimitValue;
		case 4: return v >= planeLimitValue;
			default: return (planeLimitValue < 0) ? (v >= planeLimitValue * w) : (v <= planeLimitValue * w);
		}
	}
	
	private void clipAgainstPlane(Shader shader, List<Vertex> original, List<Vertex> clipped, int plane, double planeLimitValue) {
		clipped.clear();
		for(int i = 0; i < original.size(); i++) {
			Vertex a = original.get(i);
			Vertex b = original.get((i + 1) % original.size());
			
			double apx = a.getValueByPlane(plane);
			double bpx = b.getValueByPlane(plane);
			
			boolean aIsInside = isInside(plane, apx, a.p.w, planeLimitValue);
			boolean bIsInside = isInside(plane, bpx, b.p.w, planeLimitValue);
			
			if(aIsInside) {
				clipped.add(a);
			}
			
			if(aIsInside ^ bIsInside) {
				double p = (plane >= 3) ? (planeLimitValue - a.p.z) / (b.p.z - a.p.z) : (-a.p.w * planeLimitValue + apx) / ((b.p.w - a.p.w) * planeLimitValue - (bpx - apx));
				Vertex nb = shader.getVertexFromCache();
				nb.setLerp(a, b, p);
				clipped.add(nb);
			}
		}
	}
}