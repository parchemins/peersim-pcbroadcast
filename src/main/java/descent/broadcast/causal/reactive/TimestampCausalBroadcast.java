package descent.broadcast.causal.reactive;

import java.util.HashSet;

import descent.broadcast.reliable.MReliableBroadcast;
import descent.broadcast.reliable.ReliableBroadcast;
import descent.rps.IMessage;

/**
 * Reliable broadcast that ensures causal order on message deliveries. It uses
 * vector clocks.
 */
public class TimestampCausalBroadcast extends ReliableBroadcast {

	public HashSet<MTimestamp> buffer;
	public VV vector;

	public Integer causalityCounter;

	public TimestampCausalBroadcast(String prefix) {
		super(prefix);
		this.buffer = new HashSet<MTimestamp>();
		this.vector = new VV();
		this.causalityCounter = 0;
	}

	public TimestampCausalBroadcast() {
		super();
		this.buffer = new HashSet<MTimestamp>();
		this.vector = new VV();
		this.causalityCounter = 0;
	}

	/**
	 * Broadcast a message with causal order metadata.
	 * 
	 * @param m
	 */
	public void cBroadcast(IMessage m) {
		this.vector.add(this.node.getID(), this.causalityCounter);
		MTCB mtcb = new MTCB(this.vector.clone(), m);
		this.rBroadcast(mtcb);
	}

	@Override
	public void rDeliver(MReliableBroadcast m) {
		if (m.getPayload() instanceof MTimestamp) {
			this.buffer.add((MTimestamp) m.getPayload());
			this.checkBuffer();
		}
	}

	public void cDeliver(IMessage m) {
		// nothing yet
	}

	/**
	 * Checks if messages in buffer are ready. If so, deliver them.
	 */
	public void checkBuffer() {
		for (MTimestamp mt : this.buffer) {
			try {
				if (this.vector.isReady(mt.stamp)) {
					this.buffer.remove(mt);
					this.vector.merge(mt.stamp);
					this.cDeliver(mt.message);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
