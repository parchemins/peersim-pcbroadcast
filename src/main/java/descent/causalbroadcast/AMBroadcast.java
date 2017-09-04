package descent.causalbroadcast;

import descent.causalbroadcast.rps.MIdentifier;
import descent.rps.IMessage;

/**
 * Abstract class of message. For now, it contains a unique identifier composed
 * of the node that created the original message, and a counter which is the
 * number of message previously created by this node + 1.
 *
 */
public abstract class AMBroadcast implements IMessage {

	public final MIdentifier id;

	public AMBroadcast(MIdentifier id) {
		this.id = id;
	}

	public abstract Object getPayload();
}
