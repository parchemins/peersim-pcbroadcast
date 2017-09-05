package descent.controllers;

import descent.causalbroadcast.rps.RPSCBProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

/**
 * Trigger a phase of broadcasting where peers emit messages. Messages must
 * reach every peers of the network.
 */
public class CBroadcastingPhase implements Control {

	// probability to create a message at each step per peer
	private static final String PAR_MESSAGES = "pmessage";
	private Double pMessage = 0.;

	public CBroadcastingPhase(String options) {
		this.pMessage = Configuration.getDouble(options + "." + CBroadcastingPhase.PAR_MESSAGES, 0.);
	}

	public boolean execute() {
		for (int i = 0; i < Network.size(); ++i) {
			Node n = Network.get(CommonState.r.nextInt(Network.size()));
			if (CommonState.r.nextDouble() < pMessage) {
				RPSCBProtocol rpscb = (RPSCBProtocol) n.getProtocol(RPSCBProtocol.pid);
				rpscb.cb.app.send(null);
			}
		}
		return false;
	}

}
