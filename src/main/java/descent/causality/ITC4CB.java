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
		Id min = ITC4CB._findMin(this.getId(), this.getEvent());
		// #2 increment the entry
		Event e = ITC4CB._increment(min, this.getEvent());
		// #3 update the local event
		this.setEvent(e);
		// #4 return the stamp to send
		return new ITC4CB(min, e);
	}

	private static Event _increment(Id i, Event e) {
		// #1 inc( 1, a ) :- a + 1
		if (i.isLeaf() && i.isSet() && e.isLeaf())
			return new Event(e.getValue() + 1);
		// #2 inc( 0, a ) :- a
		if (i.isLeaf() && !i.isSet())
			return e.clone();
		// #3 inc ( (il, ir), (a, l, r) ) :- ( a, inc(il, l), inc(ir, r) )
		if (!i.isLeaf() && !e.isLeaf())
			return new Event(e.getValue()).setAsNode().setLeft(ITC4CB._increment(i.getLeft(), e.getLeft()))
					.setRight(ITC4CB._increment(i.getRight(), e.getRight())).normalize();
		// #4 inc ( (il, ir), a ) :- ( 0, inc(il, l), inc(ir, r) )
		if (!i.isLeaf() && e.isLeaf())
			return new Event(0).setAsNode().setLeft(ITC4CB._increment(i.getLeft(), e))
					.setRight(ITC4CB._increment(i.getRight(), e)).normalize();

		// (TODO) throw an exception
		System.out.println("_increment unhandled case");
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
				return (new Id()).setAsNode().setValue(0)
						.setLeft(ITC4CB._findMin(i, Event.lift(e.getValue(), e.getLeft())).setRight(new Id(0)));
			} else {
				return (new Id()).setAsNode().setValue(0).setLeft(new Id(0))
						.setRight(ITC4CB._findMin(i, Event.lift(e.getValue(), e.getRight())));
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
		System.out.println("_findMin unhandled case");
		return null;
	}

	/**
	 * Increment the local stamp using the other stamp.
	 * 
	 * @param o
	 *            The other stamp used to increment the local vector, it is
	 *            supposedly ready to be delivered.
	 */
	public void incrementFrom(ITC4CB o) {
		// Could do a this.join( new ITC4B( new Id(0), o.getEvent() ) )
		Event updatedEvent = ITC4CB._incrementFrom(o.getId(), this.getEvent());
		this.setEvent(updatedEvent);
	}

	private static Event _incrementFrom(Id i, Event e) {
		// #1 incf( 1, a ) :- a+1
		if (i.isLeaf() && i.isSet() && e.isLeaf())
			return new Event(e.getValue() + 1);
		// #2 incf( 1, (a, l, r) ) :- error (TODO) ?
		// #3 incf( 0, e ) :- e
		if (i.isLeaf() && !i.isSet())
			return e.clone();
		// #4 incf( (il, ir), a ) :- ( 0, incf(il, a), incf(ir, a) )
		if (!i.isLeaf() && e.isLeaf())
			return new Event(0).setAsNode().setLeft(ITC4CB._incrementFrom(i.getLeft(), e))
					.setRight(ITC4CB._incrementFrom(i.getRight(), e)).normalize();
		// #5 incf( (il, ir), (a, l, r) :- ( a, incf(il, l), incf(ir, r) )
		if (!i.isLeaf() && !e.isLeaf())
			return new Event(e.getValue()).setAsNode().setLeft(ITC4CB._incrementFrom(i.getLeft(), e.getLeft()))
					.setRight(ITC4CB._incrementFrom(i.getRight(), e.getRight())).normalize();
		System.out.println("_incrementFrom unhandled case");
		return null;
	}

	/**
	 * Check if the other Stamp has been delivered.
	 * 
	 * @param o
	 *            The other stamp to check whether or not it has been delivered.
	 * @return True if the other stamp has been delivered, false otherwise.
	 */
	public boolean delivered(ITC4CB o) {
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

	/**
	 * Get the smallest branch in the identifier.
	 * 
	 * @return An id which is the smallest branch of the identifier of the
	 *         stamp.
	 */
	public static Id getSmallest(Id id) {
		// System.out.println(id + " --> " + ITC4CB._getDeepest(id).id);
		return ITC4CB._getSmallest(id, new IdAndDepth(new Id(0), Integer.MAX_VALUE), 0).id;
	}

	private static IdAndDepth _getSmallest(Id id, IdAndDepth result, Integer current) {
		if (current >= result.depth)
			return result;
		if (id.isLeaf() && !id.isSet())
			return new IdAndDepth(new Id(0), Integer.MAX_VALUE);
		if (id.isLeaf() && id.isSet())
			return new IdAndDepth(new Id(1), 1);
		if (!id.isLeaf()) {
			IdAndDepth left = ITC4CB._getSmallest(id.getLeft(), result, current + 1);
			IdAndDepth right = ITC4CB._getSmallest(id.getRight(), left, current + 1);
			if (left.depth > right.depth) {
				return new IdAndDepth(new Id().setAsNode().setLeft(left.id).setRight(new Id(0)), left.depth + 1);
			} else {
				return new IdAndDepth(new Id().setAsNode().setLeft(new Id(0)).setRight(right.id), right.depth + 1);
			}
		}
		// (TODO) throw an exception
		System.out.println("_getSmallest unhandled case");
		return null;
	}

	/**
	 * Get the deepest branch in the identifier.
	 * 
	 * @return An id which is the deepest branch of the identifier of the stamp.
	 */
	public static Id getDeepest(Id id) {
		// System.out.println(id + " --> " + ITC4CB._getDeepest(id).id);
		return ITC4CB._getDeepest(id).id;
	}

	private static IdAndDepth _getDeepest(Id id) {
		// #1 deepest( 0 ) :- ( 0, 0 )
		if (id.isLeaf() && !id.isSet())
			return new IdAndDepth(new Id(0), 0);
		// #2 deepest( 1 ) :- ( 1, 1 )
		if (id.isLeaf() && id.isSet())
			return new IdAndDepth(new Id(1), 1);
		// #3 deepest( (l, r) ) :- ( (l, 0), dl+1 ) with (l, dl) = deepest(l)
		// and (r, dr) = deepest(r) if dl>dr;
		// ((0, r), dr+1) otherwise.
		if (!id.isLeaf()) {
			IdAndDepth left = ITC4CB._getDeepest(id.getLeft());
			IdAndDepth right = ITC4CB._getDeepest(id.getRight());
			if (left.depth > right.depth) {
				return new IdAndDepth(new Id().setAsNode().setLeft(left.id).setRight(new Id(0)), left.depth + 1);
			} else {
				return new IdAndDepth(new Id().setAsNode().setLeft(new Id(0)).setRight(right.id), right.depth + 1);
			}
		}
		// (TODO) throw an exception
		System.out.println("_getDeepest unhandled case");
		return null;
	}

	/**
	 * Get the distance between two branches of identifiers. The common root of
	 * branches counts as 0. The rest counts as 1.
	 * 
	 * @param i1
	 *            the first branch
	 * @param i2
	 *            the second branch
	 * @return An integer which is the distance from a node to the other in
	 *         number of nodes to travel.
	 */
	public static Integer distance(Id i1, Id i2) {
		// System.out.println("i1 " + i1);
		// System.out.println("i2 " + i2);
		return ITC4CB._commonRoot(i1, i2);
	}

	private static Integer _commonRoot(Id i1, Id i2) {
		// #1 cr( (0, r1), (0, r2) ) :- cr(r1, r2)
		if (i1.isNode() && i1.getLeft().isLeaf() && !i1.getLeft().isSet() && i2.isNode() && i2.getLeft().isLeaf()
				&& !i2.getLeft().isSet())
			return ITC4CB._commonRoot(i1.getRight(), i2.getRight());
		// #2 cr( (l1, 0), (l2, 0) ) :- cr(l1, l2)
		if (i1.isNode() && i1.getRight().isLeaf() && !i1.getRight().isSet() && i2.isNode() && i2.getRight().isLeaf()
				&& !i2.getRight().isSet())
			return ITC4CB._commonRoot(i1.getLeft(), i2.getLeft());
		// #3 default(i1, i2) :- depth(i1, i2)
		return ITC4CB._depth(i1) + ITC4CB._depth(i2);
	}

	public static Integer _depth(Id i) {
		// #1 depth(1) :- 1
		if (i.isLeaf() && i.isSet())
			return 1;
		// #2 depth( (0, r) ) :- 1 + depth(r)
		if (!i.isLeaf() && i.getLeft().isLeaf() && !i.getLeft().isSet())
			return 1 + ITC4CB._depth(i.getRight());
		// #3 depth( (l, 0) ) :- 1 + depth(l)
		if (!i.isLeaf() && i.getRight().isLeaf() && !i.getRight().isSet())
			return 1 + ITC4CB._depth(i.getLeft());
		// (TODO) throw an exception
		System.out.println("_getDepth unhandled case");
		return null;
	}

	/**
	 * Count the number of nodes of the id.
	 * 
	 * @return The number of nodes
	 */
	public Integer numberOfNodes() {
		return ITC4CB._numberOfNodes(this.getId());
	}

	public static Integer _numberOfNodes(Id i) {
		// #1 nodes( e ) :- 1
		if (i.isLeaf() && i.isSet()) {
			return 1;
		} else if (i.isLeaf() && !i.isSet()) {
			return 0;
		} else { // #2 nodes( (l, r) ) :- 1 + nodes(l) + nodes(r)
			return 1 + ITC4CB._numberOfNodes(i.getLeft()) + ITC4CB._numberOfNodes(i.getRight());
		}
	}

	/**
	 * Remove the branch from the Id.
	 * 
	 * @param branch
	 *            The branch to remove.
	 */
	public void removeBranch(Id branch) {
		this.setId(ITC4CB._removeBranch(this.getId(), branch));
	}

	private static Id _removeBranch(Id i, Id b) {
		if (i.isLeaf() && i.isSet() && b.isLeaf() && b.isSet()) {
			return new Id(0).setAsLeaf();
		} else if (i.isNode() && b.isNode() && b.getLeft().isLeaf() && !b.getLeft().isSet()) {
			return new Id().setAsNode().setLeft(i.getLeft()).setRight(ITC4CB._removeBranch(i.getRight(), b.getRight()))
					.normalize();
		} else if (i.isNode() && b.isNode() && b.getRight().isLeaf() && !b.getRight().isSet()) {
			return new Id().setAsNode().setLeft(ITC4CB._removeBranch(i.getLeft(), b.getLeft()).setRight(i.getRight()));
		}
		// (TODO) throw an exception
		System.out.println("_removeBranch unhandled case");
		return null;
	}

	@Override
	public ITC4CB clone() {
		return new ITC4CB(super.clone());
	}
}
