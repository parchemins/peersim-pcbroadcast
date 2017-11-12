package descent.bidirectionnal;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

import descent.spray.Spray;
import peersim.core.Node;

/**
 * Spray with bidirectional communication links.
 */
public class BiSpray extends Spray {

	public Bag<Node> inview;

	public BiSpray(String prefix) {
		super(prefix);
		this.inview = new HashBag<Node>();
	}

	public BiSpray() {
		super();
		this.inview = new HashBag<Node>();
	}

	@Override
	public boolean addNeighbor(Node neighbor) {
		BiSpray bs = (BiSpray) neighbor.getProtocol(Spray.pid);
		bs.inview.add(this.node);
		return super.addNeighbor(neighbor);
	}
	
	

}
