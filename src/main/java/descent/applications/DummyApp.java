package descent.applications;

import descent.causalbroadcast.IBroadcast;

/**
 * Dummy app doing nothing on message delivery and sending an empty message.
 */
public class DummyApp implements IApplication {

	IBroadcast broadcast;

	public DummyApp(IBroadcast broadcast) {
		this.broadcast = broadcast;
	}

	/**
	 * Send a message to other applications using its communication channel.
	 */
	public void send(Object message) {
		this.broadcast.send(null); // send nothing
	}

	/**
	 * Deliver the message to the application.
	 */
	public void deliver(Object message) {
		// nothing
	}

}
