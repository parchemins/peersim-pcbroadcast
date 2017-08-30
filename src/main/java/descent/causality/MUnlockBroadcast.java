package descent.causality;

import descent.rps.IMessage;
import peersim.core.Node;

/**
 * Message sent to acknowledge the receipt of a lock message.
 */
public class MUnlockBroadcast implements IMessage {

	private final Node to;

	public MUnlockBroadcast(Node to) {
		this.to = to;
	}

	public Object getPayload() {
		return this.to;
	}

}
