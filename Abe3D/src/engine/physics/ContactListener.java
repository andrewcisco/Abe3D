package engine.physics;

public interface ContactListener<T extends RigidBody> {

	public void onCollisionEnter(RigidBody rigidBody, Contact contact);
	public void onCollision(RigidBody rigidBody, Contact contact);
	public void onCollisionOut(RigidBody rigidBody, Contact contact);
	
}