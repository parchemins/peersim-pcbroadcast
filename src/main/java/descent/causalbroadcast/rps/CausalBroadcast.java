package descent.causalbroadcast.rps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import descent.applications.IApplication;
import descent.causalbroadcast.IBroadcast;
import descent.rps.IMessage;
import descent.rps.PartialView;
import peersim.config.FastConfig;
import peersim.core.Node;
import peersim.transport.Transport;

/**
 * A causal broadcast built on top of peer-sampling protocols such as Cyclon or
 * Spray.
 */
public class CausalBroadcast implements IBroadcast {

	Node p; // The identifier of the peer
	PartialView partialView; // The partial view of provided by the PSP
	HashMap<Node, List<IMessage>> buffers; // Buffering messages
	IApplication app;

	public CausalBroadcast(Node p, PartialView partialView, IApplication app) {
		this.p = p;
		this.partialView = partialView;
		this.buffers = new HashMap<Node, List<IMessage>>();
		this.app = app;
	}

	/**
	 * Called when a connection is established. It checks if the connection is
	 * safe and acts accordingly
	 * 
	 * @param i
	 *            The initiator of the link.
	 * @param q
	 *            The neighbor reached by the link.
	 */
	public void onChannelOpen(Node i, Node q) {
		if (!i.equals(this.p) && this.partialView.size() - this.buffers.size() > 1 && !this.buffers.containsKey(q)) {
			this.buffers.put(q, new ArrayList<IMessage>());
			this.broadcast(new MLockedBroadcast(this.p, q));
		}
	}

	/**
	 * Called when a connection is removed, i.e., all arcs leading to the node
	 * in argument are removed.
	 * 
	 * @param q
	 *            The neighbor reached by the removed link.
	 */
	public void onChannelClosed(Node q) {
		this.buffers.remove(q);
	}

	/**
	 * Broadcast a message to the whole network. The broadcast is causal.
	 * 
	 * @param message
	 *            The message to send.
	 */
	public void broadcast(IMessage payload) {
		MRegularBroadcast message = new MRegularBroadcast(payload);
		for (List<IMessage> b : this.buffers.values())
			b.add(message);
		this._broadcast(message);
	}

	private void _broadcast(IMessage message) {
		for (Node q : new HashSet<Node>(partialView.getPeers()))
			this.sendTo(q, message);
	}

	/**
	 * Receive a message from one of the entering channels.
	 * 
	 * @param message
	 *            The received message.
	 */
	public void receive(IMessage message) {
		// (TODO) if !already received message
		if (message instanceof MLockedBroadcast) {
			ArcPair fromto = (ArcPair) message.getPayload();
			if (fromto.equals(this.p)) {
				this.sendTo(fromto.getFrom(), new MUnlockBroadcast(this.p));
			} else {
				this._broadcast(message);
			}
		}
		if (message instanceof MUnlockBroadcast) {
			Node to = (Node) message.getPayload();
			if (this.buffers.containsKey(to)) {
				for (IMessage m : this.buffers.get(to))
					this.sendTo(to, m);
				this.buffers.remove(to);
			}
		}
		if (message instanceof MRegularBroadcast) {
			this.app.deliver(message.getPayload());
			this.broadcast((IMessage) message.getPayload());
		}

	}

	/**
	 * Send a message to a particular node.
	 * 
	 * @param q
	 *            The node to send a message to.
	 * @param m
	 *            The message to send.
	 */
	private void sendTo(Node q, IMessage m) {
		((Transport) q.getProtocol(FastConfig.getTransport(RPSCBProtocol.pid))).send(q, this.p, m, RPSCBProtocol.pid);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		CausalBroadcast cb = new CausalBroadcast(this.p, (PartialView) this.partialView.clone(), this.app);
		cb.buffers = (HashMap<Node, List<IMessage>>) this.buffers.clone();
		return cb;
	}

}
