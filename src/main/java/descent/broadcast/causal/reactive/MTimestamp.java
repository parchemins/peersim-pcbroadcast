package descent.broadcast.causal.reactive;

import descent.rps.IMessage;

/**
 * Message produced and consumed by Timestamp causal broadcast.
 */
public class MTimestamp implements IMessage {

	public final VV stamp;
	public final IMessage message;

	public MTimestamp(VV stamp, IMessage message) {
		this.stamp = stamp;
		this.message = message;
	}

	public Object getPayload() {
		return this.message;
	}

}
