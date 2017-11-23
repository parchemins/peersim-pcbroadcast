package descent.observers;

import descent.observers.structure.DictGraph;
import descent.observers.structure.IObserverProgram;
import descent.observers.structure.Stats;
import peersim.config.Configuration;

public class PBuffers implements IObserverProgram {

	private final static String PAR_PROTOCOL = "protocol";
	private static int protocol;
	
	public PBuffers(String prefix) {
		PBuffers.protocol = Configuration.getPid(prefix + "." + PBuffers.PAR_PROTOCOL);
	}
	
	public void tick(long currentTick, DictGraph observer) {
		Stats statsUnsafe = observer.numberOfUnSafe(PBuffers.protocol);

		System.out.println(observer.size() + " " + observer.countArcs() + " " + observer.getViewSizeStats().toString()
				+ " ||| " + statsUnsafe.mean);

	}

	public void onLastTick(DictGraph observer) {
		// TODO Auto-generated method stub

	}

}
