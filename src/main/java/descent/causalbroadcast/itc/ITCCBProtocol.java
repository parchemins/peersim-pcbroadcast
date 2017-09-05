package descent.causalbroadcast.itc;

import java.util.List;

import descent.merging.MergingRegister;
import descent.rps.IPeerSampling;
import descent.spray.SprayPartialView;
import descent.tman.TMan;
import descent.tman.TManPartialView;
import itc.Id;
import peersim.core.Node;

/**
 * Overlay network where each peer has an interval tree clock. The overlay
 * network aims to put closer peers with "adjacent" identifiers to reduce the
 * size of the global binary tree.
 */
public class ITCCBProtocol extends TMan {

	public CausalityTracker ct;

	public ITCCBProtocol(String options) {
		super(options);
		this.ct = new CausalityTracker();
		this.descriptor = new ITCCBDescriptor();
	}

	public ITCCBProtocol() {
		super();
		this.ct = new CausalityTracker();
		this.descriptor = new ITCCBDescriptor();
	}

	@Override
	public void join(Node joiner, Node contact) {
		// #1 on join, get an Id to increment for causality tracking matters
		if (contact != null) {
			ITCCBProtocol im = (ITCCBProtocol) contact.getProtocol(ITCCBProtocol.pid);
			this.ct.borrow(im.ct.tracker);
			((ITCCBDescriptor) im.descriptor).setId(im.ct.tracker.getId());
			// (TODO) lease for a defined duration.
		}
		((ITCCBDescriptor) this.descriptor).setId(this.ct.tracker.getId());

		super.join(joiner, contact);
	}

	public void periodicCall() {
		// #1 check if should swap and stuff
		// ArrayList<Node> toExamine = new
		// ArrayList<Node>(this.partialViewTMan);
		// toExamine.addAll(this.partialView.getPeers());

		List<Node> toExamine = ((IPeerSampling) this.node.getProtocol(this.rps)).getPeers();

		Integer reduction = Integer.MIN_VALUE;
		Node nodeReduction = null;
		Id myIdReduction = null;
		Id imIdReduction = null;

		// process the best split. //(TODO)
		for (Node node : toExamine) {
			ITCCBProtocol im = (ITCCBProtocol) node.getProtocol(ITCCBProtocol.pid);
		}

		for (Node node : toExamine) {
			ITCCBProtocol im = (ITCCBProtocol) node.getProtocol(ITCCBProtocol.pid);
			// #A before
			// Integer sumOfNodes = this.ct.tracker.numberOfNodes() +
			// im.ct.tracker.numberOfNodes();
			Integer sumOfNodes = this.ct.tracker.getId().encode(null).getSizeBits()
					+ im.ct.tracker.getId().encode(null).getSizeBits();

			// #B save current config
			ITC4CB myCurrentStamp = this.ct.tracker.clone();
			ITC4CB imCurrentStamp = im.ct.tracker.clone();
			// #C fork remote id
			// #D merge with ours and set our new id

			Id toRemove = ITC4CB.getClosestBranch(this.ct.tracker.getId(), im.ct.tracker.getId()).id;
			// Id toRemove = ITC4CB.getDeepest(this.ct.tracker.getId());

			// System.out.println("BEFORE");
			// System.out.println(this.ct.tracker.getId());
			// System.out.println("TOREMOVE");
			// System.out.println(toRemove);

			this.ct.tracker.removeBranch(toRemove);
			// System.out.println("AFTER");
			// System.out.println(this.ct.tracker.getId());
			ITC4CB toMerge = new ITC4CB();
			toMerge.setId(toRemove);
			// toMerge.setId(this.ct.tracker.getId());
			// this.ct.tracker.setId(new Id(0));
			im.ct.tracker.join(toMerge);
			if (this.ct.tracker.getId().isLeaf() && !this.ct.tracker.getId().isSet()) {
				Id newId = im.ct.tracker.fork().getId();
				this.ct.tracker.setId(newId);
			}
			// #E after
			// Integer newSumOfNodes = this.ct.tracker.numberOfNodes() +
			// im.ct.tracker.numberOfNodes();
			Integer newSumOfNodes = this.ct.tracker.getId().encode(null).getSizeBits()
					+ im.ct.tracker.getId().encode(null).getSizeBits();

			if (reduction < sumOfNodes - newSumOfNodes) {
				nodeReduction = node;
				reduction = sumOfNodes - newSumOfNodes;
				myIdReduction = this.ct.tracker.getId().clone();
				imIdReduction = im.ct.tracker.getId().clone();
			}

			// #F recover and try with another node
			this.ct.tracker = myCurrentStamp;
			im.ct.tracker = imCurrentStamp;
		}

		if (nodeReduction != null && reduction > 0) {
			this.updateId(myIdReduction);
			ITCCBProtocol im = (ITCCBProtocol) nodeReduction.getProtocol(ITCCBProtocol.pid);
			im.updateId(imIdReduction);
		}

		super.periodicCall();
	};

	/**
	 * Borrow part of the remote's id of itc.
	 * 
	 * @param remote
	 *            The remote peer.
	 */
	public void borrow(ITCCBProtocol remote) {
		Id.sum(this.ct.tracker.getId(), remote.release());
		// ugly: double assign since Id.sum is ugly and set this
		this.updateId(this.ct.tracker.getId());
	}

	/**
	 * Get all remote's id.
	 * 
	 * @param remote
	 *            The remote peer.
	 */
	public void borrowAll(ITCCBProtocol remote) {
		Id.sum(this.ct.tracker.getId(), remote.releaseAll());
		// ugly: double assign since Id.sum is ugly and set this
		this.updateId(this.ct.tracker.getId());
	}

	/**
	 * Release part of our id of itc
	 * 
	 * @return The released part of the id.
	 */
	public Id release() {
		Id[] ids = this.ct.tracker.getId().split();
		this.updateId(ids[0]);
		return ids[1];
	}

	/**
	 * Release all our id. We should no longer be able to increment the itc.
	 * 
	 * @return The released Id.
	 */
	public Id releaseAll() {
		Id result = this.ct.tracker.getId();
		this.updateId(new Id(0).setAsLeaf());
		return result;
	}

	/**
	 * Update the identifier part of the interval tree clock held by this peer.
	 * 
	 * @param i
	 *            The new identifier.
	 */
	public void updateId(Id i) {
		// #1 update the causal broadcast mechanism
		this.ct.tracker.setId(i);
		// #2 update the descriptor
		((ITCCBDescriptor) this.descriptor).setId(this.ct.tracker.getId());
	}

	@Override
	public IPeerSampling clone() {
		ITCCBProtocol imClone = new ITCCBProtocol();
		try {
			imClone.partialViewTMan = (TManPartialView) this.partialViewTMan.clone();
			imClone.descriptor = new ITCCBDescriptor();
			((ITCCBDescriptor) imClone.descriptor).setId(((ITCCBDescriptor) this.descriptor).id);
			imClone.ct = (CausalityTracker) this.ct.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return imClone;
	}
}
