package descent.causalbroadcast.rps;

import descent.applications.DummyApp;
import descent.rps.IMessage;
import descent.rps.IPeerSampling;
import descent.spray.Spray;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.Node;
import peersim.edsim.EDProtocol;

/**
 * Protocol that uses a random peer-sampling protocol to build the network
 * overlay and uses a causal broadcast to transmit messages to all the peers.
 */
public class RPSCBProtocol implements EDProtocol, CDProtocol {

	// #1 peersim parameters
	private static final String PAR_PROTOCOL = "protocol";
	public static int pid;
	
	private static final String PAR_PS = "ps";
	public static int ps;
	
	// #2 local parameters
	public CausalBroadcast cb = null;
	

	public RPSCBProtocol(String prefix) {
		RPSCBProtocol.pid = Configuration.getPid(prefix +"."+ RPSCBProtocol.PAR_PROTOCOL);
		RPSCBProtocol.ps = Configuration.getPid(prefix +"."+ RPSCBProtocol.PAR_PS);
	}

	public RPSCBProtocol() {
	}
	
	/**
	 * @param node
	 *            The receiving node.
	 * @param pid
	 *            The protocol id of the receiving node
	 * @param event
	 *            The event raised, ie, here the received message.
	 */
	public void processEvent(Node node, int pid, Object event) {		
		if (this.cb == null)
			this.cb = new CausalBroadcast(node, (IPeerSampling) node.getProtocol(FastConfig.getLinkable(pid)) , new DummyApp(this.cb));
		
		if (event instanceof MLockedBroadcast || event instanceof MRegularBroadcast
				|| event instanceof MUnlockBroadcast)
			this.cb.receive((IMessage) event);
	}

	
	@Override
	public RPSCBProtocol clone(){
		RPSCBProtocol cloneProtocol = new RPSCBProtocol();
		cloneProtocol.cb = this.cb;
		return cloneProtocol;
	}

	public void nextCycle(Node arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}
