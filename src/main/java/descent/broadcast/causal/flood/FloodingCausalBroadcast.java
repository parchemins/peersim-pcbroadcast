package descent.broadcast.causal.flood;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.IteratorUtils;

import descent.bidirectionnal.MClose;
import descent.bidirectionnal.MOpen;
import descent.broadcast.reliable.MReliableBroadcast;
import descent.broadcast.reliable.ReliableBroadcast;
import descent.rps.APeerSampling;
import descent.rps.IMessage;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;

/**
 * Causal broadcast that is preventive: messages arrives ready to be delivered.
 * It makes use of FIFO channels. It maintains buffers of messages to handle
 * dynamicity.
 * 
 */
public class FloodingCausalBroadcast extends ReliableBroadcast implements EDProtocol, CDProtocol {

	private static final String PAR_PMESSAGE = "pmessage";
	private static Double pmessage;

	public HashMap<Node, ArrayList<MReliableBroadcast>> buffers;

	// (TODO) retries number and counters as id

	public FloodingCausalBroadcast(String prefix) {
		super(prefix);

		FloodingCausalBroadcast.pmessage = Configuration.getDouble(prefix + "." + FloodingCausalBroadcast.PAR_PMESSAGE,
				0.);

		this.buffers = new HashMap<Node, ArrayList<MReliableBroadcast>>();
	}

	public FloodingCausalBroadcast() {
		super();
		this.buffers = new HashMap<Node, ArrayList<MReliableBroadcast>>();
	}

	/**
	 * Broadcast the message with the guarantee that the delivery follows the happen
	 * before relationship.
	 * 
	 * @param message
	 *            The message to broadcast.
	 */
	public void cbroadcast(IMessage message) {
		MReliableBroadcast mrb = this.rBroadcast(new MRegularBroadcast(message));
		for (Node neighbor : this.buffers.keySet()) {
			this.buffers.get(neighbor).add(mrb);
		}
	}

	@Override
	public void rDeliver(MReliableBroadcast m) {
		for (Node neigbhor : this.buffers.keySet()) {
			this.buffers.get(neigbhor).add(m);
		}
		super.rDeliver(m);
	}

	public void cDeliver(MRegularBroadcast m) {
		// nothing
	}

	/**
	 * A new neighbors has been added, the link must be acknowledged before it is
	 * used.
	 * 
	 * @param n
	 *            The new neighbor.
	 */
	public void opened(Node n) {
		APeerSampling ps = (APeerSampling) this.node.getProtocol(FastConfig.getLinkable(ReliableBroadcast.pid));
		List<Node> neighborhood = IteratorUtils.toList(ps.getPeers().iterator());
		if (neighborhood.size() - this.buffers.size() >= 1) {
			this.buffers.put(n, new ArrayList<MReliableBroadcast>());
			this._sendLocked(n);
		}
	}

	/**
	 * Just received a locked message. Must acknowledge it.
	 * 
	 * @param from
	 *            The node that sent the locked message.
	 * @param to
	 *            The node that must acknowledge the locked message.
	 */
	private void receiveLocked(Node from, Node to) {
		MUnlockBroadcast mu = new MUnlockBroadcast(from, to);
		Transport t = ((Transport) this.node.getProtocol(FastConfig.getTransport(FloodingCausalBroadcast.pid)));

		APeerSampling ps = (APeerSampling) this.node.getProtocol(FastConfig.getLinkable(ReliableBroadcast.pid));
		List<Node> neighborhood = IteratorUtils.toList(ps.getPeers().iterator());
		// #1 if the origin of the locked message is in our direct neighborhood
		if (neighborhood.contains(from)) {
			t.send(this.node, from, mu, FloodingCausalBroadcast.pid);
		} else { // #2 otherwise use forwarding
			// (TODO) add the route in the locked message to send back ack
			for (Node n : neighborhood) {
				t.send(this.node, n, new MForward(n, mu), FloodingCausalBroadcast.pid);
			}
		}
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
	private void receiveAck(Node from, Node to) {
		if (this.buffers.containsKey(to)) {
			Transport t = ((Transport) this.node.getProtocol(FastConfig.getTransport(FloodingCausalBroadcast.pid)));
			// #1 empty the buffer
			for (int i = 0; i < this.buffers.get(to).size(); ++i) {
				t.send(this.node, to, this.buffers.get(to).get(i), FloodingCausalBroadcast.pid);
			}
			// #2 remove the entry from the buffer
			this.buffers.remove(to);
		}
	}

	/**
	 * A peer is removed from neighborhoods. Clean buffers if need be.
	 * 
	 * @param n
	 *            The removed neighbor.
	 * 
	 */
	public void closed(Node n) {
		this.buffers.remove(n);
	}

	@Override
	public void processEvent(Node node, int protocolId, Object message) {
		super.processEvent(node, protocolId, message);

		if (message instanceof MOpen) {
			MOpen mo = (MOpen) message;
			this.opened(mo.to);
		} else if (message instanceof MClose) {
			MClose mc = (MClose) message;
			this.closed(mc.to);
		} else if (message instanceof MForward) {
			MForward mf = (MForward) message;
			this.onForward(mf.to, mf.getPayload());
		} else if (message instanceof MLockedBroadcast) {
			MLockedBroadcast mlb = (MLockedBroadcast) message;
			this.receiveLocked(mlb.from, mlb.to);
		} else if (message instanceof MUnlockBroadcast) {
			MUnlockBroadcast mu = (MUnlockBroadcast) message;
			this.receiveAck(mu.from, mu.to);
		}
	}

	@Override
	public void nextCycle(Node node, int protocolId) {
		super.nextCycle(node, protocolId);

		if (CommonState.r.nextDouble() < FloodingCausalBroadcast.pmessage) {
			// (TODO)
		}
	}

	/**
	 * Send a locked message to a remote peer that we want to add in our
	 * neighborhood.
	 * 
	 * @param to
	 *            The peer to reach.
	 */
	private void _sendLocked(Node to) {
		MLockedBroadcast mlb = new MLockedBroadcast(this.node, to);
		this._sendToAllNeighborsButNotBroadcast(new MForward(to, mlb));
	}

	/**
	 * Forward a message to a remote peer.
	 * 
	 * @param to
	 *            The peer to forward the message to.
	 * @param message
	 *            The message to forward.
	 */
	private void onForward(Node to, IMessage message) {
		APeerSampling ps = (APeerSampling) this.node.getProtocol(FastConfig.getLinkable(ReliableBroadcast.pid));
		List<Node> neighborhood = IteratorUtils.toList(ps.getPeers().iterator());
		if (neighborhood.contains(to)) {
			((Transport) this.node.getProtocol(FastConfig.getTransport(FloodingCausalBroadcast.pid))).send(this.node,
					to, message, FloodingCausalBroadcast.pid);
		}
	}

	protected void _sendToAllNeighborsButNotBroadcast(IMessage m) {
		APeerSampling ps = (APeerSampling) this.node.getProtocol(FastConfig.getLinkable(ReliableBroadcast.pid));

		for (Node q : ps.getAliveNeighbors()) {
			// (XXX) maybe remove q from peer-sampling, cause it may be
			// scrambled too quick.
			// Or maybe put
			// EDProtocol even for cycles. So all scrambles do not happen at a
			// same time.
			if (!this.buffers.containsKey(q)) {
				((Transport) this.node.getProtocol(FastConfig.getTransport(FloodingCausalBroadcast.pid)))
						.send(this.node, q, m, FloodingCausalBroadcast.pid);
			}
		}
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
		APeerSampling ps = (APeerSampling) this.node.getProtocol(FastConfig.getLinkable(ReliableBroadcast.pid));

		for (Node q : ps.getAliveNeighbors()) {
			// (XXX) maybe remove q from peer-sampling, cause it may be
			// scrambled too quick.
			// Or maybe put
			// EDProtocol even for cycles. So all scrambles do not happen at a
			// same time.
			if (!this.buffers.containsKey(q)) {
				((Transport) this.node.getProtocol(FastConfig.getTransport(FloodingCausalBroadcast.pid)))
						.send(this.node, q, m, FloodingCausalBroadcast.pid);
			}
		}
	}

}
