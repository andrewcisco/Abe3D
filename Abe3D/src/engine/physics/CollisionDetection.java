package engine.physics;

public class CollisionDetection {

	private static final PhysicsVec2 auxVec = new PhysicsVec2();
	private static final PhysicsVec2 auxVec2 = new PhysicsVec2();
	private static final PhysicsVec2 auxVec3 = new PhysicsVec2();
	
	public static boolean checkCollisionCircleCircle(RigidBody rba, RigidBody rbb, Contact contact) {
		contact.setRBA(rba);
		contact.setRBB(rbb);
		Circle csa = (Circle) rba.getShape();
		Circle csb = (Circle) rbb.getShape();
		auxVec.set(rba.getPosition());
		auxVec.sub(rbb.getPosition());
		double penetration = csa.getRadius() + csb.getRadius() - auxVec.length();
		if(penetration > 0) {
			contact.getNormal().set(auxVec);
			contact.getNormal().normalize();
			auxVec.scale(csb.getRadius() / (csa.getRadius() + csb.getRadius()));
			auxVec.add(rbb.getPosition());
			contact.getPosition().set(auxVec);
			contact.setPenetration(penetration);
		}
		
		return penetration > 0;
	}
	
	public static boolean checkCollisionCircleStaticLine(RigidBody rba, RigidBody rbb, Contact contact) {
		contact.setRBA(rbb);
		contact.setRBB(rba);
		Circle csa = (Circle) rba.getShape();
		StaticLine lsb = (StaticLine) rbb.getShape();
		auxVec.set(lsb.getPoint1());
		auxVec.sub(lsb.getPoint2());
		auxVec2.set(lsb.getPoint1());
		auxVec2.sub(rba.getPosition());
		double p1 = auxVec2.dot(auxVec);
		auxVec3.set(lsb.getPoint2());
		auxVec3.sub(rba.getPosition());
		double p2 = auxVec3.dot(auxVec);
		if(p1 * p2 < 0) {
			auxVec.setPerp();
			contact.getNormal().set(auxVec);
			contact.getNormal().normalize();
			contact.getNormal().scale(-1);
			contact.getPosition().set(contact.getNormal());
			contact.getPosition().scale(csa.getRadius());
			contact.getPosition().add(rba.getPosition());
			double penetration = csa.getRadius() - Math.abs(contact.getNormal().dot(auxVec2));
			contact.setPenetration(penetration);
			return penetration > 0;
		} else {
			double penetration = csa.getRadius() - auxVec2.length();
			if(penetration > 0) {
				contact.getNormal().set(auxVec2);
				contact.getNormal().normalize();
				contact.setPenetration(penetration);
				contact.getPosition().set(lsb.getPoint1());
				return true;
			}
			
			penetration = csa.getRadius() - auxVec3.length();
			if(penetration > 0) {
				contact.getNormal().set(auxVec2);
				contact.getNormal().normalize();
				contact.setPenetration(penetration);
				contact.getPosition().set(lsb.getPoint1());
				return true;
			}
		}
		
		return false;
	}
}