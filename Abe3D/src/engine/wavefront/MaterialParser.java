package engine.wavefront;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import engine.core.Image;
import engine.core.Material;

public class MaterialParser {

	private static String resourcePath = "/res/";
//	private static String defaultResourcePath = "/res/";
	public static Map<String, Material> materials = new HashMap<String, Material>();
	
	public static void load(String resource) throws Exception {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(MaterialParser.class.getResourceAsStream(resourcePath + resource)));
			String line = null;
			
			while((line = reader.readLine()) != null) {
				if(line.startsWith("newmtl")) {
					extractMaterial(reader, line);
				}
			}
			reader.close();
		} catch(Exception e) {
			System.out.println("Error Path: " + resourcePath + resource);
		}
	}
	
	private static void extractMaterial(BufferedReader reader, String line) throws Exception {
		String materialName = line.substring(7);
		Material material = new Material(materialName);
		while((line = reader.readLine()) != null) {
			line = line.trim();
			if(line.trim().isEmpty()) {
				break; 
			} else if(line.startsWith("Ns")) {
				material.ns = Double.parseDouble(line.substring(3));
			} else if(line.startsWith("Ka ")) {
				String[] values = line.substring(3).split("\\ ");
				material.ka.set(Double.parseDouble(values[0]), Double.parseDouble(values[1]), Double.parseDouble(values[2]), 0);
			} else if(line.startsWith("Kd ")) {
				String[] values = line.substring(3).split("\\ ");
				material.kd.set(Double.parseDouble(values[0]), Double.parseDouble(values[1]), Double.parseDouble(values[2]), 0);
			} else if(line.startsWith("Ks ")) {
				String[] values = line.substring(3).split("\\ ");
				material.ks.set(Double.parseDouble(values[0]), Double.parseDouble(values[1]), Double.parseDouble(values[2]), 0);
			} else if(line.startsWith("Ni ")) {
				material.ni = Double.parseDouble(line.substring(3));
			} else if(line.startsWith("d ")) {
				material.illum = Double.parseDouble(line.substring(2));
			} else if(line.startsWith("mapKD ")) {
				material.mapKD = new Image(extractJustFilename(line));
			} else if(line.startsWith("mapKA ")) {
				material.mapKA = new Image(extractJustFilename(line));
			}
		}
		
		materials.put(materialName, material);
	}
	
	private static String extractJustFilename(String line) {
		int i = line.lastIndexOf("\\");
		
		if(i < 0) {
			i = line.lastIndexOf("/");
		}
		
		if(i >= 0) {
			line.substring(i);
		} else {
			line = line.substring(line.lastIndexOf(" ") + 1);
		}
		
		line = resourcePath + line;
		return line;
	}
}