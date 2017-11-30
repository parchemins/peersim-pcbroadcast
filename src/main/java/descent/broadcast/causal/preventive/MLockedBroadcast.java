package descent.broadcast.causal.preventive;

import descent.rps.IMessage;
import peersim.core.Node;

/**
 * A message sent by the broadcaster to let a specific peer knows that a channel
 * is locked and that it requires its acknowledgment to unlock it.
 */
public class MLockedBroadcast implements IMessage {

	public final Node from;
	public final Node to;

	public MLockedBroadcast(Node from, Node to) {
		this.from = from;
		this.to = to;
	}

	public Node getPayload() {
		return this.from;
	}
}
