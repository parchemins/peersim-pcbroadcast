package descent.observers;

import descent.broadcast.causal.preventive.PreventiveCausalBroadcast;
import descent.observers.structure.DictGraph;
import descent.observers.structure.DictGraph.StatsPair;
import descent.observers.structure.IObserverProgram;
import descent.observers.structure.Stats;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.transport.Transport;

public class PBuffers implements IObserverProgram {

	public void tick(long currentTick, DictGraph observer) {
		Stats statsUnsafe = observer.numberOfUnSafe();
		StatsPair statsDistBiSprayAndFlood = observer.getStatsAboutDistances(20);
		Transport t = (Transport) CommonState.getNode()
				.getProtocol(FastConfig.getTransport(PreventiveCausalBroadcast.pid));

		System.out.println(t.getLatency(null, null) + " ||| " + observer.size() + " " + observer.countArcs() + " "
				+ observer.numberOfAliveNeighbors().mean + " ||| " + statsUnsafe.mean + " ||| "
				+ statsDistBiSprayAndFlood.a.mean + " ||| " + statsDistBiSprayAndFlood.b.mean);

	}

	public void onLastTick(DictGraph observer) {
		// TODO Auto-generated method stub

	}

}
