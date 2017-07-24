package descent.intervalmerger;

import descent.causality.ITC4CB;
import descent.tman.IDescriptor;
import itc.Id;

/**
 * Descriptor of a peer which holds an interval tree clock. Its main component
 * is the identifier contained in the stamp that it uses to increment its
 * vector.
 */
public class IntervalMergerDescriptor implements IDescriptor {

	public Id id;

	public IntervalMergerDescriptor() {
		this.id = new Id(0);
	}

	public void setId(Id id) {
		this.id = id;
	}

	public double ranking(IDescriptor other) {
		IntervalMergerDescriptor o = (IntervalMergerDescriptor) other;
		// #1 get the deepest branch of the identifiers
		Id thisDeepestBranch = ITC4CB.getDeepest(this.id);
		Id otherDeepestBranch = ITC4CB.getDeepest(o.id);
		// #2 get the distance between branches (TODO)
		
		return 0;
	}

}
