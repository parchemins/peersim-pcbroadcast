package descent.bidirectionnal;

import peersim.core.Node;

/**
 * Message sent by rps to other listeners to notify that a link has been closed.
 */
public class MClose {

	public final Node to;

	public MClose(Node to) {
		this.to = to;
	};
}
