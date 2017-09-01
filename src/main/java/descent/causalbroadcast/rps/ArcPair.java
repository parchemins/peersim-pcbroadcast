package descent.causalbroadcast.rps;

import peersim.core.Node;

/**
 * A pair representing an arc in the network, or a graph.
 */
public class ArcPair {
	private final Node from;
	private final Node to;

	public ArcPair(Node from, Node to) {
		this.from = from;
		this.to = to;
	}

	public Node getFrom() {
		return from;
	}

	public Node getTo() {
		return to;
	}

}
