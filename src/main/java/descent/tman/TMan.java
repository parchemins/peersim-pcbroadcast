package descent.tman;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.IteratorUtils;

import descent.rps.APeerSampling;
import descent.rps.IMessage;
import descent.rps.IPeerSampling;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;

/**
 * Structured overlay builder using a ranking function to converge to the
 * desired topology.
 */
public class TMan extends APeerSampling {

	// #A Configuration from peersim
	private final static String PAR_RPS = "rps";
	protected static int rps;
	private static final String PAR_PROTOCOL = "protocol";
	public static int pid;

	// #B Local variables
	public TManPartialView partialView;
	public IDescriptor descriptor;

	private boolean shuffleUsingRPS = false;

	public TMan(String prefix) {
		super(prefix);
		this.partialView = new TManPartialView();
		this.descriptor = Descriptor.get();

		TMan.pid = Configuration.getPid(prefix + "." + TMan.PAR_PROTOCOL);
		TMan.rps = Configuration.getPid(prefix + "." + TMan.PAR_RPS);
	}

	public TMan() {
		super();
		this.partialView = new TManPartialView();
		this.descriptor = Descriptor.get();
	}

	public void periodicCall() {
		if (!this.isUp)
			return;

		this.shuffleUsingRPS = !this.shuffleUsingRPS;

		// #1 Choose a neighbor to exchange with
		List<Node> randomNeighbors = IteratorUtils
				.toList(((IPeerSampling) this.node.getProtocol(TMan.rps)).getPeers().iterator());

		Node q = null;
		TMan qTMan = null;
		if (this.partialView.size() > 0 && !this.shuffleUsingRPS) {
			// #A from tman's partial view
			q = this.partialView.getRandom();
		} else if (randomNeighbors.size() > 0) {
			// #B from rps' partial view
			q = randomNeighbors.get(CommonState.r.nextInt(randomNeighbors.size()));

		}
		qTMan = (TMan) q.getProtocol(TMan.pid);
		if (!qTMan.isUp) {
			this.partialView.remove(q);
			return;
		}

		// #2 Prepare a sample
		List<Node> sample = this.partialView.getSample(this.node, q, randomNeighbors,
				Math.floor(randomNeighbors.size() / 2));
		TManMessage result = qTMan.onPeriodicCall(this.node, new TManMessage(sample));
		// #3 Integrate remote sample if it fits better
		this.partialView.merge(this, this.node, result.getPayload(), randomNeighbors.size());
	}

	/**
	 * React to an exchange initiated by a TMan protocol
	 * 
	 * @param origin
	 *            The peer that initiated the exchange
	 * @param message
	 *            The message containing descriptors of neighbors
	 * @return The response of the receiving peer to the origin
	 */
	public TManMessage onPeriodicCall(Node origin, IMessage message) {
		List<Node> randomNeighbors = IteratorUtils
				.toList(((IPeerSampling) this.node.getProtocol(TMan.rps)).getPeers().iterator());
		// #1 prepare a sample
		List<Node> sample = this.partialView.getSample(this.node, origin, randomNeighbors,
				Math.floor(randomNeighbors.size() / 2));
		// #2 merge the received sample
		this.partialView.merge(this, this.node, ((TManMessage) message).getPayload(), randomNeighbors.size());
		// #3 send the prepared sample to origin
		return new TManMessage(sample);
	}

	public void join(Node joiner, Node contact) {
		this.partialView.clear();

		if (this.node == null)
			this.node = joiner;

		if (contact != null) {
			this.addNeighbor(contact);
			TMan contactTMan = (TMan) contact.getProtocol(TMan.pid);
			contactTMan.onSubscription(this.node);
		}
		this.isUp = true;
	}

	/**
	 * When a newcomer arrives, it advertises it to the rest of the network
	 * 
	 * @param origin
	 *            The newcomer
	 */
	public void onSubscription(Node origin) {
		Iterable<Node> aliveNeighbors = this.getAliveNeighbors();
		if (aliveNeighbors.iterator().hasNext()) {
			List<Node> sample = new ArrayList<Node>();
			sample.add(origin);
			for (Node neighbor : aliveNeighbors) {
				TMan neighborTMan = (TMan) neighbor.getProtocol(TMan.pid);
				neighborTMan.addNeighbor(origin);
			}
		} else {
			this.addNeighbor(origin);
		}
	}

	public void leave() {
		this.isUp = false;
		this.partialView.clear();
	}

	@Override
	public IPeerSampling clone() {
		TMan tmanClone = new TMan();
		tmanClone.partialView = (TManPartialView) this.partialView.clone();
		tmanClone.descriptor = Descriptor.get(); // (TODO) change this
		return tmanClone;
	}

	/**
	 * May add a neighbor to the partial view of TMan
	 * 
	 * @param peer
	 *            Potential neighbor
	 * @return True if the peer is added, false otherwise
	 */
	public boolean addNeighbor(Node peer) {
		if (!this.node.equals(peer)) {
			List<Node> sample = new ArrayList<Node>();
			sample.add(peer);
			this.partialView.merge(this, this.node, sample,
					((TMan) this.node.getProtocol(TMan.rps)).partialView.size());
			return this.partialView.contains(peer);
		} else {
			return false;
		}
	}

	public Iterable<Node> getPeers(int k) {
		return this.partialView.getPeers(k);
	}

	public Iterable<Node> getPeers() {
		return this.partialView.getPeers();
	}

	@Override
	protected boolean pFail(List<Node> path) {
		return false;
	}

}
