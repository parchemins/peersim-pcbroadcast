package descent.broadcast.reliable;

import java.util.HashSet;

/**
 * Entry of the vector clock with exception for reliable broadcast where message
 * can arrive out of FIFO order.
 */
public class VVwEEntry {

	public Integer max;
	public HashSet<Integer> exceptions;

	public VVwEEntry() {
		this.max = 0;
		this.exceptions = new HashSet<Integer>();
	}

	/**
	 * Add the value to the vector with exceptions.
	 * 
	 * @param value
	 *            The value to include in this structure.
	 */
	public void add(Integer value) {
		if (value < this.max) { // #1 in exceptions
			this.exceptions.remove(value);
		} else {
			for (Integer i = this.max + 1; i < value; ++i) { // #2 above the max, create exceptions
				this.exceptions.add(i);
			}
			this.max = value;
		}

	}

	/**
	 * Checks if the structure contains the value.
	 * 
	 * @param value
	 *            The value to check.
	 * @return true if it is inside, false otherwise.
	 */
	public boolean contains(Integer value) {
		return (value <= this.max && !this.exceptions.contains(value));
	}

}
