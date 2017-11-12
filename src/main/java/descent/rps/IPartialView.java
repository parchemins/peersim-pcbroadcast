package descent.rps;

import java.util.List;

import peersim.core.Node;

/**
 * Interface exposing the functions available with a partial view. A partial
 * view is a structure that stores the neighborhood of a peer in a network.
 */
public interface IPartialView {

	/**
	 * Getter of the neighbors list
	 * 
	 * @return the reference to the list of neighbors
	 */
	public Iterable<Node> getPeers();

	/**
	 * Get a list of k neighbors. If the partial view is not big enough, it
	 * returns the partial view
	 * 
	 * @param k
	 *            the number of requested neighbors
	 * @return a new instance of the list of nodes
	 */
	public Iterable<Node> getPeers(int k);

	/**
	 * Remove an occurrence of the peer from the neighborhood, if multiple
	 * occurrences of the peer exist, it remove the oldest
	 * 
	 * @param peer
	 *            the peer to remove
	 * @return true if the peer has been removed, false otherwise
	 */
	public boolean removeNode(Node peer);

	/**
	 * Add the neighbor to the partial view
	 * 
	 * @param peer
	 *            the peer to add
	 * @return true if the peer has been added, false otherwise
	 */
	public boolean addNeighbor(Node peer);

	/**
	 * Check if the partial view contains a peer
	 * 
	 * @param peer
	 *            the peer to search
	 * @return true if the partial view contains the peer, false otherwise
	 */
	public boolean contains(Node peer);

	/**
	 * Getter of the size of the partial view
	 * 
	 * @return the size of the partial view
	 */
	public int size();

	/**
	 * Remove all the elements contained in the partial view
	 */
	public void clear();
}
