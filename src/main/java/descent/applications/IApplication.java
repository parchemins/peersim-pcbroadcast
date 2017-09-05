package descent.applications;

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

	/**
	 * Send a message to the whole network other applications.
	 * 
	 * @param message
	 *            The message to send.
	 */
	public void send(Object message);
}
