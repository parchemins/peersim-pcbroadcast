package descent.bidirectionnal;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

import descent.rps.IMessage;
import descent.rps.IPeerSampling;
import descent.spray.Spray;
import peersim.config.Configuration;
import peersim.core.Node;
import peersim.edsim.EDProtocol;

/**
 * Spray with bidirectional communication links.
 */
public class BiSpray extends Spray {

	public Bag<Node> inview;
	public Bag<Node> outview;

	private static final String PAR_LISTENER = "listener";
	private static int listener;

	public BiSpray(String prefix) {
		super(prefix);

		BiSpray.listener = Configuration.getPid(prefix + "." + BiSpray.PAR_LISTENER, -1);

		this.inview = new HashBag<Node>();
		this.outview = new HashBag<Node>();
	}

	public BiSpray() {
		super();
		this.inview = new HashBag<Node>();
		this.outview = new HashBag<Node>();
	}

	@Override
	public void periodicCall() {
		HashSet<Node> before = new HashSet<Node>();
		before.addAll(inview.uniqueSet());
		before.addAll(outview.uniqueSet());

		super.periodicCall();

		HashSet<Node> after = new HashSet<Node>();
		after.addAll(inview.uniqueSet());
		after.addAll(outview.uniqueSet());

		HashSet<Node> newNeighbors = new HashSet<Node>(after);
		newNeighbors.removeAll(before);
		for (Node n : newNeighbors) {
			this.opened(n);
		}

		HashSet<Node> removedNeighbors = new HashSet<Node>(before);
		removedNeighbors.removeAll(after);
		for (Node n : removedNeighbors) {
			this.closed(n);
		}
	}

	@Override
	public IMessage onPeriodicCall(Node origin, IMessage message) {
		HashSet<Node> before = new HashSet<Node>();
		before.addAll(inview.uniqueSet());
		before.addAll(outview.uniqueSet());

		IMessage m = super.onPeriodicCall(origin, message);

		HashSet<Node> after = new HashSet<Node>();
		after.addAll(inview.uniqueSet());
		after.addAll(outview.uniqueSet());

		HashSet<Node> newNeighbors = new HashSet<Node>(after);
		newNeighbors.removeAll(before);
		for (Node n : newNeighbors) {
			this.opened(n);
		}

		HashSet<Node> removedNeighbors = new HashSet<Node>(before);
		removedNeighbors.removeAll(after);
		for (Node n : removedNeighbors) {
			this.closed(n);
		}

		return m;
	}

	public void opened(Node n) {
		if (BiSpray.listener != -1) {
			((EDProtocol) this.node.getProtocol(BiSpray.listener)).processEvent(this.node, BiSpray.pid, new MOpen(n));
		}
	}

	public void closed(Node n) {
		if (BiSpray.listener != -1) {
			((EDProtocol) this.node.getProtocol(BiSpray.listener)).processEvent(this.node, BiSpray.pid, new MClose(n));
		}
	}

	@Override
	public boolean addNeighbor(Node peer) {
		this.outview.add(peer);
		BiSpray bs = (BiSpray) peer.getProtocol(BiSpray.pid);
		bs.inview.add(this.node);
		return super.addNeighbor(peer);
	}

	@Override
	public boolean removeNeighbor(Node peer) {
		this.outview.remove(peer, 1);
		BiSpray bs = (BiSpray) peer.getProtocol(BiSpray.pid);
		bs.inview.remove(this.node, 1);
		return super.removeNeighbor(peer);
	}

	@Override
	public IPeerSampling clone() {
		return new BiSpray();
	}

	@Override
	public Iterable<Node> getAliveNeighbors() {
		// System.out.println("inview " + this.inview.uniqueSet().size());
		// System.out.println("outview " + this.outview.uniqueSet().size());
		ArrayList<Node> neighbors = new ArrayList<Node>();
		HashSet<Node> view = new HashSet<Node>(this.outview.uniqueSet());
		view.addAll(this.inview.uniqueSet());
		for (Node n : view) {
			if (n.isUp()) {
				neighbors.add(n);
			}
		}
		return neighbors;
	}

}
