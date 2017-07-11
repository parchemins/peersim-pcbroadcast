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
	public Stamp increment() {
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
