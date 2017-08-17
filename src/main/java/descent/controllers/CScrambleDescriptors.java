package descent.controllers;

import java.util.ArrayList;
import java.util.Collections;

import descent.tman.IDescriptor;
import descent.tman.TMan;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;

/**
 * Collects descriptor from peers and redistributes them
 */
public class CScrambleDescriptors implements Control {

	private static final String PAR_DATE = "date";
	private final int DATE;

	public CScrambleDescriptors(String n) {
		this.DATE = Configuration.getInt(n + "." + CScrambleDescriptors.PAR_DATE);
	}

	public boolean execute() {
		if (CommonState.getTime() == this.DATE) {
			ArrayList<IDescriptor> descriptors = new ArrayList<IDescriptor>();
			for (int i = 0; i < Network.size(); ++i) {
				TMan tman = (TMan) (Network.get(i).getProtocol(TMan.pid));
				descriptors.add(tman.descriptor);
			}
			Collections.shuffle(descriptors);
			for (int i = 0; i < Network.size(); ++i) {
				TMan tman = (TMan) (Network.get(i).getProtocol(TMan.pid));
				tman.descriptor = descriptors.get(i);
			}
		}

		return false;
	}

}
