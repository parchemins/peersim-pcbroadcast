package descent.broadcast.reliable;

import descent.rps.APeerSampling;
import descent.rps.IMessage;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;

/**
 * Broadcast protocol that reliably delivers a message to all network members
 * exactly once.
 */
public class ReliableBroadcast implements EDProtocol, CDProtocol {

	private final static String PAR_PID = "pid";
	public static Integer pid;
	
	public VVwE received;

	private Integer counter = 0;
	protected Node node;

	public ReliableBroadcast(String prefix) {
		ReliableBroadcast.pid = Configuration.getPid(prefix + "." + ReliableBroadcast.PAR_PID);
		this.received = new VVwE();
	}

	public ReliableBroadcast() {
		this.received = new VVwE();
	}

	/**
	 * Broadcast a message to all peers of the network.
	 * 
	 * @param m
	 *            The message to send.
	 */
	public MReliableBroadcast rBroadcast(IMessage m) { // b_p(m)
		++this.counter;
		MReliableBroadcast mrb = new MReliableBroadcast(this.node.getID(), this.counter, m);
		this.received.add(mrb.id, mrb.counter);
		this._sendToAllNeighbors(mrb);
		this.rDeliver(mrb);
		return mrb;
	}

	/**
	 * Should deliver the message to all applications depending on this.
	 * 
	 * @param m
	 *            The delivered message.
	 */
	public void rDeliver(MReliableBroadcast m) {
		// nothing (TODO)?
	}

	public void processEvent(Node node, int protocolId, Object message) {
		this._setNode(node);

		if (message instanceof MReliableBroadcast) { // r_p(m)
			MReliableBroadcast mrb = (MReliableBroadcast) message;
			if (!this.received.contains(mrb.id, mrb.counter)) {
				this.received.add(mrb.id, mrb.counter);
				this._sendToAllNeighbors(mrb);
				this.rDeliver(mrb);
			}
		}
	}

	protected void rSend(Node node, MReliableBroadcast m) {
		((Transport) node.getProtocol(FastConfig.getTransport(ReliableBroadcast.pid))).send(this.node, node, m,
				ReliableBroadcast.pid);
	}

	/**
	 * Send the reliable broadcast message to all neighbors.
	 * 
	 * @param m
	 *            The message to send.
	 */
	protected void _sendToAllNeighbors(MReliableBroadcast m) {
		APeerSampling ps = (APeerSampling) this.node.getProtocol(FastConfig.getLinkable(ReliableBroadcast.pid));

		for (Node q : ps.getAliveNeighbors()) {
			this.rSend(q, m);
		}
	}

	@Override
	public Object clone() {
		return new ReliableBroadcast();
	}

	public void nextCycle(Node node, int protocolId) {
		this._setNode(node);
	}

	/**
	 * Lazy loading the node.
	 * 
	 * @param n
	 *            The node hosting this protocol.
	 */
	protected void _setNode(Node n) {
		if (this.node == null) {
			this.node = n;
		}
	}

}
