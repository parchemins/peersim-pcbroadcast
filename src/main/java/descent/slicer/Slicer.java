package descent.slicer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.IteratorUtils;

import descent.rps.IPeerSampling;
import descent.tman.TMan;
import descent.tman.TManPartialView;
import itc.Stamp;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;

/**
 * Overlay network building (i) slices depending on the rank and (ii) providing
 * the right to stamp to a portion of slices |P|/X.
 */
public class Slicer extends TMan {

	private static boolean SWAP = true;
	private static final String PAR_X = "x";
	protected static Double X = 3.;

	private Integer age = 0;

	public Stamp stamp;
	public boolean isWriter = false;

	public Slicer(String prefix) {
		super(prefix);
		this.descriptor = new RankDescriptor();
		Slicer.X = Configuration.getDouble(prefix + "." + Slicer.PAR_X);
	}

	public Slicer() {
		super();
		this.descriptor = new RankDescriptor();
	}

	@Override
	public void onSubscription(Node origin) {
		if (IteratorUtils.toList(((IPeerSampling) this.node.getProtocol(this.rps)).getPeers().iterator()).isEmpty()) {
			((RankDescriptor) this.descriptor).setRank(0);
			this.stamp = new Stamp();
			this.isWriter = true;
		}

		super.onSubscription(origin);
	}

	public void periodicCall() {
		this.age += 1;
		if (CommonState.getTime() == 100) {
			// (TODO) create a controller doing that
			Double rn = Math.abs(CommonState.r.nextGaussian()) * 5.;
			((RankDescriptor) this.descriptor).setFrequency(rn);
		}

		// #1 initialize descriptor based on Spray
		List<Node> randomNeighbors = IteratorUtils
				.toList(((IPeerSampling) this.node.getProtocol(this.rps)).getPeers().iterator());

		if (this.age >= Math.max(randomNeighbors.size(), 6) && !((RankDescriptor) this.descriptor).isSet()) {
			((RankDescriptor) this.descriptor).setRank((int) Math.floor(randomNeighbors.size()));
		}

		// #2 see if a swap of rank is needed
		// (TODO) move this to be more generic, i.e. should not be in slicer
		if (Slicer.SWAP) {
			ArrayList<Node> toExamine = new ArrayList<Node>(this.partialView);
			toExamine.addAll(randomNeighbors);

			// #A farthest frequency
			RankDescriptor thisDescriptor = (RankDescriptor) this.descriptor;
			Double maxDistance = 0.;
			Node toSwap = null;

			for (Node node : toExamine) {
				Slicer slicerNode = (Slicer) node.getProtocol(Slicer.pid);
				RankDescriptor otherDescriptor = (RankDescriptor) slicerNode.descriptor;
				Double currentDistance = thisDescriptor.distanceFrequency(otherDescriptor);
				if (maxDistance < currentDistance
						&& (thisDescriptor.frequency > otherDescriptor.frequency
								&& thisDescriptor.rank > otherDescriptor.rank)
						|| (thisDescriptor.frequency < otherDescriptor.frequency
								&& thisDescriptor.rank < otherDescriptor.rank)) {
					maxDistance = currentDistance;
					toSwap = node;
				}
			}

			// #B swap
			if (toSwap != null) {
				Slicer toSwapSlicer = (Slicer) toSwap.getProtocol(Slicer.pid);

				Integer r = ((RankDescriptor) toSwapSlicer.descriptor).rank;
				((RankDescriptor) toSwapSlicer.descriptor).setRank(((RankDescriptor) this.descriptor).rank);
				((RankDescriptor) this.descriptor).setRank(r);
			}
		}

		// #3 exchange views
		super.periodicCall();

		// #4 getting a stamp
		if (((RankDescriptor) this.descriptor).rank <= randomNeighbors.size() / Slicer.X) {
			boolean found = false;
			ArrayList<Node> candidates = new ArrayList<Node>(this.partialView);
			candidates.addAll(randomNeighbors);
			Integer i = 0;
			while (!found && i < candidates.size()) {
				Node node = candidates.get(i);
				Slicer slicer = (Slicer) node.getProtocol(Slicer.pid);
				RankDescriptor slicerDescriptor = (RankDescriptor) slicer.descriptor;
				if (slicerDescriptor.rank < ((RankDescriptor) this.descriptor).rank && slicer.isWriter) {
					found = true;
				}
				++i;
			}
		}
	}

	@Override
	public IPeerSampling clone() {
		TMan slicerClone = new Slicer();
		slicerClone.partialView = (TManPartialView) this.partialView.clone();
		slicerClone.descriptor = new RankDescriptor();
		return slicerClone;
	}

}
