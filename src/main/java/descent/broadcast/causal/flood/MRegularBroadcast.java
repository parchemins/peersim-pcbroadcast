package descent.broadcast.causal.flood;

import descent.rps.IMessage;

/**
 * Regular message sent by the broadcast messenger. It carries a message to
 * deliver to the application.
 */
public class MRegularBroadcast implements IMessage {

	private final IMessage payload;

	public MRegularBroadcast(IMessage message) {
		this.payload = message;
	}

	public IMessage getPayload() {
		return this.payload;
	}
}
