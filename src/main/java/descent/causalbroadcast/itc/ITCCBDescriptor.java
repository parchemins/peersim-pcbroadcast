package descent.causalbroadcast.itc;

import java.util.List;

import descent.tman.IDescriptor;
import itc.Id;

/**
 * Descriptor of a peer which holds an interval tree clock. Its main component
 * is the identifier contained in the stamp that it uses to increment its
 * vector.
 */
public class ITCCBDescriptor implements IDescriptor {

	public Id id;

	public ITCCBDescriptor() {
		this.id = new Id(0);
	}

	public void setId(Id id) {
		this.id = id;
	}

	public double ranking(IDescriptor other) {
		ITCCBDescriptor o = (ITCCBDescriptor) other;
		return ITC4CB.getClosestBranch(this.id, o.id).depth;
	}

	public double ranking2(IDescriptor other) {
		ITCCBDescriptor o = (ITCCBDescriptor) other;
		// #1 get the deepest branch of the identifiers
		Id thisDeepestBranch = ITC4CB.getDeepest(this.id);
		Id otherDeepestBranch = ITC4CB.getDeepest(o.id);
		// #2 get the distance between branches
		return ITC4CB.distance(thisDeepestBranch, otherDeepestBranch);
	}

}
