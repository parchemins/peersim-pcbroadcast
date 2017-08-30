package descent.causality;

/**
 * An application using network communications.
 */
public interface IApplication {
	/**
	 * Deliver the message to the application.
	 * 
	 * @param message
	 *            The delivered message.
	 */
	public void deliver(Object message);
}
