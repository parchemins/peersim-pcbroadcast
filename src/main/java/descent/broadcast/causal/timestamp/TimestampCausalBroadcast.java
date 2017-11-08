package descent.broadcast.causal.timestamp;

import java.util.ArrayList;

import descent.broadcast.reliable.MReliableBroadcast;
import descent.broadcast.reliable.ReliableBroadcast;
import descent.broadcast.reliable.VectorClock;
import descent.rps.IMessage;

/**
 * Reliable broadcast that ensures causal order on message deliveries. It uses
 * vector clocks.
 */
public class TimestampCausalBroadcast extends ReliableBroadcast {

	private static String PAR_PID = "pid";
	private static int PID;

	private ArrayList<MReliableBroadcast> buffer;
	
	public TimestampCausalBroadcast(String prefix) {
		super(prefix);
	}

	@Override
	public void rBroadcast(IMessage m) {
		// TODO Auto-generated method stub
		super.rBroadcast(m);
	}
	
	/**
	 * Broadcast a message with causal order metadata.
	 * 
	 * @param m
	 */
	public void cBroadcast(IMessage m) {
		VectorClock vectorToSend = this.received.clone();
		vectorToSend.add(this.node.getID(), this.counterbis);
		MTCB mtcb = new MTCB(vectorToSend, m);
		this.rBroadcast(mtcb);
	}

	@Override
	public void rDeliver(IMessage m) {
		// TODO Auto-generated method stub
		super.rDeliver(m);
	}	
}
