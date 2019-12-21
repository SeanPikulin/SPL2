package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.GadgetAvailableEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;

/**
 * Q is the only Subscriber\Publisher that has access to the {@link bgu.spl.mics.application.passiveObjects.Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Q extends Subscriber {
	private Inventory inventory;
	private int Qtick;

	public Q() {
		super("Q");
		inventory = Inventory.getInstance();
	}

	@Override
	protected void initialize() {
		getBroker().register(this);
		subscribeEvent(GadgetAvailableEvent.class, new Callback<GadgetAvailableEvent>() {
			@Override
			public void call(GadgetAvailableEvent c) {
				c.getReport().setQTime(Qtick);
				complete(c, inventory.getItem(c.getGadget()));
			}
		});
		subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
			@Override
			public void call(TickBroadcast c) {
				Qtick = c.getTick();
			}
		});

	}
}
