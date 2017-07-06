package descent.causality;

import java.util.ArrayList;

import itc.Id;
import itc.Stamp;

/**
 * Causality tracker that delivers messages when they are ready, i.e., all its
 * causal dependencies have been delivered. Messages are delivered exactly once.
 */
public class CausalityTracker {

	/**
	 * The most up-to-date vector
	 */
	public Stamp tracker;

	/**
	 * Buffer of operations that are not yet ready to be delivered.
	 */
	public ArrayList<Stamp> buffer;

	public CausalityTracker() {
		this.tracker = new Stamp();
		this.buffer = new ArrayList<Stamp>();
	}

	/**
	 * Create a stamp using a remote peer's stamp.
	 * 
	 * @param remote
	 *            The remote peer's stamp.
	 */
	public CausalityTracker(Stamp remote) {
		this.tracker = remote.fork();
		this.buffer = new ArrayList<Stamp>();
	}

	/**
	 * Ask a remote peer for an Id.
	 * 
	 * @param remote
	 *            The remote peer's stamp.
	 * @return The identifier that has been leased.
	 */
	public Id lease(Stamp remote) {
		Stamp temporary = remote.fork();
		this.tracker.setId(temporary.getId());
		return temporary.getId();
	}

	/**
	 * Receive a stamped message, should it be delivered or not.
	 * 
	 * @param message
	 *            The received message.
	 * @return Whether or not the message has been received for the first time.
	 * 
	 */
	public boolean receive(Stamp message) {
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
	private boolean alreadyExists(Stamp stamp) {
		return stamp.leq(this.tracker) || this.buffer.contains(stamp);
	}

	/**
	 * Check if the stamp is ready to be delivered.
	 * 
	 * @param stamp
	 *            The stamp to check.
	 * @return Whether or not it is ready.
	 */
	private boolean isReady(Stamp stamp) {
		// (TODO)
		return false;
	}

	/**
	 * Check the buffer of message for messages that are ready to be delivered.
	 */
	private void checkBuffer() {
		// (TODO)
	}
}
