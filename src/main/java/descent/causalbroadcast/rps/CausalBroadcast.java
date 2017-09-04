package descent.causalbroadcast.rps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import descent.applications.IApplication;
import descent.causalbroadcast.AMBroadcast;
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

	private final Node p; // The identifier of the peer
	private PartialView partialView; // The partial view
	private HashMap<Node, List<AMBroadcast>> buffers; // Buffering messages
	private IApplication app;

	public CausalBroadcast(Node p, PartialView partialView, IApplication app) {
		this.p = p;
		this.partialView = partialView;
		this.buffers = new HashMap<Node, List<AMBroadcast>>();
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
			this.buffers.put(q, new ArrayList<AMBroadcast>());

			this._broadcast(new MLockedBroadcast(VisibilityMatrix.get(this.p), this.p, q));
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
	 * Broadcast the message. This is called once per message. It increments a
	 * local counter used to make unique identifiers.
	 * 
	 * @param payload
	 *            The message to send.
	 */
	public void send(IMessage payload) {
		MRegularBroadcast message = new MRegularBroadcast(VisibilityMatrix.get(this.p), payload);
		this.broadcast(message);
	}

	/**
	 * Broadcast a message to the whole network. The broadcast is causal. This
	 * is called once per peer, for it acts has a forward mechanism.
	 * 
	 * @param message
	 *            The message to broadcast.
	 */
	public void broadcast(AMBroadcast message) {
		for (List<AMBroadcast> b : this.buffers.values())
			b.add(message);
		this._broadcast(message);
	}

	private void _broadcast(AMBroadcast message) {
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
		if (message instanceof MUnlockBroadcast) {
			Node to = ((MUnlockBroadcast) message).getPayload();
			if (this.buffers.containsKey(to)) {
				for (IMessage m : this.buffers.get(to))
					this.sendTo(to, m);
				this.buffers.remove(to);
			}
		} else {
			// only broadcasted messages are marked
			if (!VisibilityMatrix.alreadyReceived(this.p, (AMBroadcast) message)) {
				if (message instanceof MLockedBroadcast) {
					ArcPair fromto = ((MLockedBroadcast) message).getPayload();
					if (fromto.equals(this.p)) {
						this.sendTo(fromto.getFrom(), new MUnlockBroadcast(this.p));
					} else {
						this._broadcast((MLockedBroadcast) message);
					}
				}
				if (message instanceof MRegularBroadcast) {
					VisibilityMatrix.incrementFrom(this.p, (MRegularBroadcast) message);
					this.app.deliver(message.getPayload());
					this.broadcast((MRegularBroadcast) message);
				}
			}
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
		cb.buffers = (HashMap<Node, List<AMBroadcast>>) this.buffers.clone();
		return cb;
	}

}
