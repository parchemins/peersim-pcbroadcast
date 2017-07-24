package descent.observers.properties;

import descent.observers.structure.DictGraph;
import descent.observers.structure.IObserverProgram;

/**
 * Created by julian on 15/05/15.
 */
public class PClusteringCoefficient implements IObserverProgram {

	public void tick(long currentTick, DictGraph observer) {
		System.out.println(observer.countArcs() + " " + observer.meanClusterCoefficient() + " "
				+ observer.globalClusterCoefficient());
	}

	public void onLastTick(DictGraph observer) {

	}
}
