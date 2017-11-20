package descent.broadcast.causal.flood;

import descent.rps.IMessage;
import peersim.core.Node;

/**
 * Message sent to acknowledge the receipt of a lock message.
 */
public class MUnlockBroadcast implements IMessage {

	public final Node from;
	public final Node to;

	public MUnlockBroadcast(Node from, Node to) {
		this.from = from;
		this.to = to;
	}

	public Node getPayload() {
		return this.to;
	}

}
