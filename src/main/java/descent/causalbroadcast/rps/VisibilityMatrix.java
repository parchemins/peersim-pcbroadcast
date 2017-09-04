package descent.causalbroadcast.rps;

import descent.causalbroadcast.AMBroadcast;
import peersim.core.Node;

/**
 * A square static matrix which keeps track of the last message received by each
 * peer.
 */
public class VisibilityMatrix {

	public static Integer[][] matrix = null;

	public VisibilityMatrix(Integer size) {
		// lazy initialization
		if (VisibilityMatrix.matrix == null) {
			matrix = new Integer[size][size];
			for (int i = 0; i < size; ++i) {
				for (int j = 0; j < size; ++j)
					VisibilityMatrix.matrix[i][j] = 0;
			}
		}
	}

	/**
	 * Increment the counter of node n when it sends an original message.
	 * 
	 * @param n
	 *            The node that sends the message.
	 */
	public static void increment(Node n) {
		VisibilityMatrix.matrix[(int) n.getID()][(int) n.getID()] += 1;
	}

	/**
	 * Increment on received message.
	 * 
	 * @param incrementer
	 *            The node that increments the message.
	 * @param m
	 *            The received message.
	 */
	public static void incrementFrom(Node incrementer, AMBroadcast m) {
		VisibilityMatrix.matrix[(int) incrementer.getID()][(int) m.id.origin.getID()] = Math
				.max(VisibilityMatrix.matrix[(int) incrementer.getID()][(int) m.id.origin.getID()], m.id.counter);
	}

	/**
	 * Checks if the message has already been received by the checker node.
	 * 
	 * @param checker
	 *            The node that checks the receipt of the message.
	 * @param m
	 *            The message received.
	 */
	public static boolean alreadyReceived(Node checker, AMBroadcast m) {
		return VisibilityMatrix.matrix[(int) checker.getID()][(int) m.id.origin.getID()] <= m.id.counter;
	}

	/**
	 * Get an identifier to assign it to a message.
	 * 
	 * @param n
	 *            The node that wants an id.
	 * @return The generated identifier.
	 */
	public static MIdentifier get(Node n) {
		VisibilityMatrix.increment(n);
		return new MIdentifier(n, VisibilityMatrix.matrix[(int) n.getID()][(int) n.getID()]);
	}

}
