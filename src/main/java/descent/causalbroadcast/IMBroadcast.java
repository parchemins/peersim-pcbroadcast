package descent.causalbroadcast;

import peersim.core.Node;

/**
 * Interface of message. For now, it contains a unique identifier composed of
 * the node that created the original message, and a counter which is the number
 * of message previously created by this node + 1.
 *
 */
public interface IMBroadcast {

	/**
	 * @return The node that created the original message.
	 */
	public Node getOrigin();

	/**
	 * @return The counter when the node was created.
	 */
	public Integer getCounter();
}
