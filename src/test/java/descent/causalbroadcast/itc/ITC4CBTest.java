package descent.causalbroadcast.itc;

import org.junit.Test;

import descent.causalbroadcast.itc.ITC4CB;
import itc.Event;
import itc.Id;
import junit.framework.TestCase;

public class ITC4CBTest extends TestCase {

	/**
	 * 1 entry incremented.
	 */
	@Test
	public void testIncrement() {
		Id i = new Id(1);
		Event e = new Event();
		ITC4CB s = new ITC4CB(i, e);

		s.increment();
		assertTrue(s.getEvent().isLeaf());
		assertEquals(1, s.getEvent().getValue());
	}

	/**
	 * 2 entries one of which is incremented.
	 */
	@Test
	public void testIncrement2() {
		Id i = new Id(1);
		Event e = new Event();
		ITC4CB s = new ITC4CB(i, e);

		s.fork();

		assertFalse(s.getId().isLeaf());
		assertTrue(s.getId().getLeft().isSet() ^ s.getId().getRight().isSet());

		s.increment();

		assertFalse(s.getEvent().isLeaf());
		assertTrue(s.getEvent().getLeft().getValue() == 1 ^ s.getEvent().getRight().getValue() == 1);
	}

	/**
	 * 2 entries one of which is incremented. Then join entries and increment
	 * again.
	 */
	@Test
	public void testIncrementJoin() {
		Id i = new Id(1);
		Event e = new Event();
		ITC4CB s = new ITC4CB(i, e);
		ITC4CB s2 = new ITC4CB(s.fork());

		s.increment();
		Id.sum(s.getId(), s2.getId());

		s.increment();

		assertTrue(s.getId().isLeaf());
		assertTrue(s.getEvent().isLeaf());
		assertEquals(1, s.getEvent().getValue());
	}

	/**
	 * 2 entries one of which is incremented 2 times. Then join entries and
	 * increment twice.
	 */
	@Test
	public void testIncrement2Join() {
		Id i = new Id(1);
		Event e = new Event();
		ITC4CB s = new ITC4CB(i, e);
		ITC4CB s2 = new ITC4CB(s.fork());

		s.increment();
		s.increment();
		assertFalse(s.getEvent().isLeaf());
		assertTrue(s.getEvent().getLeft().getValue() == 2 ^ s.getEvent().getRight().getValue() == 2);

		Id.sum(s.getId(), s2.getId());

		s.increment();
		assertFalse(s.getEvent().isLeaf());
		assertEquals(1, s.getEvent().getValue());
		assertTrue((s.getEvent().getLeft().getValue() == 1 ^ s.getEvent().getRight().getValue() == 1)
				|| (s.getEvent().getLeft().getValue() == 0 ^ s.getEvent().getRight().getValue() == 0));
	}

	/**
	 * 1 entry incremented once, check if it is already delivered.
	 */
	@Test
	public void testIsDelivered() {
		Id i = new Id(1);
		Event e = new Event();
		ITC4CB s = new ITC4CB(i, e);
		ITC4CB s2 = new ITC4CB(s);

		ITC4CB inc = s.increment();

		assertFalse(s2.delivered(inc));
		assertTrue(s.delivered(inc));
	}

	/**
	 * 1 entry incremented once, check if it is ready to be delivered.
	 */
	@Test
	public void testIsReady1against0() {
		Id i = new Id(1);
		Event e = new Event();
		ITC4CB s = new ITC4CB(i, e);
		ITC4CB s2 = new ITC4CB(s);

		ITC4CB inc = s.increment();

		assertTrue(s2.isReady(inc));
		assertFalse(s.isReady(inc));
	}

	/**
	 * 1 entry incremented once. Then fork, then incremented. Check if it is
	 * ready to be delivered.
	 */
	@Test
	public void testIsReadyMoreComplicated() {
		Id i = new Id(1);
		Event e = new Event();
		ITC4CB s = new ITC4CB(i, e);
		ITC4CB inc = s.increment();

		ITC4CB s2 = new ITC4CB(s.fork());

		inc = s.increment();

		assertTrue(s2.isReady(inc));
		assertFalse(s.isReady(inc));
	}

	/**
	 * Increment from a 1-entry increment.
	 */
	@Test
	public void testIncrementFrom() {
		Id i = new Id(1);
		Event e = new Event();
		ITC4CB s = new ITC4CB(i, e);
		ITC4CB s2 = new ITC4CB(s);

		ITC4CB inc = s.increment();

		s2.incrementFrom(inc);

		assertTrue(s2.getEvent().isLeaf());
		assertEquals(1, s2.getEvent().getValue());
	}

	/**
	 * Increment from a 1-entry increment using a join.
	 */
	@Test
	public void testIncrementFromUsingJoin() {
		Id i = new Id(1);
		Event e = new Event();
		ITC4CB s = new ITC4CB(i, e);
		ITC4CB s2 = new ITC4CB(s);

		ITC4CB inc = s.increment();

		s2.join(new ITC4CB(new Id(0), inc.getEvent()));

		assertTrue(s2.getEvent().isLeaf());
		assertEquals(1, s2.getEvent().getValue());
	}

}
