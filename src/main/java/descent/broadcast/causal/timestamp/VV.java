package descent.broadcast.causal.timestamp;

import java.util.HashMap;
import java.util.Map;

/**
 * Vector clock associating id->counter.
 *
 */
public class VV {

	private Map<Long, Integer> vector;

	public VV() {
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

	/**
	 * Check if the other VV is ready regarding this VV, i.e., 1 entry is
	 * incremented only.
	 * 
	 * @param other
	 *            The other VV to check.
	 * @return true if it is ready, false otherwise.
	 * @throws Exception
	 */
	public boolean isReady(VV other) throws Exception {
		Integer sum = 0;
		for (Long id : other.vector.keySet()) {
			Integer thisValue = 0;
			if (this.vector.containsKey(id))
				thisValue = this.vector.get(id);

			sum += Math.max(0, other.vector.get(id) - thisValue);

			if (sum > 1) {
				return false;
			}
		}
		if (sum == 0)
			throw new Exception("Should not happen");
		return true;
	}

	@Override
	public VV clone() {
		VV c = new VV();
		for (Long id : this.vector.keySet()) {
			c.add(id, this.vector.get(id));
		}
		return c;
	}

	/**
	 * Merge the other vector with this one.
	 * 
	 * @param other
	 *            The other vector.
	 */
	public void merge(VV other) {
		for (Long id : other.vector.keySet()) {
			this.add(id, other.vector.get(id));
		}
	}
}
