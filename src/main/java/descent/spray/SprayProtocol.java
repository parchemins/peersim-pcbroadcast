package descent.spray;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.Linkable;
import peersim.edsim.EDProtocol;
import descent.PeerSamplingService;

/**
 * THIS IMPLEMENTATION IGNORES THE
 *
 * Created by julian on 2/5/15.
 */
public abstract class SprayProtocol implements Linkable, EDProtocol,
		CDProtocol, PeerSamplingService {

	public static int c, tid, pid;

	// ============================================
	// E N T I T Y
	// ============================================

	private static final String PAR_C = "c";
	public static final String SCAMPLON_PROT = "0";
	private static final String PAR_TRANSPORT = "transport";

	public SprayProtocol(String n) {
		c = Configuration.getInt(n + "." + PAR_C, 0);
		tid = Configuration.getPid(n + "." + PAR_TRANSPORT);
		pid = Configuration.lookupPid(SCAMPLON_PROT);
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	// ============================================
	// P U B L I C
	// ============================================

	public void pack() {

	}

	public void onKill() {

	}

}