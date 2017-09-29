package engine.physics;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class World {

	private final PhysicsVec2 gravity = new PhysicsVec2(0, 300);
	private final List<Contact> contacts = new ArrayList<Contact>();
	private final List<Contact> contactsCache = new ArrayList<Contact>();
	
	private final List<RigidBody> dynamicBodies = new ArrayList<RigidBody>();
	private final List<RigidBody> allBodies = new ArrayList<RigidBody>();
	
	private RigidBody body;
	
	public World() {}
	
	public RigidBody getBody() {
		return body;
	}
	
	public void setBody(RigidBody rigidBody) {
		this.body = rigidBody;
	}
	
	public PhysicsVec2 getGravity() {
		return gravity;
	}
	
	public List<RigidBody> getAllBodies() {
		return allBodies;
	}
	
	public List<RigidBody> getDynamicBodies() {
		return dynamicBodies;
	}
	
	public void addRigidBody(RigidBody body) {
		allBodies.add(body);
		if(body.isDynamic()) {
			dynamicBodies.add(body);
		}
	}
	
	public void update() {
		long originalDelta = PhysicsTime.delta;
		int div = 10;
		PhysicsTime.delta = PhysicsTime.delta / div;
		for(int r = 0; r < div; r++) {
			updateInternal();
		}
		
		PhysicsTime.delta = originalDelta;
	}
	
	public void updateInternal() {
		handleOnCollisionOut();
		handleOnCollisionEnter();
		
		for(RigidBody rigidBody : dynamicBodies) {
			rigidBody.updateVelocity();
		}
		
		for(Contact contact : contacts) {
			contact.resolveCollision();
		}
		
		for(RigidBody rigidBody : dynamicBodies) {
			rigidBody.update();
		}
		
		for(RigidBody rigidBody : dynamicBodies) {
			rigidBody.updatePosition();
		}
		
		for(Contact contact : contacts) {
			contact.correctPosition();
		}
	}
	
	private void handleOnCollisionOut() {
		Iterator<Contact> icontact = contacts.iterator();
		while(icontact.hasNext()) {
			Contact contact = icontact.next();
			RigidBody rb1 = contact.getRBA();
			RigidBody rb2 = contact.getRBB();
			if(rb1.getShape() instanceof Circle && rb2.getShape() instanceof Circle && !CollisionDetection.checkCollisionCircleCircle(rb1, rb2, contact)) {
				icontact.remove();
				onCollisionOut(contact);
				saveContactToCache(contact);
			} else if(rb1.getShape() instanceof Circle && rb2.getShape() instanceof StaticLine && !CollisionDetection.checkCollisionCircleStaticLine(rb1, rb2, contact)) {
				icontact.remove();
				onCollisionOut(contact);
				saveContactToCache(contact);
			} else if(rb2.getShape() instanceof Circle && rb1.getShape() instanceof StaticLine && !CollisionDetection.checkCollisionCircleStaticLine(rb1, rb2, contact)) {
				icontact.remove();
				onCollisionOut(contact);
				saveContactToCache(contact);
			}
		}
	}
	
	private void handleOnCollisionEnter() {
		for(RigidBody rb1 : allBodies) {
			RigidBody rb2 = body;
			
			if(!rb1.isDynamic() && !rb2.isDynamic()) {
				continue;
			}
			
			if(rb1 == rb2 || !rb1.isVisible() || !rb2.isVisible()) {
				continue;
			}
			
			Contact contact = getContactFromCache();
			if(rb1.getShape() instanceof Circle && rb2.getShape() instanceof Circle && !CollisionDetection.checkCollisionCircleCircle(rb1, rb2, contact)) {
				onCollision(contact);
				if(!contacts.contains(contact)) {
					contacts.add(contact);
					onCollisionEnter(contact);
				}
			} else if(rb1.getShape() instanceof Circle && rb2.getShape() instanceof StaticLine && CollisionDetection.checkCollisionCircleStaticLine(rb1, rb2, contact)) {
				onCollision(contact);
				if(!contacts.contains(contact)) {
					contacts.add(contact);
					onCollisionEnter(contact);
				}
			} else if(rb2.getShape() instanceof Circle && rb1.getShape() instanceof StaticLine && CollisionDetection.checkCollisionCircleStaticLine(rb2, rb1, contact)) {
				onCollision(contact);
				if(!contacts.contains(contact)) {
					contacts.add(contact);
					onCollisionEnter(contact);
				}
			}
		}
	}
	
	public void drawDebug(Graphics2D g) {
		for(RigidBody rigidBody : allBodies) {
			if(rigidBody.isVisible()) {
				rigidBody.drawDebug(g);
			}
		}
	}
	
	public void onCollisionEnter(Contact contact) {
		contact.getRBA().onCollisionEnter(contact.getRBB(), contact);
		contact.getRBB().onCollisionEnter(contact.getRBA(), contact);
	}
	
	public void onCollision(Contact contact) {
		contact.getRBA().onCollision(contact.getRBB(), contact);
		contact.getRBB().onCollision(contact.getRBA(), contact);
	}
	
	public void onCollisionOut(Contact contact) {
		contact.getRBA().onCollisionEnter(contact.getRBB(), contact);
		contact.getRBB().onCollisionEnter(contact.getRBA(), contact);
	}
	
	public Contact getContactFromCache() {
		if(contactsCache.isEmpty()) {
			return new Contact();
		}
		
		Contact contact = contactsCache.remove(contactsCache.size() - 1);
		return contact;
	}
	
	public void saveContactToCache(Contact contact) {
		contact.setRBA(null);
		contact.setRBB(null);
		contactsCache.add(contact);
	}
}