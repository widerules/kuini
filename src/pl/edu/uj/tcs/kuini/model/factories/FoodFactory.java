package pl.edu.uj.tcs.kuini.model.factories;

import java.util.Random;

import pl.edu.uj.tcs.kuini.model.Actor;
import pl.edu.uj.tcs.kuini.model.ActorType;
import pl.edu.uj.tcs.kuini.model.Path;
import pl.edu.uj.tcs.kuini.model.actions.DecayAction;
import pl.edu.uj.tcs.kuini.model.actions.IAction;
import pl.edu.uj.tcs.kuini.model.geometry.Position;
import pl.edu.uj.tcs.kuini.model.live.ILiveActor;
import pl.edu.uj.tcs.kuini.model.live.ILiveState;

public class FoodFactory implements IFoodFactory {
	private final IAction foodAction = new DecayAction(1.0f);
	private final Random random;
	
	public FoodFactory(Random random){
		this.random = random;
	}

	@Override
	public ILiveActor getFood(ILiveState state) {
		Position p = new Position(random.nextFloat()*state.getWidth(), random.nextFloat()*state.getHeight());
		return new Actor(ActorType.FOOD, state.nextActorId(), state.getFoodPlayerId(), 
				foodAction, p, 0.5f, 0, 100, 100, Path.EMPTY_PATH);
	}

}
