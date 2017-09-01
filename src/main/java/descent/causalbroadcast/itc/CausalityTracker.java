package descent.causalbroadcast.itc;

import java.util.ArrayList;

import itc.Id;

/**
 * Causality tracker that delivers messages when they are ready, i.e., all its
 * causal dependencies have been delivered. Messages are delivered exactly once.
 */
public class CausalityTracker {

	/**
	 * The most up-to-date vector
	 */
	public ITC4CB tracker;

	/**
	 * Buffer of operations that are not yet ready to be delivered.
	 */
	public ArrayList<ITC4CB> buffer;

	public CausalityTracker() {
		this.tracker = new ITC4CB();
		this.buffer = new ArrayList<ITC4CB>();
	}

	/**
	 * Create a stamp using a remote peer's stamp.
	 * 
	 * @param remote
	 *            The remote peer's stamp.
	 */
	public CausalityTracker(ITC4CB remote) {
		this.tracker = new ITC4CB(remote.fork());
		this.buffer = new ArrayList<ITC4CB>();
	}

	/**
	 * Ask a remote peer for an Id.
	 * 
	 * @param remote
	 *            The remote peer's stamp.
	 * @return The identifier that has been leased.
	 */
	public Id borrow(ITC4CB remote) {
		ITC4CB temporary = new ITC4CB(remote.fork());
		this.tracker.setId(temporary.getId());
		return temporary.getId();
	}

	/**
	 * Get a stamp from the causality tracker.
	 * 
	 * @return A stamp encoding causal relationships regarding other events.
	 */
	public ITC4CB stamp() {
		// (TODO) throw if this protocol cannot stamp
		return this.tracker.increment();
	}

	/**
	 * Check if whether or not this causality tracking protocol can stamp
	 * operations.
	 * 
	 * @return True if it can stamp, false otherwise
	 */
	public boolean canStamp() {
		return !this.tracker.getId().isLeaf() || this.tracker.getId().isSet();
	}

	/**
	 * Receive a stamped message, should it be delivered or not.
	 * 
	 * @param message
	 *            The received message.
	 * @return Whether or not the message has been received for the first time.
	 * 
	 */
	public boolean receive(ITC4CB message) {
		if (this.alreadyExists(message)) {
			// #1 message has already been received
			return false;
		} else {
			// #2 message is added to the buffer and this later is checked for
			// messages that are ready.
			this.buffer.add(message);
			this.checkBuffer();
			return true;
		}
	}

	/**
	 * Check if a stamp already has been delivered or already exists in the
	 * buffer.
	 * 
	 * @param stamp
	 *            The stamp to check.
	 * @return Whether or not it already exists.
	 */
	private boolean alreadyExists(ITC4CB stamp) {
		return this.tracker.delivered(stamp) || this.buffer.contains(stamp);
	}

	/**
	 * Check if the stamp is ready to be delivered.
	 * 
	 * @param stamp
	 *            The stamp to check.
	 * @return Whether or not it is ready.
	 */
	private boolean isReady(ITC4CB stamp) {
		return this.tracker.isReady(stamp);
	}

	/**
	 * Check the buffer of message for messages that are ready to be delivered.
	 */
	private void checkBuffer() {
		boolean found = false;
		int i = this.buffer.size() - 1;
		while (!found && i >= 0) {
			if (this.isReady(this.buffer.get(i))) {
				found = true;
				this.deliver(this.buffer.get(i));
				this.buffer.remove(i);
			}
			--i;
		}
		if (found)
			checkBuffer();
	}

	/**
	 * Deliver the message. It increment the local causality tracking structure
	 * using the stamp of the message.
	 * 
	 * @param stamp
	 *            The stamp to increment from.
	 */
	private void deliver(ITC4CB stamp) {
		this.tracker.incrementFrom(stamp);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		CausalityTracker ctClone = new CausalityTracker();
		ctClone.buffer = (ArrayList<ITC4CB>) this.buffer.clone();
		ctClone.tracker = new ITC4CB(this.tracker.clone());
		return ctClone;
	}
}
