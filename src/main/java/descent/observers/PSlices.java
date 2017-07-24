package descent.observers;

import java.util.ArrayList;

import descent.observers.structure.DictGraph;
import descent.observers.structure.IObserverProgram;
import descent.observers.structure.Stats;

public class PSlices implements IObserverProgram {

	public void tick(long currentTick, DictGraph observer) {
		ArrayList<Integer> distances = observer.getDistancesDiscrete();
		ArrayList<Integer> distribution = observer.distributionInSlices();
		Stats distanceSlice = observer.distanceFromPerfectSlices();

		System.out.println(observer.size() + " " + distribution + " " + distances + " " + " " + distanceSlice);

	}

	public void onLastTick(DictGraph observer) {
		System.out.println(observer.networkxTManDigraph("AHorseWithNoName"));
	}

}
