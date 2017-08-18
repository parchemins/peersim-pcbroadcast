package descent.intervalmerger;

import java.util.ArrayList;

import descent.causality.CausalityTracker;
import descent.causality.ITC4CB;
import descent.merging.MergingRegister;
import descent.rps.IPeerSampling;
import descent.spray.SprayPartialView;
import descent.tman.TMan;
import descent.tman.TManPartialView;
import itc.Id;
import itc.Stamp;
import peersim.core.Node;

/**
 * Overlay network where each peer has an interval tree clock. The overlay
 * network aims to put closer peers with "adjacent" identifiers to reduce the
 * size of the global binary tree.
 */
public class IntervalMerger extends TMan {

	public CausalityTracker ct;

	public IntervalMerger(String options) {
		super(options);
		this.ct = new CausalityTracker();
		this.descriptor = new IntervalMergerDescriptor();
	}

	public IntervalMerger() {
		super();
		this.ct = new CausalityTracker();
		this.descriptor = new IntervalMergerDescriptor();
	}

	@Override
	public void join(Node joiner, Node contact) {
		// #1 on join, get an Id to increment for causality tracking matters
		if (contact != null) {
			IntervalMerger im = (IntervalMerger) contact.getProtocol(IntervalMerger.pid);
			this.ct.borrow(im.ct.tracker);
			((IntervalMergerDescriptor) im.descriptor).setId(im.ct.tracker.getId());
			// (TODO) lease for a defined duration.
		}
		((IntervalMergerDescriptor) this.descriptor).setId(this.ct.tracker.getId());

		super.join(joiner, contact);
	}

	public void periodicCall() {
		// #1 check if should swap and stuff
		ArrayList<Node> toExamine = new ArrayList<Node>(this.partialViewTMan);
		//toExamine.addAll(this.partialView.getPeers());

		Integer reduction = Integer.MIN_VALUE;
		Node nodeReduction = null;
		Id myIdReduction = null;
		Id imIdReduction = null;

		for (Node node : toExamine) {
			IntervalMerger im = (IntervalMerger) node.getProtocol(IntervalMerger.pid);
			// #A before
			Integer sumOfNodes = this.ct.tracker.numberOfNodes() + im.ct.tracker.numberOfNodes();
			// #B save current config
			ITC4CB myCurrentStamp = this.ct.tracker.clone();
			ITC4CB imCurrentStamp = im.ct.tracker.clone();
			// #C fork remote id
			// (TODO) get the deepest branch
			Id newId = im.ct.tracker.fork().getId();
			// #D merge with ours and set our new id
			im.ct.tracker.join(this.ct.tracker);
			this.ct.tracker.setId(newId);
			// #E after
			Integer newSumOfNodes = this.ct.tracker.numberOfNodes() + im.ct.tracker.numberOfNodes();

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
			IntervalMerger im = (IntervalMerger) nodeReduction.getProtocol(IntervalMerger.pid);
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
	public void borrow(IntervalMerger remote) {
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
	public void borrowAll(IntervalMerger remote) {
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
		((IntervalMergerDescriptor) this.descriptor).setId(this.ct.tracker.getId());
	}

	@Override
	public IPeerSampling clone() {
		IntervalMerger imClone = new IntervalMerger();
		try {
			imClone.partialView = (SprayPartialView) this.partialView.clone();
			imClone.register = (MergingRegister) this.register.clone();
			imClone.partialViewTMan = (TManPartialView) this.partialViewTMan.clone();
			imClone.descriptor = new IntervalMergerDescriptor();
			((IntervalMergerDescriptor) imClone.descriptor).setId(((IntervalMergerDescriptor) this.descriptor).id);
			imClone.ct = (CausalityTracker) this.ct.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return imClone;
	}
}
