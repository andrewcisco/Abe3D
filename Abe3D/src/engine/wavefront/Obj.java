package engine.wavefront;

import java.util.ArrayList;
import java.util.List;

import engine.core.Material;
import engine.wavefront.WavefrontParser.Face;

public class Obj {

	public List<Face> faces = new ArrayList<Face>();
	public Material material;
	
}