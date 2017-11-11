package descent.broadcast.reliable;

import java.util.HashMap;

/**
 * Version vector with exceptions to deal with out of FIFO order messages.
 */
public class VVwE {

	public HashMap<Long, VVwEEntry> received;

	public VVwE() {
		this.received = new HashMap<Long, VVwEEntry>();
	}

	/**
	 * Add the entry if it does not exist, or increment it.
	 * 
	 * @param id
	 *            The identifier of the peer.
	 * @param counter
	 *            The associated counter.
	 */
	public void add(Long id, Integer counter) {
		if (!this.received.containsKey(id)) {
			this.received.put(id, new VVwEEntry());
		}
		this.received.get(id).add(counter);
	}

	/**
	 * Check if the pair id,counter is already in the structure.
	 * 
	 * @param id
	 *            The identifier of the peer.
	 * @param counter
	 *            The associated counter.
	 * @return True if the id is already integrated to the structure, false
	 *         otherwise.
	 */
	public boolean contains(Long id, Integer counter) {
		return this.received.containsKey(id) && this.received.get(id).contains(counter);
	}

}
