package descent.broadcast.reliable;

import java.util.HashMap;
import java.util.Map;

/**
 * Vector clock associating id->counter.
 *
 */
public class VectorClock {

	private Map<Long, Integer> vector;

	public VectorClock() {
		this.vector = new HashMap<Long, Integer>();
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
		if (!this.vector.containsKey(id)) {
			this.vector.put(id, counter);
		} else {
			this.vector.put(id, Math.max(this.vector.get(id), counter));
		}
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
		return (this.vector.containsKey(id) && counter <= this.vector.get(id));
	}

	@Override
	public VectorClock clone(){
		VectorClock c = new VectorClock();
		for (Long id : this.vector.keySet()) {
			c.add(id, this.vector.get(id));
		}
		return c;
	}
}
