package descent.broadcast.causal.timestamp;

import descent.broadcast.reliable.VectorClock;
import descent.rps.IMessage;

/**
 * Message produced and consumed by Causal broadcast using vector clock.
 *
 */
public class MTCB implements IMessage {

	public final VectorClock vector;
	private final IMessage message;

	public MTCB(VectorClock vector, IMessage message) {
		this.vector = vector;
		this.message = message;
	}

	public Object getPayload() {
		return this.message;
	}

}
