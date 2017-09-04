package descent.causalbroadcast.rps;

import descent.causalbroadcast.AMBroadcast;
import descent.rps.IMessage;
import peersim.core.Node;

/**
 * A message sent by the broadcaster to let a specific peer knows that a channel
 * is locked and that it requires its acknowledgment to unlock it.
 */
public class MLockedBroadcast extends AMBroadcast implements IMessage {

	private final Node from;
	private final Node to;

	public MLockedBroadcast(MIdentifier id, Node from, Node to) {
		super(id);
		this.from = from;
		this.to = to;
	}

	public ArcPair getPayload() {
		return new ArcPair(from, to);
	}
}
