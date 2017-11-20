package descent.broadcast.causal.flood;

import java.util.ArrayList;
import java.util.HashMap;

import descent.broadcast.reliable.MReliableBroadcast;
import descent.broadcast.reliable.ReliableBroadcast;
import descent.rps.APeerSampling;
import descent.rps.IMessage;
import peersim.config.FastConfig;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;

/**
 * Causal broadcast that is preventive: messages arrives ready to be delivered.
 * It makes use of FIFO channels. It maintains buffers of messages to handle
 * dynamicity.
 * 
 */
public class FloodingCausalBroadcast extends ReliableBroadcast implements EDProtocol {

	HashMap<Node, ArrayList<IMessage>> buffers;

	// (TODO) retries number and counters as id

	public FloodingCausalBroadcast(String prefix) {
		super(prefix);
		this.buffers = new HashMap<Node, ArrayList<IMessage>>();
	}

	public FloodingCausalBroadcast() {
		super();
		this.buffers = new HashMap<Node, ArrayList<IMessage>>();
	}

	/**
	 * A new neighbors has been added, the link must be acknowledged before it is
	 * used.
	 * 
	 * @param n
	 *            The new neighbor.
	 */
	public void opened(Node n) {

	}

	/**
	 * Just received a locked message. Must acknowledge it.
	 * 
	 * @param from
	 *            The node that sent the locked message.
	 * @param to
	 *            The node that must acknowledge the locked message.
	 */
	private void onLocked(Node from, Node to) {

	}

	/**
	 * Just received an acknowledged message. Must empty the corresponding buffer
	 * etc.
	 * 
	 * @param from
	 *            We are the origin
	 * @param to
	 *            The node that acknowledged our locked message.
	 */
	private void onAck(Node from, Node to) {

	}

	/**
	 * A peer is removed from neighborhoods. Clean buffers if need be.
	 * 
	 * @param n
	 *            The removed neighbor.
	 * 
	 */
	public void closed(Node n) {

	}

	@Override
	public void processEvent(Node node, int protocolId, Object message) {
		super.processEvent(node, protocolId, message);

	}

	/**
	 * Send the reliable broadcast message to all neighbors, excepts ones still
	 * buffering phase.
	 * 
	 * @param m
	 *            The message to send.
	 */
	@Override
	protected void _sendToAllNeighbors(MReliableBroadcast m) {
		APeerSampling ps = (APeerSampling) this.node.getProtocol(FastConfig.getLinkable(ReliableBroadcast.PID));

		for (Node q : ps.getAliveNeighbors()) {
			// (XXX) maybe remove q from peer-sampling, cause it may be scrambled too quick.
			// Or maybe put
			// EDProtocol even for cycles. So all scrambles do not happen at a same time.
			if (!buffers.containsKey(q)) {
				((Transport) node.getProtocol(FastConfig.getTransport(ReliableBroadcast.PID))).send(this.node, q, m,
						ReliableBroadcast.PID);
			}
		}
	}

}
