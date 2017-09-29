package engine.core;

import java.util.ArrayList;
import java.util.List;

import engine.rasterizer.Vertex;

public class Shader {

	public Vertex[] vertices;
	public double[] uniforms;
	
	// Edge 1
	public double[] dVarsE1;
	public double[] varsE1;
	
	// Edge 2
	public double[] dVarsE2;
	public double[] varsE2;
	
	// Edges
	public double[] dVarsScan;
	public double[] varsScan;
	
	public int[] color = new int[] { 255, 255, 255, 255 };
	
	private int vertexExtraDataSize;
	private int vertexVarSize;
	
	public Shader(int uniformSize, int vertexExtraDataSize, int vertexVarSize) {
		this.vertexExtraDataSize = vertexExtraDataSize;
		this.vertexVarSize = vertexVarSize;
		
		vertices = new Vertex[3];
		for(int i = 0; i < 3; i++) {
			vertices[i] = new Vertex(vertexExtraDataSize, vertexVarSize);
		}
		
		uniforms = new double[uniformSize];
		dVarsE1 = new double[vertexVarSize];
		varsE1 = new double[vertexVarSize];
		dVarsE2 = new double[vertexVarSize];
		varsE2 = new double[vertexVarSize];
		dVarsScan = new double[vertexVarSize];
		varsScan = new double[vertexVarSize];
	}
	
	public void preDraw(Renderer renderer, Vertex v1, Vertex v2, Vertex v3) {
		// do nothing
	}
	
	public void postDraw(Renderer renderer, Vertex v1, Vertex v2, Vertex v3) {
		// do nothing
	}
	
	public void processVertex(Renderer renderer, Vertex v) {
		// do nothing
	}
	
	public void processPixel(Renderer renderer, int xMin, int xMax, int x, int y, double[] vars) {
		// do nothing
	}
	
	// Cached vertices
	private int vertexCacheIndex = 0;
	private List<Vertex> vertexCache = new ArrayList<Vertex>();
	
	public void initVertexCache() {
		vertexCacheIndex = 0;
	}
	
	public Vertex getVertexFromCache() {
		Vertex cachedVertex = null;
		if(vertexCacheIndex > (vertexCache.size() - 1)) {
			cachedVertex = new Vertex(vertexExtraDataSize, vertexVarSize);
			vertexCache.add(cachedVertex);
			vertexCacheIndex++;
		} else  {
			cachedVertex = vertexCache.get(vertexCacheIndex++);
		} 
		
		return cachedVertex;
	}
}