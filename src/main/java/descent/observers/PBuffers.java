package descent.observers;

import descent.observers.structure.DictGraph;
import descent.observers.structure.DictGraph.StatsPair;
import descent.observers.structure.IObserverProgram;
import descent.observers.structure.Stats;

public class PBuffers implements IObserverProgram {

	public void tick(long currentTick, DictGraph observer) {
		Stats statsUnsafe = observer.numberOfUnSafe();
		StatsPair statsDistBiSprayAndFlood = observer.getStatsAboutDistances(20);


		System.out.println(observer.size() + " " + observer.countArcs() + " " + observer.numberOfAliveNeighbors().mean
				+ " ||| " + statsUnsafe.mean + " ||| " + statsDistBiSprayAndFlood.a.mean + " ||| " + statsDistBiSprayAndFlood.b.mean);

	}

	public void onLastTick(DictGraph observer) {
		// TODO Auto-generated method stub

	}

}
