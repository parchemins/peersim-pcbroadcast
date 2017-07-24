package descent.observers.properties;

import descent.observers.structure.DictGraph;
import descent.observers.structure.IObserverProgram;

/**
 * Created by julian on 11/05/15.
 */
public class PPartialViewSize implements IObserverProgram {

	public void tick(long currentTick, DictGraph observer) {
		System.out.println(observer.getViewSizeStats().mean);
	}

	public void onLastTick(DictGraph observer) {

	}
}
