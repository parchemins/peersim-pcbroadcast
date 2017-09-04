package descent.causalbroadcast.rps;

import descent.causalbroadcast.IMBroadcast;
import descent.rps.IMessage;
import peersim.core.Node;

/**
 * Regular message sent by the broadcast messenger. It carries a message to
 * deliver to the application.
 */
public class MRegularBroadcast implements IMessage, IMBroadcast {

	private final IMessage payload;

	public MRegularBroadcast(IMessage message) {
		this.payload = message;
	}

	public Object getPayload() {
		return this.payload;
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
