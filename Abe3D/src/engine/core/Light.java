package engine.core;

import engine.math.Vec4;

public class Light {

	public Vec4 position = new Vec4();
	public Vec4 ambient = new Vec4();
	public Vec4 diffuse = new Vec4();
	public Vec4 specular = new Vec4();
	public boolean enabled = true;
	
	public Light() {}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}