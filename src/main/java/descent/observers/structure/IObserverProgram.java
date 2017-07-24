package descent.observers.structure;

/**
 * Created by julian on 4/29/15.
 */
public interface IObserverProgram {

    public void tick(long currentTick, DictGraph observer);

    public void onLastTick(DictGraph observer);

}
