package descent.causality;

import itc.Event;
import itc.Id;
import itc.Stamp;

/**
 * Extended version of interval tree clocks that adds function designed for a
 * causal broadcast.
 */
public class ITC4CB extends Stamp {

	public ITC4CB() {
		super();
	}

	protected ITC4CB(Id i, Event e) {
		super(i, e);
	}

	protected ITC4CB(Stamp s) {
		super(s);
	}

	/**
	 * Increment the local stamp by 1.
	 * 
	 * @return A stamp comprising the event along with the identifier that shows
	 *         which entry this function increments.
	 */
	public ITC4CB increment() {
		// #1 process the entry to increment
		Id min = ITC4CB.findMin(this.getId(), this.getEvent());
		// #2 increment the entry
		Event e = ITC4CB._increment(min, this.getEvent());
		// #3 update the local event
		this.setEvent(e);
		// #4 return the stamp to send
		return new ITC4CB(min, e);
	}

	private static Event _increment(Id i, Event e) {
		// (TODO)
		return null;
	}

	/**
	 * Get the identifier targeting the minimal value in the event.
	 * 
	 * @return An Id containing a single entry to increment.
	 */
	private static Id _findMin(Id i, Event e) {
		// #1 findMin( 1, a ) :- 1
		if (i.isLeaf() && i.isSet() && e.isLeaf())
			return new Id(1);
		// #2 findMin( 1, (a, l, r) ) :-
		// ( findMin( 1, l ), 0 ) if min(l) < min(r)
		// ( 0, findMin( 1, r ) ) if min(l) ≥ min(r)
		if (i.isLeaf() && i.isSet() && !e.isLeaf())
			if (e.getLeft().getValue() < e.getRight().getValue()) {
				return (new Id()).setAsNode().setValue(0).setLeft(
						ITC4CB._findMin(i.getLeft(), Event.lift(e.getValue(), e.getLeft())).setRight(new Id(0)));
			} else {
				return (new Id()).setAsNode().setValue(0).setLeft(new Id(0))
						.setRight(ITC4CB._findMin(i.getRight(), Event.lift(e.getValue(), e.getRight())));
			}
		// #3 findMin( (0, ir), a ) :- (0, findMin( ir, a ))
		if (!i.isLeaf() && i.getLeft().isLeaf() && !i.getLeft().isSet() && e.isLeaf())
			return (new Id()).setAsNode().setValue(0).setLeft(new Id(0)).setRight(ITC4CB._findMin(i.getRight(), e));
		// #4 findMin( (0, ir), (a, l, r) ) :- ( 0, findMin(ir, r^a) )
		if (!i.isLeaf() && i.getLeft().isLeaf() && !i.getLeft().isSet() && !e.isLeaf())
			return (new Id()).setAsNode().setValue(0).setLeft(new Id(0))
					.setRight(ITC4CB._findMin(i.getRight(), Event.lift(e.getValue(), e.getRight())));
		// #5 findMin( (il, 0), a) :- ( findMin(il, a), 0 )
		if (!i.isLeaf() && i.getRight().isLeaf() && !i.getRight().isSet() && e.isLeaf())
			return (new Id()).setAsNode().setValue(0).setLeft(ITC4CB._findMin(i.getLeft(), e)).setRight(new Id(0));
		// #6 findMin( (il, 0), (a, l, r) ) :- ( findMin( il, l^a ), 0 )
		if (!i.isLeaf() && i.getRight().isLeaf() && !i.getRight().isSet() && !e.isLeaf())
			return (new Id().setAsNode().setValue(0)
					.setLeft(ITC4CB._findMin(i.getLeft(), Event.lift(e.getValue(), e.getLeft()))).setRight(new Id(0)));
		// (TODO) throw an exception
		System.out.println("_delivered unhandled case");
		return null;
	}

	/**
	 * Increment the local stamp using the other stamp.
	 * 
	 * @param o
	 *            The other stamp used to increment the local vector, it is
	 *            supposedly ready to be delivered.
	 */
	public void incrementFrom(Stamp o) {
		// (TODO)
	}

	/**
	 * Check if the other Stamp has been delivered.
	 * 
	 * @param o
	 *            The other stamp to check whether or not it has been delivered.
	 * @return True if the other stamp has been delivered, false otherwise.
	 */
	public boolean delivered(Stamp o) {
		return ITC4CB._delivered(o.getId(), this.getEvent(), o.getEvent());
	}

	private static boolean _delivered(Id i, Event e1, Event e2) {
		// #1 delivered( 0, e1, e2 ) :- true
		if (i.isLeaf() && !i.isSet())
			return true;
		// #2 delivered( 1, e1, e2 ) :- leq( e2, e1 )
		if (i.isLeaf() && i.isSet())
			return e2.leq(e1);
		// #3 delivered( (il, ir), (a, l1, r1), (b, l2, r2) ) :-
		// delivered(il, l1^a, l2^b) && delivered(ir, r1^a, r2^b)
		if (!i.isLeaf() && !e1.isLeaf() && !e2.isLeaf())
			return ITC4CB._delivered(i.getLeft(), Event.lift(e1.getValue(), e1.getLeft()),
					Event.lift(e2.getValue(), e2.getLeft()))
					&& ITC4CB._delivered(i.getRight(), Event.lift(e1.getValue(), e1.getRight()),
							Event.lift(e2.getValue(), e2.getRight()));
		// #4 delivered( (il, ir), (a, b) ) :- b ≤ a
		if (!i.isLeaf() && e1.isLeaf() && e2.isLeaf())
			return e2.getValue() <= e1.getValue();
		// #5 delivered( (il, ir), a, (b, l2, r2) ) :-
		// delivered( il, a, l2^b ) && delivered( ir, a, r2^b)
		if (!i.isLeaf() && e1.isLeaf() && !e2.isLeaf())
			return ITC4CB._delivered(i.getLeft(), e1, Event.lift(e2.getValue(), e2.getLeft()))
					&& ITC4CB._delivered(i.getRight(), e1, Event.lift(e2.getValue(), e2.getRight()));
		// #6 delivered( (il, ir), (a, l1, r1), b ) :-
		// delivered( il, l1^a, b ) && delivered( ir, r1^a, b )
		if (!i.isLeaf() && !e1.isLeaf() && e2.isLeaf())
			return ITC4CB._delivered(i.getLeft(), Event.lift(e1.getValue(), e1.getLeft()), e2)
					&& ITC4CB._delivered(i.getRight(), Event.lift(e1.getValue(), e1.getRight()), e2);
		// (TODO) throw an exception
		System.out.println("_delivered unhandled case");
		return false;
	}

	/**
	 * Check if the other Stamp is ready to be delivered.
	 * 
	 * @param other
	 *            The other stamp to check whether or not it has been delivered.
	 * @return True if the other stamp is ready to be delivered, false
	 *         otherwise.
	 */
	public boolean isReady(Stamp o) {
		return ITC4CB._isReady(o.getId(), this.getEvent(), o.getEvent());
	}

	private static boolean _isReady(Id i, Event e1, Event e2) {
		// #1 rdy( 0, e1, e2 ) :- leq(e2, e1)
		if (i.isLeaf() && !i.isSet())
			return e2.leq(e1);
		// #2 rdy( 1, a, b ) :- a = b-1
		if (i.isLeaf() && i.isSet() && e1.isLeaf() && e2.isLeaf())
			return e1.getValue() == e2.getValue() - 1;
		// #3 rdy( 1, (a, l1, r1), b) :- false
		if (i.isLeaf() && i.isSet() && !e1.isLeaf() && e2.isLeaf())
			return false;
		// #4 rdy( 1, a, (b, l2, r2) ) :- false
		if (i.isLeaf() && i.isSet() && e1.isLeaf() && !e2.isLeaf())
			return false;
		// #5 rdy( (il, ir), (a, l1, r1), (b, l2, r2) ) :-
		// rdy( il, l1^a, l2^b ) && rdy( ir, r1^a, r2^b )
		if (!i.isLeaf() && !e1.isLeaf() && !e2.isLeaf())
			return ITC4CB._isReady(i.getLeft(), Event.lift(e1.getValue(), e1.getLeft()),
					Event.lift(e2.getValue(), e2.getLeft()))
					&& ITC4CB._isReady(i.getRight(), Event.lift(e1.getValue(), e1.getRight()),
							Event.lift(e2.getValue(), e2.getRight()));
		// #6 rdy( (il, ir), a, (b, l2, r2) ) :-
		// rdy( il, a, l2^b ) && rdy( ir, a, r2^b )
		if (!i.isLeaf() && e1.isLeaf() && !e2.isLeaf())
			return ITC4CB._isReady(i.getLeft(), e1, Event.lift(e2.getValue(), e2.getLeft()))
					&& ITC4CB._isReady(i.getRight(), e1, Event.lift(e2.getValue(), e2.getRight()));
		// #7 rdy( (il, ir), (a, l1, r1), b ) :-
		// rdy( il, l1^a, b ) && rdy( ir, r1^a, b)
		if (!i.isLeaf() && !e1.isLeaf() && e2.isLeaf())
			return ITC4CB._isReady(i.getLeft(), Event.lift(e1.getValue(), e1.getLeft()), e2)
					&& ITC4CB._isReady(i.getRight(), Event.lift(e1.getValue(), e1.getRight()), e2);
		// #8 rdy( (il, ir), a, b ) :- rdy( il, a, b ) && rdy( ir, a, b )
		if (!i.isLeaf() && e1.isLeaf() && e2.isLeaf())
			return ITC4CB._isReady(i.getLeft(), e1, e2) && ITC4CB._isReady(i.getRight(), e1, e2);
		// (TODO) throw an exception
		System.out.println("_isReady unhandled case");
		return false;
	}

}
