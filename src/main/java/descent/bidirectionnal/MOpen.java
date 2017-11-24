package descent.bidirectionnal;

import peersim.core.Node;

/**
 * Message sent by rps to notify the opening of a new communication channel.
 *
 */
public class MOpen {

	public final Node to;
	public final Node mediator;

	public MOpen(Node to, Node mediator) {
		this.to = to;
		this.mediator = mediator;
	}

}
