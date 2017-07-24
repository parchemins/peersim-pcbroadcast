package descent.observers;

import descent.observers.structure.DictGraph;
import descent.observers.structure.IObserverProgram;
import descent.observers.structure.Stats;

public class PIntervalMerger implements IObserverProgram {

	public void tick(long currentTick, DictGraph observer) {
		Stats s = observer.maxDepthOfIdentifiers();
		System.out.println(observer.size() + " " + s.min + " " + s.max + " " + s.mean + " " + s.stdDev);
	}

	public void onLastTick(DictGraph observer) {
		// TODO Auto-generated method stub

	}

}
