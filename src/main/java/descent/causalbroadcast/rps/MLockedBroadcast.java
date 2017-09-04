package descent.causalbroadcast.rps;

import descent.causalbroadcast.IMBroadcast;
import descent.rps.IMessage;
import peersim.core.Node;

/**
 * A message sent by the broadcaster to let a specific peer knows that a channel
 * is locked and that it requires its acknowledgment to unlock it.
 */
public class MLockedBroadcast implements IMessage, IMBroadcast {

	private final Node from;
	private final Node to;

	public MLockedBroadcast(Node from, Node to) {
		this.from = from;
		this.to = to;
	}

	public Object getPayload() {
		return new ArcPair(from, to);
	}

	public Node getOrigin() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getCounter() {
		// TODO Auto-generated method stub
		return null;
	}

}
