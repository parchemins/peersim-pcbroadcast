package descent.causalbroadcast;

import descent.rps.IMessage;

/**
 * Interface of mechanisms that send a message from one peer to all other peers
 * in the network.
 */
public interface IBroadcast {

	/**
	 * Broadcast a message to the whole network.
	 * 
	 * @param message
	 *            The message to broadcast.
	 */
	public void send(IMessage message);
}
