package descent.observers;

import descent.observers.structure.DictGraph;
import descent.observers.structure.IObserverProgram;
import descent.observers.structure.Stats;

public class PIntervalMerger implements IObserverProgram {

	public void tick(long currentTick, DictGraph observer) {
		Stats s = observer.maxDepthOfIdentifiers();
		Stats sSize = observer.sizeOfIdentifier();
		System.out.println(observer.size() + " " + s.min + " " + s.max + " " + s.mean + " " + s.stdDev + " | "
				+ sSize.min + " " + sSize.max + " " + sSize.mean + " " + sSize.stdDev);
	}

	public void onLastTick(DictGraph observer) {
		// TODO Auto-generated method stub

	}

}
