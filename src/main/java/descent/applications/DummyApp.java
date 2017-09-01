package descent.applications;

/**
 * Dummy app doing nothing on message delivery.
 */
public class DummyApp implements IApplication{

	public void deliver(Object message) {
		// nothing
	}

}
