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
	public void broadcast(IMessage message);

	/**
	 * Received a broadcasted message.
	 * 
	 * @param message
	 *            The received message.
	 */
	public void receive(IMessage message);
}
