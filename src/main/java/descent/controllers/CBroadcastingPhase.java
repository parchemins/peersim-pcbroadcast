package descent.controllers;

import peersim.config.Configuration;
import peersim.core.Control;

/**
 * Trigger a phase of broadcasting where peers emit messages. Messages must
 * reach every peers of the network.
 */
public class CBroadcastingPhase implements Control {

	private static final String PAR_MESSAGES = "messages";
	private Integer nbMessages = 0;
	private Integer[][] latency;

	public CBroadcastingPhase(String options) {
		this.nbMessages = Configuration.getInt(options + "." + CBroadcastingPhase.PAR_MESSAGES, 0);
	}

	public boolean execute() {
		// (TODO) create an timeline of events
		for (int i = 0; i < nbMessages; ++i) {
			// #1 choose a peer
			// #2 emit
		}
		return false;
	}

}
