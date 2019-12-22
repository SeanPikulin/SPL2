package bgu.spl.mics.application.publishers;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Publisher;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * TimeService is the global system timer There is only one instance of this Publisher.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other subscribers about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends Publisher {
	private int duration;
	private boolean terminated;
	private int counter;

	public TimeService(int duration) {
		super("Time_Service");
		this.duration = duration;
		counter = 1;
	}


	@Override
	protected void initialize() {
		this.terminated = false;
	}

	@Override
	public void run() { // check Timer
		initialize();
		while (!terminated) {
			Broadcast broadcast = new TickBroadcast(counter);
			getSimplePublisher().sendBroadcast(broadcast);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			counter++;
			if (counter == duration + 1)
				terminated = true;
		}
	}

}
