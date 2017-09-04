package descent.causalbroadcast.rps;

import descent.causalbroadcast.AMBroadcast;
import descent.rps.IMessage;

/**
 * Regular message sent by the broadcast messenger. It carries a message to
 * deliver to the application.
 */
public class MRegularBroadcast extends AMBroadcast implements IMessage {

	private final IMessage payload;

	public MRegularBroadcast(MIdentifier id, IMessage message) {
		super(id);
		this.payload = message;
	}

	public IMessage getPayload() {
		return this.payload;
	}
}
