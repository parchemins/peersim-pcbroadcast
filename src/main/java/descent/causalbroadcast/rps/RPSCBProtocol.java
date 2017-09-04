package descent.causalbroadcast.rps;

import descent.applications.DummyApp;
import descent.rps.IMessage;
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

	/**
	 * @param node
	 *            The receiving node.
	 * @param pid
	 *            The protocol id of the receiving node
	 * @param event
	 *            The event raised, ie, here the received message.
	 */
	public void processEvent(Node node, int pid, Object event) {
		if (event instanceof MLockedBroadcast || event instanceof MRegularBroadcast
				|| event instanceof MUnlockBroadcast)
			this.cb.receive((IMessage) event);
	}

}
