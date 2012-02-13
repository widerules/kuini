package pl.edu.uj.tcs.kuini.model.actions;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import pl.edu.uj.tcs.kuini.model.ActorType;
import pl.edu.uj.tcs.kuini.model.LivePath;
import pl.edu.uj.tcs.kuini.model.Path;
import pl.edu.uj.tcs.kuini.model.geometry.Position;
import pl.edu.uj.tcs.kuini.model.geometry.Vector;
import pl.edu.uj.tcs.kuini.model.live.ILiveActor;
import pl.edu.uj.tcs.kuini.model.live.ILiveState;

public class BoidMoveAction implements IAction {
	private final float followPathFactor = 1.0f;
	private final float velocityFactor = 0.5f;
	private final float repellFactor = 0.5f;
	private final Random random;
	private final ICollisionResolver collisionResolver;
	
	public BoidMoveAction(Random random, ICollisionResolver collisionResolver){
		this.random = random;
		this.collisionResolver = collisionResolver;
	}

	@Override
	public void performAction(ILiveActor actor, float elapsedTime,
			ILiveState state) {
		List<ILiveActor> repellers = new LinkedList<ILiveActor>();
		for(ILiveActor neighbour : state.getNeighbours(actor.getPosition(), actor.getRadius()*5)){
			if(neighbour.getPosition().distanceTo(actor.getPosition()) < 3*actor.getRadius() && (
					neighbour.getActorType() == ActorType.ANT || (neighbour.getActorType() == ActorType.ANTHILL && 
					neighbour.getPlayerId() != actor.getPlayerId())))
				repellers.add(neighbour);
		}
		float maxDistance = actor.getSpeed()*elapsedTime;
		Position velocityTarget = getVelocityTarget(actor, elapsedTime, maxDistance);
		Position pathTarget = getPathTarget(actor, maxDistance);
		Position repellAntsTarget = getRepellTarget(actor, repellers, maxDistance);
		
		Position target = average(new Position[]{velocityTarget, pathTarget, repellAntsTarget}, 
				new float[]{velocityFactor, followPathFactor, repellFactor});
		
		Vector direction = new Vector(actor.getPosition(), target);
		actor.setAngle(direction.getAngle());
		actor.setPosition(collisionResolver.computePosition(actor, target, state));
	}

	private Position getRepellTarget(ILiveActor actor, List<ILiveActor> friends, float maxDistance) {
		return normalize(actor.getPosition(), 
				actor.getPosition().symmetry(averagePositions(friends, actor.getPosition())),
				maxDistance);
	}
	
	private Position averagePositions(List<ILiveActor> actors, Position resultIfEmpty){
		if(actors.isEmpty())return resultIfEmpty;
		float x=0, y=0;
		for(ILiveActor actor : actors){
			Position p = actor.getPosition();
			x += p.getX();
			y += p.getY();
		}
		x /= actors.size();
		y /= actors.size();
		return new Position(x, y);
	}

	private Position average(Position[] positions, float[] factors){
		float x=0, y=0;
		float sum = 0;
		for(int i=0;i<positions.length;i++){
			x += factors[i]*positions[i].getX();
			y += factors[i]*positions[i].getY();
			sum += factors[i];
		}
		x /= sum;
		y /= sum;
		return new Position(x, y);
	}
	
	private Position getVelocityTarget(ILiveActor actor, float elapsedTime, float maxDistance){
		return normalize(actor.getPosition(),
				new Vector(actor.getPosition(), 
		        actor.getAngle()+((float) ((random.nextFloat()-0.5)*0.5*elapsedTime)), 
				100.0f).getTarget(),
				maxDistance);
	}
	
	private Position normalize(Position actorPosition, Position target, float distance){
		if(actorPosition.distanceTo(target) < distance)return target;
		float angle = new Vector(actorPosition, target).getAngle();
		return new Vector(actorPosition, angle, distance).getTarget();
	}
	
	private Position getPathTarget(ILiveActor actor, float maxDistance) {
		LivePath path = actor.getLivePath();
		while(true){
			if(!path.hasNextPosition()){
				actor.setPath(Path.EMPTY_PATH);
				return actor.getPosition();
			}
			if(actor.getPosition().distanceTo(path.currentPosition()) < 2*actor.getRadius())
				path.nextPosition();
			else 
				break;
		}
		return normalize(actor.getPosition(), path.currentPosition(), maxDistance);
	}

}
