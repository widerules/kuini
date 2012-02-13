package pl.edu.uj.tcs.kuini.model.factories;

import pl.edu.uj.tcs.kuini.model.live.ILiveActor;
import pl.edu.uj.tcs.kuini.model.live.ILiveState;

public interface IAntFactory {
	ILiveActor getAnt(ILiveState state, int playerId);
}
