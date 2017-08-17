package descent.controllers;

import descent.intervalmerger.IntervalMerger;
import itc.Id;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;

/**
 * It modifies the peers to simulate join/merge phases of interval tree clocks.
 *
 */
public class CChurnPhase implements Control {

	private static final String PAR_DATE = "date";
	private static final String PAR_N = "n";

	private final int DATE; // when
	private final int N; // number of join+merge

	public CChurnPhase(String n) {
		this.DATE = Configuration.getInt(n + "." + CChurnPhase.PAR_DATE);
		this.N = Configuration.getInt(n + "." + CChurnPhase.PAR_N);
	}

	public boolean execute() {
		if (CommonState.getTime() == this.DATE) {
			for (int i = 0; i < this.N; ++i) {
				// leaver -- merge -> toMergeWith; rnToJoinFrom -- join ->
				// leaver
				int rnLeaver = CommonState.r.nextInt(Network.size());
				int rnToMergeWith = CommonState.r.nextInt(Network.size());
				while (rnToMergeWith == rnLeaver) {
					rnToMergeWith = CommonState.r.nextInt(Network.size());
				}
				int rnToJoinFrom = CommonState.r.nextInt(Network.size());
				while (rnToJoinFrom == rnLeaver) {
					rnToJoinFrom = CommonState.r.nextInt(Network.size());
				}

				IntervalMerger imLeaver = (IntervalMerger) Network.get(rnLeaver).getProtocol(IntervalMerger.pid);
				IntervalMerger imToMergeWith = (IntervalMerger) Network.get(rnToMergeWith)
						.getProtocol(IntervalMerger.pid);
				IntervalMerger imToJoinFrom = (IntervalMerger) Network.get(rnToJoinFrom)
						.getProtocol(IntervalMerger.pid);

				// System.out.println("BEFOAR");
				// System.out.println(imLeaver.ct.tracker.getId());
				// System.out.println(imToMergeWith.ct.tracker.getId());
				imToMergeWith.borrowAll(imLeaver);
				imLeaver.borrow(imToJoinFrom);

				// System.out.println("AFTAR");
				// System.out.println(imToMergeWith.ct.tracker.getId());
			}
		}
		return false;
	}

}
