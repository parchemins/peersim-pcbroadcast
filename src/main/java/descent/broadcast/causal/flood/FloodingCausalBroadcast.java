package descent.broadcast.causal.flood;

import java.util.ArrayList;
import java.util.HashMap;

import descent.broadcast.reliable.ReliableBroadcast;
import descent.rps.IMessage;
import peersim.core.Node;

public class FloodingCausalBroadcast extends ReliableBroadcast {

	HashMap<Node, ArrayList<IMessage>> buffers;

	// (TODO) retries number and counters as id

	public FloodingCausalBroadcast(String prefix) {
		super(prefix);
		this.buffers = new HashMap<Node, ArrayList<IMessage>>();
	}

	public FloodingCausalBroadcast() {
		super();
		this.buffers = new HashMap<Node, ArrayList<IMessage>>();
	}

}
