package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.GadgetAvailableEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
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
			/**
			 * When GadgetAvailableEvent is received, the function calls the complete function that
			 * gets the item if exists.It also adds the QTime o the report.
			 */
			public void call(GadgetAvailableEvent c) {
				// update the report with its information
				c.getReport().setQTime(Qtick);
				complete(c, inventory.getItem(c.getGadget()));
			}
		});
		subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
			@Override
			/**
			 * When TickBroadcast is received, the function updates the Qtick
			 */
			public void call(TickBroadcast c) {
				Qtick = c.getTick();
			}
		});
		subscribeBroadcast(TerminateBroadcast.class, new Callback<TerminateBroadcast>() {
			@Override
			/**
			 * when TerminateBroadcast is received, the function calls the terminate function
			 */
			public void call(TerminateBroadcast c) {
				terminate();
			}
		});
	}
}
