package engine.physics;

@SuppressWarnings("rawtypes")
public abstract class ContactAdapter<T extends RigidBody> implements ContactListener {

	protected T owner;
	
	public ContactAdapter(T t) {
		this.owner = t;
	}
	
	@Override
	public void onCollisionEnter(RigidBody rigidBody, Contact contact) {
		
	}
	
	@Override
	public void onCollision(RigidBody rigidBody, Contact contact) {
		
	}
	
	@Override
	public void onCollisionOut(RigidBody rigidBody, Contact contact) {
		
	}
}