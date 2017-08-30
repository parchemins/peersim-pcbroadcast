package descent.causality;

import descent.rps.IMessage;
import peersim.core.Node;

/**
 * A message sent by the broadcaster to let a specific peer knows that a channel
 * is locked and that it requires its acknowledgment to unlock it.
 */
public class MLockedBroadcast implements IMessage {

	private final Node from;
	private final Node to;

	public MLockedBroadcast(Node from, Node to) {
		this.from = from;
		this.to = to;
	}

	public Object getPayload() {
		return new ArcPair(from, to);
	}

}
