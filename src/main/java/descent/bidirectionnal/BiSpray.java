package descent.bidirectionnal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

import descent.broadcast.causal.flood.FloodingCausalBroadcast;
import descent.rps.IMessage;
import descent.rps.IPeerSampling;
import descent.spray.Spray;
import descent.spray.SprayPartialView;
import peersim.config.Configuration;
import peersim.core.CommonState;
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

	private Node counterpart;

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
		// System.out.println("A");
		HashSet<Node> before = new HashSet<Node>();
		before.addAll(inview.uniqueSet());
		before.addAll(outview.uniqueSet());

		Node q = this.getOldest();
		this.counterpart = q;
		if (q == null) {
			// System.out.println("meow");
			return;
		}

		super._periodicCall(q);

		HashSet<Node> after = new HashSet<Node>();
		after.addAll(inview.uniqueSet());
		after.addAll(outview.uniqueSet());

		HashSet<Node> newNeighbors = new HashSet<Node>(after);
		newNeighbors.removeAll(before);
		// System.out.println("VVVVVVVVVVVVVVVVVVVVVVVV");
		for (Node n : newNeighbors) {
			// System.out.println("V" + this.counterpart.getID());
			this.opened(n);
		}
		// System.out.println("VVVVVVVVVVVVVVVVVVVVVVVV");

		HashSet<Node> removedNeighbors = new HashSet<Node>(before);
		removedNeighbors.removeAll(after);
		for (Node n : removedNeighbors) {
			this.closed(n);
		}

		this.counterpart = null;
	}

	@Override
	public IMessage onPeriodicCall(Node origin, IMessage message) {
		// System.out.println("B " + origin.getID());
		this.counterpart = origin;
		// System.out.println("this.counterpart =" + this.counterpart.getID());

		HashSet<Node> before = new HashSet<Node>();
		before.addAll(inview.uniqueSet());
		before.addAll(outview.uniqueSet());

		IMessage m = super.onPeriodicCall(origin, message);

		HashSet<Node> after = new HashSet<Node>();
		after.addAll(inview.uniqueSet());
		after.addAll(outview.uniqueSet());

		HashSet<Node> newNeighbors = new HashSet<Node>(after);
		newNeighbors.removeAll(before);
		// System.out.println("=====");
		for (Node n : newNeighbors) {
			// System.out.println("AAAAA " + this.counterpart.getID());
			this.opened(n);
		}
		// System.out.println("=====");

		HashSet<Node> removedNeighbors = new HashSet<Node>(before);
		removedNeighbors.removeAll(after);
		for (Node n : removedNeighbors) {
			this.closed(n);
		}

		this.counterpart = null;

		return m;
	}

	@Override
	public Node getOldest() {
		if (BiSpray.listener != -1) {

			FloodingCausalBroadcast fcb = (FloodingCausalBroadcast) this.node.getProtocol(FloodingCausalBroadcast.pid);

			Integer age = 0;
			ArrayList<Node> possibleResults = new ArrayList<Node>();

			for (Entry<Node, Integer> e : this.partialView.ages.entrySet()) {
				if (!fcb.buffers.containsKey(e.getKey())) { // we remove unsafe links
					if (age < e.getValue()) {
						age = e.getValue();
						possibleResults = new ArrayList<Node>();
					}
					if (age == e.getValue()) {
						possibleResults.add(e.getKey());
					}
				}
			}
			// System.out.println(this.partialView.ages.size());
			if (possibleResults.size() > 0) {
				return possibleResults.get((int) Math.floor(Math.random() * possibleResults.size()));
			} else {
				System.out.println("meow ? ");
				System.out.println("partial view " + this.partialView.size());
				return null;
			}
		} else {
			return super.getOldest();
		}
	}

	@Override
	public List<Node> getSample(Node caller, Node neighbor, boolean isInitiator) {
		if (BiSpray.listener != -1) {
			FloodingCausalBroadcast fcb = (FloodingCausalBroadcast) this.node.getProtocol(FloodingCausalBroadcast.pid);
			ArrayList<Node> sample = new ArrayList<Node>();
			ArrayList<Node> clone = new ArrayList<Node>();
			
			int nbSafe = 0;
			for (Node n : this.partialView.partialView.uniqueSet()) {
				if (!fcb.buffers.containsKey(n)) {
					++nbSafe;
				}
			}
			if (nbSafe <=1) {
				return sample;
			}


			for (Node n : this.partialView.partialView) {
				if (!fcb.buffers.containsKey(n)) {
					Integer occ = this.partialView.partialView.getCount(n);
					for (int i = 0; i < occ; ++i)
						clone.add(n);
				}
			}

			// #A if the caller in the initiator, it automatically adds itself
			int sampleSize = (int) Math.ceil(clone.size() / 2.0);
			if (isInitiator) { // called from the chosen peer
				clone.remove(clone.indexOf(neighbor));// replace an occurrence of the chosen neighbor
				sample.add(caller); // by the initiator identity
			}

			// #B create the sample from random peers inside the partial view
			while (sample.size() < sampleSize) {
				int rn = CommonState.r.nextInt(clone.size());
				sample.add(clone.get(rn));
				clone.remove(rn);
			}

			// #C since the partial view can contain multiple references to a
			// neighbor, including the chosen peer to exchange with, we replace
			// them with references of the caller
			sample = (ArrayList<Node>) SprayPartialView.replace(sample, neighbor, caller);

			return sample;
		} else {
			return super.getSample(caller, neighbor, isInitiator);
		}
	}

	public void opened(Node n) {
		if (CommonState.getTime() <= 60000) {// (XXX)
			return;
		}

		// System.out.println("mefzepofzeofze + " + this.counterpart.getID());

		if (BiSpray.listener != -1) {
			// if (this.counterpart == null) {
			// System.out.println("meowezgiozebg");
			// }

			((EDProtocol) this.node.getProtocol(BiSpray.listener)).processEvent(this.node, BiSpray.pid,
					new MOpen(n, this.counterpart));
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

		boolean isNewNeighbor = !(bs.outview.contains(peer)) && !(bs.inview.contains(peer));
		bs.inview.add(this.node);
		if (isNewNeighbor) {
			// System.out.println("A");
			if (this.counterpart != null && bs.node.getID() != this.counterpart.getID()) {
				bs.counterpart = this.counterpart;
				bs.opened(this.node);
				bs.counterpart = null;
			} else {
				bs.opened(this.node);
			}
		}

		return super.addNeighbor(peer);
	}

	@Override
	public boolean removeNeighbor(Node peer) {
		this.outview.remove(peer, 1);
		BiSpray bs = (BiSpray) peer.getProtocol(BiSpray.pid);

		bs.inview.remove(this.node, 1);
		boolean isRemovedNeighbor = !(bs.outview.contains(peer)) && !(bs.inview.contains(peer));
		if (isRemovedNeighbor) {
			bs.closed(this.node);
		}

		return super.removeNeighbor(peer);
	}

	@Override
	public IPeerSampling clone() {
		return new BiSpray();
	}

	@Override
	public Iterable<Node> getAliveNeighbors() {
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
