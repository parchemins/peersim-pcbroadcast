package descent.bidirectionnal;

import peersim.core.Node;

/**
 * Message sent by rps to notify the opening of a new communication channel.
 *
 */
public class MOpen {

	public final Node to;

	public MOpen(Node to) {
		this.to = to;
	}

}
