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
	 * @param other
	 *            The other stamp used to increment the local vector, it is
	 *            supposedly ready to be delivered.
	 */
	public void incrementFrom(Stamp other) {

	}

	/**
	 * Check if the other Stamp has been delivered.
	 * 
	 * @param other
	 *            The other stamp to check whether or not it has been delivered.
	 * @return True if the other stamp has been delivered, false otherwise.
	 */
	public boolean delivered(Stamp other) {
		return true;
	}

	/**
	 * Check if the other Stamp is ready to be delivered.
	 * 
	 * @param other
	 *            The other stamp to check whether or not it has been delivered.
	 * @return True if the other stamp is ready to be delivered, false
	 *         otherwise.
	 */
	public boolean isReady(Stamp other) {
		return false;
	}

}
