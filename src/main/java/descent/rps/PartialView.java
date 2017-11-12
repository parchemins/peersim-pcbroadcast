package descent.rps;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

import peersim.core.CommonState;
import peersim.core.Node;

/**
 * Implementation of a basic partial view.
 */
public class PartialView implements IPartialView {

	public Bag<Node> partialView;

	public PartialView() {
		this.partialView = new HashBag<Node>();
	}

	public Iterable<Node> getPeers() {
		return new HashBag<Node>(this.partialView);
	}

	public Iterable<Node> getPeers(int k) {
		if (this.partialView.size() == k || k == Integer.MAX_VALUE) {
			return this.getPeers();
		} else {
			HashBag<Node> sample = new HashBag<Node>();
			ArrayList<Node> clone = new ArrayList<Node>(this.partialView);
			while (sample.size() < Math.min(k, this.partialView.size())) {
				int rn = CommonState.r.nextInt(clone.size());
				sample.add(clone.get(rn));
				clone.remove(rn);
			}
			return sample;
		}
	}

	public boolean removeNode(Node peer) {
		return this.partialView.remove(peer);
	}

	public boolean addNeighbor(Node peer) {
		return this.partialView.add(peer);
	}

	public boolean contains(Node peer) {
		return this.partialView.contains(peer);
	}

	public int size() {
		return this.partialView.size();
	}

	public void clear() {
		this.partialView.clear();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		PartialView pv = new PartialView();
		pv.partialView = new HashBag<Node>(this.partialView);
		return pv;
	}
}
