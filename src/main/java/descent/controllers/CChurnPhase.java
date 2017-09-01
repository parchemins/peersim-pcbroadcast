package descent.controllers;

import descent.causalbroadcast.itc.ITCCBProtocol;
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

				ITCCBProtocol imLeaver = (ITCCBProtocol) Network.get(rnLeaver).getProtocol(ITCCBProtocol.pid);
				ITCCBProtocol imToMergeWith = (ITCCBProtocol) Network.get(rnToMergeWith)
						.getProtocol(ITCCBProtocol.pid);
				ITCCBProtocol imToJoinFrom = (ITCCBProtocol) Network.get(rnToJoinFrom)
						.getProtocol(ITCCBProtocol.pid);

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
