package descent.intervalmerger;

import descent.causality.CausalityTracker;
import descent.merging.MergingRegister;
import descent.rps.IPeerSampling;
import descent.spray.SprayPartialView;
import descent.tman.TMan;
import descent.tman.TManPartialView;
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
