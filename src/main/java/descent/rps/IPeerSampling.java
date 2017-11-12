package descent.rps;

import peersim.core.Node;

/**
 * Basic functions composing the random peer sampling protocol.
 */
public interface IPeerSampling {

	/**
	 * Function called every delta time. It generally corresponds to a protocol
	 * aiming to renew connections to handle churn. Often, a neighbor is chosen to
	 * perform the operation.
	 */
	void periodicCall();

	/**
	 * The event trigger when the neighbor received a message from the periodic call
	 * of a peer (cf periodicCall function).
	 *
	 * @param origin
	 *            the peer which initiates the periodic protocol
	 * @param message
	 *            the message to send to the random neighbor
	 * @return return a message
	 */
	IMessage onPeriodicCall(Node origin, IMessage message);

	/**
	 * Join the network using the contact in argument.
	 *
	 * @param joiner
	 *            the peer that joins the network
	 * @param contact
	 *            the peer that will introduce caller to the network
	 */
	void join(Node joiner, Node contact);

	/**
	 * The event called when a peer join the network using us as contact peer.
	 *
	 * @param origin
	 *            the subscriber
	 */
	void onSubscription(Node origin);

	/**
	 * Leave the network. Either does nothing, or may help the network to recover
	 */
	void leave();

	/**
	 * Getter of the neighbors, it includes dead links too
	 *
	 * @param k
	 *            the number of requested neighbors
	 * @return a list of neighbors of size k, or size of the neighborhood if k is
	 *         too large
	 */
	Iterable<Node> getPeers(int k);

	/**
	 * Getter of all the neighbors. Includes dead links.
	 * 
	 * @return the list of all neighbors.
	 */
	Iterable<Node> getPeers();

	/**
	 * Getter of the neighbors, does not include peers that are dead
	 *
	 * @return a list of nodes
	 */
	Iterable<Node> getAliveNeighbors();

	/**
	 * Clone
	 *
	 * @return a clone of the instance calling it
	 */
	IPeerSampling clone();

}
