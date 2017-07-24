package descent.observers.properties;

import descent.observers.structure.DictGraph;
import descent.observers.structure.IObserverProgram;

public class PModularityCoefficient implements IObserverProgram {

	public void tick(long currentTick, DictGraph observer) {
		System.out.println(observer.countArcs() + " "
				+ observer.modularityCoefficient());
	}

	public void onLastTick(DictGraph observer) {

	}
}
