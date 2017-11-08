package descent.broadcast.reliable;

import descent.rps.IMessage;

/**
 * Message produced and consumed by reliable broadcast.
 */
public class MReliableBroadcast {

	public final Long id;
	public final Integer counter;
	public final IMessage message;

	public MReliableBroadcast(Long id, Integer counter, IMessage message) {
		this.id = id;
		this.counter = counter;
		this.message = message;
	}

}
