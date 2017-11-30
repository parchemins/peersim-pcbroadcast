package descent.broadcast.causal.preventive;

import descent.rps.IMessage;
import peersim.core.Node;

/**
 * Forward the message to its destination to.
 */
public class MForward implements IMessage {

	public final Node to;
	public final IMessage message;

	public MForward(Node to, IMessage message) {
		this.to = to;
		this.message = message;
	}

	public IMessage getPayload() {
		return this.message;
	}

}
