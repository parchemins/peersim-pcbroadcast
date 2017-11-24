package descent.observers;

import descent.observers.structure.DictGraph;
import descent.observers.structure.IObserverProgram;
import descent.observers.structure.Stats;

public class PBuffers implements IObserverProgram {

	public void tick(long currentTick, DictGraph observer) {
		Stats statsUnsafe = observer.numberOfUnSafe();

		System.out.println(observer.size() + " " + observer.countArcs() + " " + observer.numberOfAliveNeighbors().mean
				+ " ||| " + statsUnsafe.mean);

	}

	public void onLastTick(DictGraph observer) {
		// TODO Auto-generated method stub

	}

}
