package engine.core;

import engine.math.Vec4;

public class Material {

	public String name;
	public double ns;
	public Vec4 ka = new Vec4();
	public Vec4 kd = new Vec4();
	public Vec4 ks = new Vec4();
	public double ni;
	public double d;
	public double illum;
	public Image mapKD;
	public Image mapKA;
	
	public Material(String name) {
		this.name = name;
	}
	
}