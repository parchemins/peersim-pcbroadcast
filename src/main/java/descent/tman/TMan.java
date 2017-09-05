package descent.tman;

import java.util.ArrayList;
import java.util.List;

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
	protected int rps;

	// #B Local variables
	public TManPartialView partialViewTMan;
	public IDescriptor descriptor;

	private boolean shuffleUsingRPS = false;

	public TMan(String prefix) {
		super(prefix);
		this.partialViewTMan = new TManPartialView();
		this.descriptor = Descriptor.get();

		this.rps = Configuration.getPid(prefix + "." + TMan.PAR_RPS);
	}

	public TMan() {
		super();
		this.partialViewTMan = new TManPartialView();
		this.descriptor = Descriptor.get();
	}

	public void periodicCall() {
		if (!this.isUp)
			return;

		this.shuffleUsingRPS = !this.shuffleUsingRPS;

		// #1 Choose a neighbor to exchange with
		List<Node> randomNeighbors = ((IPeerSampling) this.node.getProtocol(this.rps)).getPeers();

		Node q = null;
		TMan qTMan = null;
		if (this.partialViewTMan.size() > 0 && !this.shuffleUsingRPS) {
			// #A from tman's partial view
			q = this.partialViewTMan.getRandom();
		} else if (randomNeighbors.size() > 0) {
			// #B from rps' partial view
			q = randomNeighbors.get(CommonState.r.nextInt(randomNeighbors.size()));

		}
		qTMan = (TMan) q.getProtocol(TMan.pid);
		if (!qTMan.isUp) {
			this.partialViewTMan.remove(q);
			return;
		}

		// #2 Prepare a sample
		List<Node> sample = this.partialViewTMan.getSample(this.node, q, randomNeighbors,
				Math.floor(randomNeighbors.size() / 2));
		IMessage result = qTMan.onPeriodicCall(this.node, new TManMessage(sample));
		// #3 Integrate remote sample if it fits better
		this.partialViewTMan.merge(this, this.node, (List<Node>) result.getPayload(), randomNeighbors.size());
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
	public IMessage onPeriodicCall(Node origin, IMessage message) {
		List<Node> randomNeighbors = ((IPeerSampling) this.node.getProtocol(this.rps)).getPeers();
		// #1 prepare a sample
		List<Node> sample = this.partialViewTMan.getSample(this.node, origin, randomNeighbors,
				Math.floor(randomNeighbors.size() / 2));
		// #2 merge the received sample
		this.partialViewTMan.merge(this, this.node, (List<Node>) message.getPayload(), randomNeighbors.size());
		// #3 send the prepared sample to origin
		return new TManMessage(sample);
	}

	public void join(Node joiner, Node contact) {
		this.partialViewTMan.clear();

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
		List<Node> aliveNeighbors = this.getAliveNeighbors();
		if (aliveNeighbors.size() > 0) {
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
		this.partialViewTMan.clear();
	}

	@Override
	public IPeerSampling clone() {
		TMan tmanClone = new TMan();
		tmanClone.partialViewTMan = (TManPartialView) this.partialViewTMan.clone();
		tmanClone.descriptor = Descriptor.get(); // (TODO) change this
		tmanClone.rps = this.rps;
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
			this.partialViewTMan.merge(this, this.node, sample,
					((IPeerSampling) this.node.getProtocol(this.rps)).getPeers().size());
			return this.partialViewTMan.contains(peer);
		} else {
			return false;
		}
	}

	public List<Node> getPeers(int k) {
		return this.partialViewTMan.getPeers(k);
	}

	public List<Node> getPeers() {
		return this.partialViewTMan.getPeers();
	}

	@Override
	protected boolean pFail(List<Node> path) {
		return false;
	}

}
