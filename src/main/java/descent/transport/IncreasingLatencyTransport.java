package descent.transport;

import descent.broadcast.causal.preventive.PreventiveCausalBroadcast;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;

public class IncreasingLatencyTransport implements Transport {

	private static final String PAR_MIN = "min";
	private static Integer min;

	private static final String PAR_INC = "inc";
	private static Integer inc;

	private static final String PAR_FROM = "from";
	private static long from;

	private static final String PAR_STEP = "step";
	private static long step;

	public IncreasingLatencyTransport(String prefix) {
		IncreasingLatencyTransport.min = Configuration.getInt(prefix + "." + IncreasingLatencyTransport.PAR_MIN, 0);
		IncreasingLatencyTransport.inc = Configuration.getInt(prefix + "." + IncreasingLatencyTransport.PAR_INC, 0);
		IncreasingLatencyTransport.from = Configuration.getLong(prefix + "." + IncreasingLatencyTransport.PAR_FROM, 0);
		IncreasingLatencyTransport.step = Configuration.getLong(prefix + "." + IncreasingLatencyTransport.PAR_STEP, 1);
	}

	public long getLatency(Node src, Node dest) {
		long nbStep = (CommonState.getTime() - IncreasingLatencyTransport.from) / IncreasingLatencyTransport.step;
		if (nbStep > 0) {
			return nbStep * IncreasingLatencyTransport.inc + IncreasingLatencyTransport.min;
		} else {
			return 0;
		}
	}

	public void send(Node src, Node dest, Object msg, int pid) {
		EDSimulator.add(getLatency(src, dest), msg, dest, pid);
	}

	public Object clone() {
		return this;
	}

}
