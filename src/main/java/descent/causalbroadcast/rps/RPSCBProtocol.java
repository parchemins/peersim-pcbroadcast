package descent.causalbroadcast.rps;

import descent.applications.DummyApp;
import descent.spray.Spray;
import peersim.core.Node;
import peersim.edsim.EDProtocol;

/**
 * Protocol that uses a random peer-sampling protocol to build the network
 * overlay and uses a causal broadcast to transmit messages to all the peers.
 */
public class RPSCBProtocol extends Spray implements EDProtocol {

	public CausalBroadcast cb;

	public RPSCBProtocol(String n) {
		super(n);
		this.cb = new CausalBroadcast(this.node, this.partialView, new DummyApp());
	}

	public void processEvent(Node arg0, int arg1, Object arg2) {

	}

}
