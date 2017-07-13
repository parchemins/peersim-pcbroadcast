package descent.intervalmerger;

import descent.causality.CausalityTracker;
import descent.merging.MergingRegister;
import descent.rps.IPeerSampling;
import descent.spray.SprayPartialView;
import descent.tman.Descriptor;
import descent.tman.TMan;
import descent.tman.TManPartialView;
import peersim.core.Node;

/**
 * Overlay network where each peer has an interval tree clock. The overlay
 * network aims to put closer peers with "adjacent" identifiers to reduce the
 * size of the global binary tree. (TODO) (TODO) (TODO) (TODO) (TODO) (TODO)
 * (TODO)
 */
public class IntervalMerger extends TMan {

	public CausalityTracker ct;

	public IntervalMerger(String options) {
		super(options);
		this.ct = new CausalityTracker();
	}

	public IntervalMerger() {
		super();
		this.ct = new CausalityTracker();
	}

	@Override
	public void join(Node joiner, Node contact) {
		super.join(joiner, contact);

		// #1 on join, get an Id to increment for causality tracking matters
		IntervalMerger im = (IntervalMerger) contact.getProtocol(IntervalMerger.pid);
		this.ct.lease(im.ct.tracker);
		// (TODO) lease for a defined duration.
	}

	@Override
	public IPeerSampling clone() {
		IntervalMerger imClone = new IntervalMerger();
		try {
			imClone.partialView = (SprayPartialView) this.partialView.clone();
			imClone.register = (MergingRegister) this.register.clone();
			imClone.partialViewTMan = (TManPartialView) this.partialViewTMan.clone();
			imClone.descriptor = Descriptor.get();
			imClone.ct = (CausalityTracker) this.ct.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return imClone;
	}
}
