package descent.causalbroadcast.rps;

import peersim.core.Node;

/**
 * It is not a message but an identifier of messages.
 */
public class MIdentifier {

	public final Node origin;
	public final Integer counter;

	public MIdentifier(Node origin, Integer counter) {
		this.origin = origin;
		this.counter = counter;
	}

}
