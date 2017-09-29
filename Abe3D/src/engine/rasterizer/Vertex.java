package engine.rasterizer;

import engine.math.Vec2;
import engine.math.Vec4;

public class Vertex implements Comparable<Vertex> {

	public Vec4 p = new Vec4();
	public Vec4 normal = new Vec4();
	public Vec2 st = new Vec2();
	
	public double[] extraDatas;
	public double[] vars;
	
	public Vertex(Vertex v) {
		this(v.extraDatas.length, v.vars.length);
	}
	
	public Vertex(int extraDataSize, int varSize) {
		extraDatas = new double[extraDataSize];
		vars = new double[varSize];
	}
	
	public double getValueByPlane(int plane) {
		switch(plane) {
			case 1: return p.x;
			case 2: return p.y;
			case 3:
			case 4: return p.z;
			default: return 0;
		}
	}
	
	public void setLerp(Vertex a, Vertex b, double porc) {
		p.setLerp(a.p, b.p, porc);
		normal.setLerp(a.normal, b.normal, porc);
		st.setLerp(a.st, b.st, porc);
		
		for(int i = 0; i < extraDatas.length; i++) {
			extraDatas[i] = a.extraDatas[i] + porc * (b.extraDatas[i] - a.extraDatas[i]);
		}
	}
	
	public void multiply(double s) {
		p.multiply(s);
		normal.multiply(s);
		st.multiply(s);
	}
	
	@Override
	public int compareTo(Vertex t) {
		return (int) (100000 * (p.y - t.p.y));
	}
}