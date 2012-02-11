package pl.edu.uj.tcs.kuini.model;

import java.util.List;
import java.util.Map;

public interface IState {

    public List<IActor> getActorStates();
    public Map<Integer, IPlayer> getPlayerStatesById();
    public float getWidth();
    public float getHeight();
    /**
     * If game is in progress returns -1,
     * else return winner id.
     * @return winner id
     */
    public int getWinnerId();
    public boolean isGameEnded();
}
