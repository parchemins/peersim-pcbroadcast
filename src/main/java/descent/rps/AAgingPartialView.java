package descent.rps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import peersim.core.Node;

public abstract class AAgingPartialView extends PartialView implements IAgingPartialView {

	public Map<Node, Integer> ages;

	public AAgingPartialView() {
		super();
		this.ages = new HashMap<Node, Integer>();
	}

	public void incrementAge() {
		for (Integer age : this.ages.values())
			++age;
	}

	public Node getOldest() {
		Integer age = 0;
		ArrayList<Node> possibleResults = new ArrayList<Node>();
		for (Entry<Node, Integer> e : this.ages.entrySet()) {
			if (age < e.getValue()) {
				age = e.getValue();
				possibleResults = new ArrayList<Node>();
			}
			if (age == e.getValue()) {
				possibleResults.add(e.getKey());
			}
		}
		Node node = possibleResults.get((int) Math.floor(Math.random() * possibleResults.size()));
		return node;
	}

	public abstract List<Node> getSample(Node caller, Node neighbor, boolean isInitiator);

	@Override
	public boolean removeNeighbor(Node peer) {
		boolean hasRemoved = this.partialView.contains(peer);
		this.partialView.remove(peer, 1);
		if (!this.partialView.contains(peer))
			this.ages.remove(peer);
		return hasRemoved;
	}

	public abstract void mergeSample(Node me, Node other, List<Node> newSample, List<Node> oldSample,
			boolean isInitiator);

	@Override
	public abstract boolean addNeighbor(Node peer);

	public void clear() {
		super.clear();
		this.ages.clear();
	}

}
