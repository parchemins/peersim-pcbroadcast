package descent.spray;

import java.util.ArrayList;
import java.util.List;

import descent.merging.MergingRegister;
import descent.rps.APeerSampling;
import descent.rps.IMessage;
import descent.rps.IPeerSampling;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;

/**
 * Random peer-sampling protocol that self-adapts its functioning to the size of
 * the network using local knowledge only.
 */
public class Spray extends APeerSampling {

	// #A Configuration from peersim
	// In average, the number of arcs should be ~ a*ln(N)+b
	private static final String PAR_A = "a";
	protected Double A = 1.;

	private static final String PAR_B = "b";
	protected Double B = 0.;

	private static final String PAR_PID = "pid";
	public static int pid;

	// #B Local variables
	public SprayPartialView partialView;

	public MergingRegister register;

	public Spray(String prefix) {
		super(prefix);
		this.partialView = new SprayPartialView();
		this.register = new MergingRegister();

		Spray.pid = Configuration.getPid(prefix + "." + Spray.PAR_PID);
		this.A = Configuration.getDouble(prefix + "." + Spray.PAR_A, 1.);
		this.B = Configuration.getDouble(prefix + "." + Spray.PAR_B, 0.);
	}

	public Spray() {
		super();
		this.partialView = new SprayPartialView();
		this.register = new MergingRegister();
	}

	@Override
	protected boolean pFail(List<Node> path) {
		// The probability is constant since the number of hops to establish
		// a connection is constant: p1 -> bridge -> p2 -> bridge -> p1
		double pf = 1 - Math.pow(1 - APeerSampling.fail, 6);
		return CommonState.r.nextDouble() < pf;
	}

	public void periodicCall() {
		this._periodicCall();
	}

	public Node _periodicCall() {
		// #0 stop if the peer is down
		if (!this.isUp || this.partialView.size() <= 0) {
			return null;
		}

		// #1 Choose the peer to exchange with
		this.partialView.incrementAge();
		Node q = this.partialView.getOldest();
		Spray qSpray = (Spray) q.getProtocol(Spray.pid);
		// #A Peer is down: departed or left
		if (!qSpray.isUp) {
			this.onPeerDown(q);
			return null;
		}
		// #B Arc did not properly established
		boolean isFailedConnection = this.pFail(null);
		if (isFailedConnection) {
			this.onArcDown(q);
			return null;
		}

		// #2 Create a sample
		List<Node> sample = this.partialView.getSample(this.node, q, true);
		IMessage received = qSpray.onPeriodicCall(this.node, new SprayMessage(sample));

		// #3 Merge the received sample with current partial view
		List<Node> samplePrime = (List<Node>) received.getPayload();
		this.mergeSample(this.node, q, samplePrime, sample, true);

		return q;
	}

	public IMessage onPeriodicCall(Node origin, IMessage message) {
		// #0 Process the sample to send back
		List<Node> samplePrime = this.partialView.getSample(this.node, origin, false);
		// #1 Merge the received sample with our own partial view and exclude
		this.mergeSample(this.node, origin, (List<Node>) message.getPayload(), samplePrime, false);
		// #2 Prepare the result to send back
		return new SprayMessage(samplePrime);
	}

	public void join(Node joiner, Node contact) {
		// #0 Make sure the partial view starts empty
		this.partialView.clear();
		// #1 Lazy loading of the node identity
		if (this.node == null) {
			this.node = joiner;
		}
		// #2 Check if it is the very first peer
		if (contact != null) {
			// #A Inject A arcs to expect A*ln(N)+B arcs
			this.inject(this.A, 0., contact);
			// #B Inform the contact peer
			Spray contactSpray = (Spray) contact.getProtocol(Spray.pid);
			contactSpray.onSubscription(this.node);
		}
		this.isUp = true;
	}

	public void onSubscription(Node origin) {
		// #0 Check dead neighbors
		for (Node deadNeighbor : this._getDeadNeighbors()) {
			this.onPeerDown(deadNeighbor);
		}
		// #1 Forward subscription to neighbors
		Iterable<Node> aliveNeighbors = this._getAliveNeighbors();
		if (aliveNeighbors.iterator().hasNext()) {
			// #A If the contact peer has neighbors
			for (Node neighbor : aliveNeighbors) {
				Spray neighborSpray = (Spray) neighbor.getProtocol(Spray.pid);
				neighborSpray.inject(1., 0., origin);
			}
		} else {
			// #B Otherwise it keeps this neighbor: 2-peers network
			// #2 Inject A + B arcs to expect A*ln(N)+B arcs
			this.inject(this.A, this.B, origin);
		}
	}

	/**
	 * private version of getalive neighbors
	 * 
	 * @return
	 */
	private Iterable<Node> _getAliveNeighbors() {
		return super.getAliveNeighbors();
	}

	/**
	 * private version of getdead neighbors
	 * 
	 * @return
	 */
	private Iterable<Node> _getDeadNeighbors() {
		return super.getDeadNeighbors();
	}

	/**
	 * Inject A+B number of arcs.
	 * 
	 * @param A
	 *            A*ln(N)
	 * @param B
	 *            +B
	 */
	protected void inject(Double A, Double B, Node neighbor) {
		Double a = A;
		for (Integer i = 0; i < Math.floor(A); ++i) {
			this.partialView.addNeighbor(neighbor);
			a -= 1;
		}
		if (CommonState.r.nextDouble() < a) {
			this.partialView.addNeighbor(neighbor);
		}
		Double b = B;
		for (Integer i = 0; i < Math.floor(B); ++i) {
			this.partialView.addNeighbor(neighbor);
			b -= 1;
		}
		if (CommonState.r.nextDouble() < b) {
			this.partialView.addNeighbor(neighbor);
		}
	}

	public void leave() {
		this.isUp = false;
		this.partialView.clear();
	}

	@Override
	public IPeerSampling clone() {
		Spray sprayClone = new Spray();
		sprayClone.A = this.A;
		sprayClone.B = this.B;
		return sprayClone;
	}

	@Override
	public boolean addNeighbor(Node peer) {
		return this.partialView.addNeighbor(peer);
	}

	public boolean removeNeighbor(Node peer) {
		return this.partialView.removeNeighbor(peer);
	}

	/**
	 * Procedure that handles the peer failures when detected
	 * 
	 * @param q
	 *            the peer supposedly crashed or departed
	 */
	private void onPeerDown(Node q) {
		// #1 Probability to *not* recreate the connection: A/ln(N) + B/N
		double pRemove = this.A / this.partialView.size() + this.B / Math.exp(this.partialView.size());
		// #2 Remove all occurrences of q in our partial view and count them
		int occ = this.partialView.removeAll(q); // (XXX) may need to go through
													// this.removeNeighbor
		if (this.partialView.size() > 0) {
			// #3 probabilistically double known connections
			for (int i = 0; i < occ; ++i) {
				if (CommonState.r.nextDouble() > pRemove) {
					this.addNeighbor(this.partialView.getLowestOcc());
				}
			}
		}
	}

	/**
	 * Replace an failed arc by an existing one
	 * 
	 * @param q
	 *            the destination of the arc to replace
	 */
	private void onArcDown(Node q) {
		// #1 Remove the unestablished link
		this.removeNeighbor(q);
		// #2 Double a known connection at random
		if (this.partialView.size() > 0) {
			Node toDouble = this.partialView.getLowestOcc();
			this.addNeighbor(toDouble);
		}
	}

	public Iterable<Node> getPeers(int k) {
		return this.partialView.getPeers(k);
	}

	public Iterable<Node> getPeers() {
		return this.partialView.getPeers();
	}

	public void mergeSample(Node caller, Node neighbor, List<Node> newSample, List<Node> oldSample,
			boolean isInitiator) {

		SprayPartialView spv = (SprayPartialView) this.partialView.clone();

		// opposite transformation of the getSample
		ArrayList<Node> oldSampleInitial = (ArrayList<Node>) SprayPartialView.replace(oldSample, caller, neighbor);
		for (Node toRemoveNeighbor : oldSampleInitial) {
			spv.removeNeighbor(toRemoveNeighbor);
		}

		// #B add the received sample
		for (Node toAddNeighbor : newSample) {
			boolean found = false;
			int i = 0;
			if (spv.contains(toAddNeighbor)) {
				// #1 search for a removed peer that is not duplicate
				while (!found && oldSampleInitial.size() > i) {
					if (!spv.contains(oldSampleInitial.get(i))) {
						found = true;
					} else {
						++i;
					}
				}
			}
			if (!found) {
				spv.addNeighbor(toAddNeighbor);
			} else {
				spv.addNeighbor(oldSampleInitial.get(i));
			}
		}

		for (Node n : spv.getPeers()) {
			if (!this.partialView.contains(n)) {
				this.addNeighbor(n);
			}
		}

		for (Node n : this.partialView.getPeers()) {
			if (!spv.contains(n)) {
				this.removeNeighbor(n);
			}
		}
	}
}
