package engine.core;

public class Time {

	public static long delta;
	private static long lastUpdatedTime = System.nanoTime();
	
	public static void update() {
		long currentTime = System.nanoTime();
		delta = currentTime - lastUpdatedTime;
		lastUpdatedTime = currentTime;
	}
}