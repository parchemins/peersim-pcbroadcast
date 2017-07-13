package descent.causality;

import org.junit.Test;

import junit.framework.TestCase;

public class CausalityTrackerTest extends TestCase {

	/**
	 * Two operations arrives in inverted order to a CT, it awaits for the first
	 * operation to be delivered before delivering the second one.
	 */
	@Test
	public void test2Causal() {
		CausalityTracker ct1 = new CausalityTracker();
		CausalityTracker ct2 = new CausalityTracker(ct1.tracker);

		ITC4CB s1 = ct1.stamp();
		ITC4CB s2 = ct1.stamp();

		ct2.receive(s2);

		assertEquals(1, ct2.buffer.size());
		assertTrue(ct2.tracker.getEvent().isLeaf());
		assertEquals(0, ct2.tracker.getEvent().getValue());

		ct2.receive(s1);

		assertEquals(0, ct2.buffer.size());
		assertFalse(ct2.tracker.getEvent().isLeaf());
		assertTrue(
				ct2.tracker.getEvent().getLeft().getValue() == 2 ^ ct2.tracker.getEvent().getRight().getValue() == 2);
	}

	/**
	 * 1 operation, then two concurrent ones received in different orders.
	 */
	@Test
	public void test1CausalThen2Concurrent() {
		CausalityTracker ct1 = new CausalityTracker();
		CausalityTracker ct2 = new CausalityTracker(ct1.tracker);
		CausalityTracker ct3 = new CausalityTracker(ct1.tracker);

		ITC4CB s1 = ct1.stamp();

		ct2.receive(s1);
		ct3.receive(s1);

		ITC4CB sa = ct2.stamp();
		ITC4CB sb = ct3.stamp();

		// #1 1 -> a -> b
		CausalityTracker ct4 = new CausalityTracker();

		ct4.receive(s1);
		assertEquals(0, ct4.buffer.size());

		ct4.receive(sa);
		assertEquals(0, ct4.buffer.size());

		ct4.receive(sb);
		assertEquals(0, ct4.buffer.size());

		assertTrue(ct4.tracker.getEvent().isLeaf());
		assertEquals(1, ct4.tracker.getEvent().getValue());

		// #2 1 -> b -> a
		CausalityTracker ct5 = new CausalityTracker();

		ct5.receive(s1);
		assertEquals(0, ct5.buffer.size());

		ct5.receive(sb);
		assertEquals(0, ct5.buffer.size());

		ct5.receive(sa);
		assertEquals(0, ct5.buffer.size());

		assertTrue(ct5.tracker.getEvent().isLeaf());
		assertEquals(1, ct5.tracker.getEvent().getValue());

		// #3 a -> b -> 1
		CausalityTracker ct6 = new CausalityTracker();

		ct6.receive(sa);
		assertEquals(1, ct6.buffer.size());

		ct6.receive(sb);
		assertEquals(2, ct6.buffer.size());

		ct6.receive(s1);
		assertEquals(0, ct6.buffer.size());

		assertTrue(ct6.tracker.getEvent().isLeaf());
		assertEquals(1, ct6.tracker.getEvent().getValue());
	}

	/**
	 * Check that it should re-emit the message only once.
	 */
	@Test
	public void testForwardOnce() {
		CausalityTracker ct1 = new CausalityTracker();
		CausalityTracker ct2 = new CausalityTracker(ct1.tracker);

		ITC4CB s = ct1.stamp();

		boolean shouldIForward = ct2.receive(s);
		assertTrue(shouldIForward);

		boolean shouldIKeepForwarding = ct2.receive(s);
		assertFalse(shouldIKeepForwarding);
	}
}
